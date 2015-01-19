package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.WeatherCondition;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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
    UpdateManager userManager;

    /**
     * @param event
     * @param invitedUsers
     * @param creator
     * @return if all invitations have been sent correctly
     */
    public boolean createEvent(Event event, List<String> invitedUsers, User creator) {
        //set flags to false
        event.setBwodb(false);
        event.setBwtdb(false);
        
        event.setCreator(creator);
        
        em.persist(event);
        
        event.addInvited(creator, 1);
        
        return sendInvite(event,invitedUsers);
    }
    
    /**
     * Adds invites and sends emails
     * @param event
     * @param invitedEmails
     * @return true if all invitations have been sent correctly
     */
    private boolean sendInvite(Event event, List<String> invitedEmails){
        //delete people already invited
        List<Calendar> calendars = event.getInvited();
        for (Calendar c : calendars) {
            invitedEmails.remove(c.getUserEmail());
        }
        
        //deletes the creator from users invited to the event
        invitedEmails.remove(event.getCreator().getEmail());
        
        //Checks the existance of the emails in the user database
        for (String s : invitedEmails) {
            User u = em.find(User.class, s);
            try {
                //if exists, add event to his calendar
                event.addInvited(u, 0);
                emailSender.send(s, invite, text);
            } catch (NullPointerException e){
                return false;
            }
        }
        return true;
    }

    /**
     * Calls userManager.updateFromEventUpdate(currentEvent)
     * @param currentEvent
     * @param invitedUsers
     * @return true if all invitations have been sent correctly
     */
    public boolean updateEvent(Event currentEvent, List<String> invitedUsers) {
        currentEvent.setBwodb(false);
        currentEvent.setBwtdb(false);
        em.merge(currentEvent);
        
        //send invites
        boolean noErrors = sendInvite(currentEvent, invitedUsers);
        //removes weather conditions
        currentEvent.setWeatherConditions(new ArrayList<WeatherCondition>());
        em.merge(currentEvent);
        
        userManager.updateFromEventUpdate(currentEvent);
        return noErrors;
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
     * @param event 
     */
    public void deleteEvent(Event event) {
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
        
        Event e = em.merge(event);
        em.remove(e);
    }
    
}
