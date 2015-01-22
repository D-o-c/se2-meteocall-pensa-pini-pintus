package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.primarykeys.ContactPK;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 */
public class UserManager {
    
    @PersistenceContext
    EntityManager em;
    
    /**
     * If public then private and viceversa
     * @param user 
     */
    public void changeCalendarVisibility(User user){
        boolean temp = user.isPublic();
        temp ^= true;
        user.setPublic(temp);
        em.merge(user);
    }
    
    /**
     * Update password and merge user
     * @param user
     * @param password 
     */
    public void changePassword(User user, String password){
        user.setPassword(password);
        em.merge(user);
    }
    
    /**
     * @param user
     * @return calendar of a user
     */
    public ScheduleModel getCalendar(User user) {
        
        ScheduleModel calendar = new DefaultScheduleModel();
        
        try {
            
            List<Calendar> userEvents = user.getEvents();
            for(Calendar c : userEvents) {
                int inviteStatus = c.getInviteStatus();
                if(inviteStatus == 1) {
                    Event e = c.getEvent();
                    DefaultScheduleEvent dse = new DefaultScheduleEvent(e.getName(),
                                                                        e.getBeginTime(),
                                                                        e.getEndTime());
                    //DefaultScheduleEvent.setData() is used to set the ID
                    dse.setData(e.getEventId());
                    calendar.addEvent(dse);
                    
                }//endif
                
            }//endfor
            
        } catch (Exception e) {}
        
        return calendar;
    }
    
    /**
     * @param user
     * @param event
     * @return
     * <br/>0 no problems
     * <br/>-1 begin time is after end time
     * <br/>-2 user has another event at the same time
     */
    public int timeConsistency(User user, Event event){
        
        try {
            if (event.getBeginTime().after(event.getEndTime())){
                return -1;
            }

            List<Calendar> calendars = user.getEvents();
            for (Calendar c : calendars) {
                if (event.getBeginTime().before(c.getEvent().getEndTime()) &&
                    event.getEndTime().after(c.getEvent().getBeginTime()) &&
                    event.getEventId() != c.getEventId() &&
                    c.getInviteStatus() == 1) {
                    return -2;
                }
            }
        }
        catch(NullPointerException e){}
        return 0;
    }

    /**
     * @param user
     * @return all events of a user
     */
    public List<Event> getUserEvent(User user) {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getInviteStatus() == 1){
                events.add(user.getEvents().get(i).getEvent());
            }
        }
        return events;
    }
    
    /**
     * @param user
     * @return all pending invites of a user 
     */
    public List<Event> getInvites(User user) {
        List<Event> temp = new ArrayList<>();
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getInviteStatus() == 0){
                temp.add(user.getEvents().get(i).getEvent());
            }
        }
        return temp;
    }

    /**
     * 
     * @param user
     * @param e
     * @return 
     */
    public int importCalendar(User user, HashSet<Event> e) {
        int status = 0;
        for (int i=0;i<user.getEvents().size();i++){
            if (!user.getEvents().get(i).getEvent().getCreator().equals(user)){
                user.getEvents().get(i).setInviteStatus(-1);
            }
        }
        List<Event> events = new ArrayList<>(e);
        
        for (Event event : events) {
            if (this.timeConsistency(user, event)==0){
                for (int j = 0; j < event.getInvited().size(); j++) {
                    if (event.getInvited().get(j).getUser().equals(user)) {
                        event.getInvited().get(j).setInviteStatus(1);
                        
                    }
                }

                for (int i = 0; i < user.getEvents().size(); i++){
                    if (event.equals(user.getEvents().get(i).getEvent())){
                        user.getEvents().get(i).setInviteStatus(1);
                    }
                }
                em.merge(event);
                em.merge(user);
            }
            else{
                status = -2;
            }
        }
        
        return status;
    
    }

    /**
     * @param user
     * @return user.getNotifies()
     */
    public List<Update> getNotifies(User user) {
        return user.getNotifies();
    }

    /**
     * Updates "update" and merge "update"
     * @param update 
     */
    public void setNotifyRead(Update update) {
        update.setRead(true);
        em.merge(update);
    }
    
    /**
     * Updates all "update" of user and merge
     * @param user 
     */
    public void setAllNotifyRead(User user) {
        for(Update update : user.getNotifies()) {
            update.setRead(true);
            em.merge(update);
        }
        em.merge(user);
    }
    
    public boolean allRead(User user) {
        for(Update update : user.getNotifies()) {
            //one notify not read is enough to return false
            if(!update.isRead()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Accept invitation decision = 1</br>
     * Deny invitation decision = -1 </br>
     * @param user
     * @param selectedEvent 
     * @param decision
     */
    public void answerInvite(User user, Event selectedEvent, int decision) {
        
        List<Calendar> calendars = selectedEvent.getInvited();
        for(Calendar c : calendars) {
            if(c.getUser().equals(user)) {
                c.setInviteStatus(decision);
            }
        }
        
        calendars = user.getEvents();
        for(Calendar c : calendars) {
            if(c.getEvent().equals(selectedEvent)) {
                c.setInviteStatus(decision);
            }
        }
        
        em.merge(selectedEvent);
        em.merge(user);
    }
    
    /**
     * @param user
     * @return number of not read notifies
     */
    public int getNumberOfNotReadNotifies(User user) {
        return ((Number)em.createNamedQuery(Update.countNotRead)
                    .setParameter(1, user.getEmail()).getSingleResult())
                        .intValue();
    }

    /**
     * @param user
     * @return user.getContacts()
     */
    public List<Contact> getContacts(User user) {
        return user.getContacts();
    }

    /**
     * Removes a contact from user contact lists
     * @param user
     * @param contact 
     */
    public void deleteContact(User user, Contact contact) {
        ContactPK pk = new ContactPK(contact.getEmail(),user.getEmail());
        Contact toBeRemoved = em.find(Contact.class, pk);
        user.getContacts().remove(toBeRemoved);
        em.remove(toBeRemoved);
    }
    
}
