package it.polimi.meteocal.boundary;

import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
    
    /**
     * Adds creator user email to event
     * Calls EntityManager.persist(event)
     * Updates calendars of invited users
     * @param event
     * @param invitedUsers
     * @return if all invited users exist in the database
     */
    public boolean createEvent(Event event, List<String> invitedUsers) {
        boolean noErrors = true;
        //Logged user is the creator of the event
        User creator = getLoggedUser();
        event.setCreator(creator);
        //persists the event into the database
        em.persist(event);
        
        //adds the event to creator's calendar
        event.addInvited(creator, 1);
        //deletes the creator from users invited to the event
        invitedUsers.remove(creator.getEmail());
        
        //Checks the existance of the emails in the user database
        for (String invitedUser : invitedUsers) {
            User u = em.find(User.class, invitedUser);
            try {
                //if exists, add event to his calendar
                event.addInvited(u, 0);
            } catch (NullPointerException e){
                noErrors = false;
            }
            
        }
        return noErrors;
    }
    
    /**
     * Finds all events
     * @return List of Events
     */
    public List<Event> findAll(){
        return em.createNamedQuery(Event.findAll, Event.class)
                                .getResultList();
    }
    
}
