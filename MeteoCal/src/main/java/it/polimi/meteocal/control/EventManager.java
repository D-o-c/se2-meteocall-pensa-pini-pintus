package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.WeatherCondition;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

/**
 *
 */
public class EventManager {
    
    private static final String invite = "Invite";
    private static final String text =
            "You received an invitation to an event, log on MeteoCal to accept!";
    private static final String delete = "Event deleted";
    private static final String deleted = " has been deleted";
    
    @PersistenceContext
    EntityManager em;
    
    //Controls
    @Inject
    EmailSender emailSender;
    @Inject
    UpdateManager updateManager;
    @Inject
    WeatherManager weatherManager;

    /**
     * @param event
     * @param invitedUsers
     * @param creator
     * @return if all invitations have been sent correctly
     */
    public boolean createEvent(Event event, List<String> invitedUsers, User creator) {
        
        /*******NON SERVE TANTO DI DEFAULT ALLA CREAZIONE SONO = 0*************/
        //no notifies have been sent from the system(one day before)
        //event.setBwodb(false);
        //no notifies have been sent from the system(3 days before)
        //event.setBwtdb(false);
        
        //em.merge(event);
        
        event.setCreator(creator);
        
        em.persist(event);
        
        event.addInvited(creator, 1);
        
        weatherManager.searchWeather();
        
        return sendInvite(event,invitedUsers);
    }
    
    /**
     * Adds invites and sends emails
     * @param event
     * @param invitedEmails
     * @return true if all invitations have been sent correctly
     */
    private boolean sendInvite(Event event, List<String> invitedEmails){
        
        List<Calendar> calendars = event.getInvited();
        List<String> usersPartecEmail = new ArrayList<>();
        List<String> usersPendinEmail = new ArrayList<>();
        List<String> usersRefuseEmail = new ArrayList<>();
        for (Calendar c : calendars) {
            if(c.getInviteStatus() == 1) {
                usersPartecEmail.add(c.getUserEmail());
            }
            else if (c.getInviteStatus() == 0) {
                usersPendinEmail.add(c.getUserEmail());
            }
            else {
                usersRefuseEmail.add(c.getUserEmail());
            }
        }
        
        for(int i = 0; i < invitedEmails.size(); i++) {
            String s = invitedEmails.get(i);
            //remove emails of people partecipating to the event yet
            //(of course this removes also the creator)
            if(usersPartecEmail.contains(s)) {
                invitedEmails.remove(s);
                i--;
            }
            //remove emails of people who have not answered yet
            else if(usersPendinEmail.contains(s)) {
                invitedEmails.remove(s);
                i--;
            }
            //remove emails of people who refused the event
            else if(usersRefuseEmail.contains(s)) {
                invitedEmails.remove(s);
                i--;
            }                
        }   
        
        //NOW
        //invitedEmails contains only new emails
        //usersRefuseEmail contains emails of people who refused
        
        boolean allUsersInvited = true;
        
        //Managing new emails
        for (String pk : invitedEmails) {
            User u = em.find(User.class, pk);
            //if user exists
            if(u != null) {
                event.addInvited(u, 0);
                emailSender.send(u.getEmail(), invite, text);
            } 
            else {
                //at least one of the new emails do not exists
                allUsersInvited = false;
            }
        }
        
        //Managing users who refused (maybe for error)
        //They exist for sure
        //for each user email
        for (String email : usersRefuseEmail) {
            //for each calendar of the event
            for (Calendar c : event.getInvited()) {
                if (c.getUserEmail().equals(email)) {
                    //userManager will generate notify
                    c.setInviteStatus(0);
                    em.merge(c);
                    //em.merge(em.find(User.class, c.getUserEmail()));
                    emailSender.send(email, invite, text);
                }
            }
        }
        
        em.merge(event);
        
        return allUsersInvited;
        
    }

    /**
     * Calls updateManager.updateFromEventUpdate(currentEvent)
     * @param currEvent
     * @param tempEvent
     * @param invitedUsers
     * @return 0: if all invitations have been sent correctly<br/>
     *         -1: if not all invitations have been sent correctly<br/>
     *         -2: if you must retry
     */
    public int updateEvent(Event currEvent, Event tempEvent, List<String> invitedUsers) {
        
     //   em.setProperty("javax.persistence.lock.timeout", 2000);
    //    currentEvent = em.merge(currentEvent);
    //    currentEvent = em.find(Event.class, currentEvent.getEventId());
        Event currentEvent = em.merge(currEvent);
        Boolean cont = true;
        while(cont){
            try{
                em.lock(currentEvent, LockModeType.PESSIMISTIC_WRITE);
                cont=false;
            }
            catch(Exception e){
                return -2;
            }
        }
        currentEvent = tempEvent;
        //no notifies have been sent from the system(one day before)
        currentEvent.setBwodb(false);
        //no notifies have been sent from the system(3 days before)
        currentEvent.setBwtdb(false);
        
       
        
        //send invites
        boolean noErrors = sendInvite(currentEvent, invitedUsers);
        //removes weather conditions
        currentEvent.setWeatherConditions(new ArrayList<WeatherCondition>());
    //    em.persist(currentEvent);
        
        em.merge(currentEvent);
        updateManager.updateFromEventUpdate(currentEvent);
        
    //    em.lock(currentEvent, LockModeType.NONE);
        
        
        weatherManager.searchWeather();
        if (noErrors){
            return 0;
        }
        return -1;
    }

    /**
     * @param eventId
     * @return em.find(Event.class, eventId)
     */
    public Event find(long eventId) {
        return em.find(Event.class, eventId);
    }

    /**
     * 
     * @param event
     * @param user 
     */
    public void removeFromPartecipants(Event event, User user) {
        for (int i=0;i<event.getInvited().size();i++){
            if (event.getInvited().get(i).getUser().equals(user)){
                event.getInvited().get(i).setInviteStatus(-1);
            }
        }
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getEventId()==event.getEventId()){
                user.getEvents().get(i).setInviteStatus(-1);
            }
        }
        em.merge(event);
        em.merge(user);
    }

    /**
     * @param event
     * @return list of user who partecipates to an event
     */
    public List<User> getPartecipants(Event event) {
        List<User> partecipants = new ArrayList<>();
        for(Calendar c : event.getInvited()) {
            if(c.getInviteStatus() == 1) {
                partecipants.add(c.getUser());
            }
        }
        return partecipants;
    }

    /**
     * Removes an event
     * @param e 
     * @return  
     */
    public int deleteEvent(Event e) {
        Event event = em.merge(e);
        Boolean cont = true;
        while(cont){
            try{
                em.lock(event, LockModeType.PESSIMISTIC_WRITE);
                cont=false;
            }
            catch(Exception exc){
                return -2;
            }
        }
        
        for (Calendar calendar : event.getInvited()){
            if (calendar.getInviteStatus() == 1){
                emailSender.send(calendar.getUserEmail(),
                                delete, 
                                event.getName() + deleted);
            }
            
            User u = calendar.getUser();
            for (int i = 0; i < u.getEvents().size(); i++){
                if (u.getEvents().get(i).getEvent().equals(event)){
                    u.getEvents().remove(i);
                    i--;
                }
            }

            for (int i = 0; i < u.getNotifies().size(); i++){
                if (u.getNotifies().get(i).getEvent().equals(event)){
                    u.getNotifies().remove(i);
                    i--;
                }
            }
            
            em.merge(u);
        }
        
        
        em.remove(event);
        return 0;
    }
    
}
