package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.EmailSender;
import it.polimi.meteocal.control.EventManager;
import it.polimi.meteocal.control.GuestManager;
import it.polimi.meteocal.control.UserManager;
import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author doc
 */
@Stateless
public class EventArea{
    
    
    
    @Inject
    GuestManager gm;
    
    @Inject
    UserManager userManager;
    
    @Inject
    EventManager em;
    
    @Inject
    EmailSender emailS;
    
    @Inject
    Principal principal;
    
    Event currentEvent;
    

    
    
    /**
     * @return List of contacts of the logged user
     *//*
    public List<Contact> getContacts() {
        return getLoggedUser().getContacts();
    }*/
    
    public Event getCurrentEvent() {
        return currentEvent;
    }
    
    /**
     * Return true if logged user is creator
     * @return 
     */
    public boolean isCreator(){
        return currentEvent.getCreator().equals(gm.getLoggedUser());
    }
    
    public boolean isPartecipants() {
        List<String> partecipants = new ArrayList<>();
        for (Calendar c : currentEvent.getInvited()){
            if (c.getInviteStatus() == 1){
                partecipants.add(c.getUser().getEmail());
            }
        }
        return partecipants.contains(gm.getLoggedUser().getEmail());
    }
    
    /**
     * Adds creator user email to event
     * Calls EntityManager.persist(event)
     * Updates calendars of invited users
     * @param event
     * @param invitedUsers
     * @return if all invited users exist in the database
     * 
     * OKOK
     */
    public boolean createEvent(Event event, String invites){
        List<String> invitedUsers = this.updateInviteList(invites);
        
        return em.createEvent(event, invitedUsers, gm.getLoggedUser());
        
    }
    /*
     * OKOK
     */
    public boolean updateCurrentEvent(String invites){
        List<String> invitedUsers = this.updateInviteList(invites);
        
        return em.updateEvent(currentEvent, invitedUsers);
    }
    
    
    
    /**
     * Cleans the Arraylist<String> invitedUsers
     * From ; and " "
     * 
     * OKOK
     */
    private List<String> updateInviteList(String invites){
        List<String> invitedUsers;
        
        if (invites == null){
            invites = "";
        }
        invites = invites.replace("\n", "").replace("\r", "").replace("\t", "");
        String[] part = invites.split(";");
        HashSet temp = new HashSet();
        invitedUsers = Arrays.asList(part);
        for (int i = 0; i < invitedUsers.size(); i++){
            invitedUsers.set(i, invitedUsers.get(i).replaceAll(" ", ""));
            temp.add(invitedUsers.get(i));
        }
        temp.remove("");
        invitedUsers = new ArrayList<>(temp);
        
        return invitedUsers;
        
    }
    
    
    /**
     * Finds all events
     * @return List of Events
     *//*
    public List<Event> findAll(){
        return em.createNamedQuery(Event.findAll, Event.class)
                                .getResultList();
    }*/

    
    public void setCurrentEvent(String id) {
        if (id == null){
            currentEvent = new Event();
            
        }
        else{
            currentEvent = em.find(Long.parseLong(id));
        }
    }
/*
    public Event findEvent(long id) {
        return em.find(id);
    }*/

    /**
     * Remove current user from the list of partecipants of current event
     * OKOK
     */
    public void removeFromPartecipants() {
        
        em.removeFromPartecipants(currentEvent, gm.getLoggedUser());
        
        
    }

    
    
    public List<User> getPartecipants() {
        return em.getPartecipants(currentEvent);
        
        
    }

    
    public void deleteEvent() {
        
        em.deleteEvent(currentEvent);
        currentEvent = null;
        
        
    }
    
    
    
    /**
     * Method to suggest contacts during form compilation
     * Calls UserManager.getContacts()
     * @param query
     * @return possible contact emails matching the query
     */
    public List<String> complete(String query){
        List<String> invitedEmail = new ArrayList<>();
        List<Contact> contactList = userManager.getContacts(gm.getLoggedUser());
        for (Contact c : contactList) {
            if (c.getEmail().startsWith(query)){
                invitedEmail.add(c.getEmail() + "; ");
            }
        }
        return invitedEmail;
    }

  

    
    
}
