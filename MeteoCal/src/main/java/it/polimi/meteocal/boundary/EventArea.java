package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.EmailSender;
import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author doc
 */
@Stateless
public class EventArea{
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    Event currentEvent;

    /**
     * Calls EntityManager.find(User.class, principal.getName())
     * @return the logger user
     */
    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    /**
     * @return List of contacts of the logged user
     */
    public List<Contact> getContacts() {
        return getLoggedUser().getContacts();
    }
    
    public Event getCurrentEvent() {
        return currentEvent;
    }
    
    public boolean isCreator(){
        return currentEvent.getCreator().getEmail().equals(this.getLoggedUser().getEmail());
    }
    
    /**
     * Adds creator user email to event
     * Calls EntityManager.persist(event)
     * Updates calendars of invited users
     * @param event
     * @param invitedUsers
     * @return if all invited users exist in the database
     */
    public boolean createEvent(Event event, List<String> invitedUsers) throws MessagingException {
        boolean noErrors = true;
        //Logged user is the creator of the event
        User creator = getLoggedUser();
        event.setCreator(creator);
        //persists the event into the database
        em.persist(event);
        
        //adds the event to creator's calendar
        event.addInvited(creator, 1);
        
        sendInvite(event,invitedUsers);
        
        
        return noErrors;
    }
    
    public void updateCurrentEvent(List<String> invitedUsers) throws MessagingException{
        sendInvite(currentEvent, invitedUsers);
        em.merge(currentEvent);
    }
    
    private boolean sendInvite(Event e, List<String> iu) throws MessagingException{
        //delete people already invited
        List<Calendar> temp = e.getInvited();
        for (Calendar temp1 : temp) {
            iu.remove(temp1.getUserEmail());
        }
        
        //deletes the creator from users invited to the event
        iu.remove(e.getCreator().getEmail());
        //Checks the existance of the emails in the user database
        for (String invitedUser : iu) {
            User u = em.find(User.class, invitedUser);
            try {
                //if exists, add event to his calendar
                e.addInvited(u, 0);
                EmailSender.send(invitedUser,"Invite",
                        "You received an invitation to an event, log on MeteoCal to accept!");
            } catch (NullPointerException exc){
                return false;
            }
            
        }
        return true;
        
    }
    /**
     * Finds all events
     * @return List of Events
     */
    public List<Event> findAll(){
        return em.createNamedQuery(Event.findAll, Event.class)
                                .getResultList();
    }

    
    public void setCurrentEvent(long eventId) {
        currentEvent = em.find(Event.class, eventId);
    }

    public Event findEvent(long id) {
        return em.find(Event.class, id);
    }

    /**
     * Remove current user from the list of partecipants of current event
     */
    public void removeFromPartecipants() {
         for (int i=0;i<currentEvent.getInvited().size();i++){
            if (currentEvent.getInvited().get(i).getUser().getEmail().equals(this.getLoggedUser().getEmail())){
                currentEvent.getInvited().get(i).setInviteStatus(-1);
            }
        }
        for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
            if (this.getLoggedUser().getEvents().get(i).getEventId()==currentEvent.getEventId()){
                this.getLoggedUser().getEvents().get(i).setInviteStatus(-1);
            }
        }
        em.merge(currentEvent);
        em.merge(this.getLoggedUser());
    }

    public List<User> getPartecipants() {
        List<User> temp = new ArrayList<>();
        for (int i = 0; i < currentEvent.getInvited().size(); i++){
            if (currentEvent.getInvited().get(i).getInviteStatus()==1){
                temp.add(currentEvent.getInvited().get(i).getUser());
            }
        }
        return temp;
    }

    
    
}
