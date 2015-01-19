package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.EmailSender;
import it.polimi.meteocal.control.EventManager;
import it.polimi.meteocal.control.GuestManager;
import it.polimi.meteocal.control.UserManager;
import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 */
@Stateless
public class EventArea{
    
    //Controls
    @Inject
    GuestManager guestManager;
    @Inject
    UserManager userManager;
    @Inject
    EventManager eventManager;
    @Inject
    EmailSender emailSender;
    
    // Event currently selected
    Event currentEvent;
    
    /**
     * @return currentEvent
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }
    
    /**
     * Calls eventManager.find(id)
     * @param id 
     */
    public void setCurrentEvent(Object id) {
        if (id == null){
            currentEvent = new Event();
            currentEvent.setPub(false);
           
        }
        else{
            currentEvent = eventManager.find((long) id);
        }
    }
    
    /**************************************************************************/
    
    /**
     * @return true if loggedUser == creatorUser
     */
    public boolean isCreator(){
        return currentEvent.getCreator().equals(guestManager.getLoggedUser());
    }
    
    /**
     * @return true if loggedUser partecipates to currentEvent
     */
    public boolean isPartecipants() {
        
        List<String> partecipants = new ArrayList<>();
        
        for (Calendar c : currentEvent.getInvited()){
            if (c.getInviteStatus() == 1){
                partecipants.add(c.getUser().getEmail());
            }
        }
        
        return partecipants.contains(guestManager.getLoggedUser().getEmail());
    }
    
    /**
     * Calls this.updateInviteList(invitesInput)
     * @param event
     * @param invitesInput : String containing emails
     * @return eventManager.createEvent(event, invitedUsers, guestManager.getLoggedUser())
     */
    public boolean createEvent(Event event, String invitesInput){
        List<String> invitedUsers = updateInviteList(invitesInput);
        return eventManager.createEvent(event, invitedUsers, guestManager.getLoggedUser());
    }
    
    /**
     * Calls this.updateInviteList(invitesInput)
     * @param invitesInput : String containing emails
     * @return eventManager.updateEvent(currentEvent, invitedUsers)
     */
    public boolean updateCurrentEvent(String invitesInput){
        List<String> invitedUsers = updateInviteList(invitesInput);
        return eventManager.updateEvent(currentEvent, invitedUsers);
    }
    
    /**
     * Builds a List<String> from the invitesInput
     * @param invitesInput : String
     * @return List<String> invitedUsers
     */
    private List<String> updateInviteList(String invitesInput){
        
        List<String> invitedUsers;
        HashSet hs = new HashSet();
        
        if (invitesInput == null){
            invitesInput = "";
        }
        
        invitesInput = invitesInput.replace("\n", "").replace("\r", "").replace("\t", "");
        String[] part = invitesInput.split(";");
        invitedUsers = Arrays.asList(part);
        
        for (int i = 0; i < invitedUsers.size(); i++){
            invitedUsers.set(i, invitedUsers.get(i).replaceAll(" ", ""));
            hs.add(invitedUsers.get(i));
        }
        
        hs.remove("");
        invitedUsers = new ArrayList<>(hs);
        
        return invitedUsers;
    }

    /**
     * Calls eventManager.removeFromPartecipants(currentEvent, guestManager.getLoggedUser());
     */
    public void removeFromPartecipants() {
        eventManager.removeFromPartecipants(currentEvent, guestManager.getLoggedUser());
    }

    /**
     * @return eventManager.getPartecipants(currentEvent)
     */
    public List<User> getPartecipants() {
        return eventManager.getPartecipants(currentEvent);
    }

    /**
     * Calls eventManager.deleteEvent(currentEvent)
     * currentEvent = null
     */
    public void deleteEvent() {
        eventManager.deleteEvent(currentEvent);
        currentEvent = null;
    }
    
    /**
     * Method to suggest contacts during form compilation
     * Calls userManager.getContacts()
     * @param query
     * @return invitedEmail : list of possible contact emails matching the query
     */
    public List<String> complete(String query){
        List<String> invitedEmail = new ArrayList<>();
        List<Contact> contactList = userManager.getContacts(guestManager.getLoggedUser());
        for (Contact c : contactList) {
            if (c.getEmail().startsWith(query)){
                invitedEmail.add(c.getEmail() + "; ");
            }
        }
        return invitedEmail;
    }

}
