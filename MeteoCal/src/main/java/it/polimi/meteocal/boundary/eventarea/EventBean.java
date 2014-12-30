package it.polimi.meteocal.boundary.eventarea;

import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.control.EventManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;


/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class EventBean {
    
    private final static String user_home_with_errors =
            "/user/home?faces-redirect=true&eventcreatedwitherror=true";
    private final static String user_home = 
            "/user/home?faces-redirect=true&eventcreated=true";
    
    @EJB
    EventManager em;
    
    private Event event;
    
    private String invites;
    private List<String> invitedUsers;
    
    /**
     * Empty Constructor
     */
    public EventBean() {}
    
    /**************************** Getter and Setter ***************************/
    public Event getEvent() {
        if (event == null) {
            event = new Event();
        }
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
    public String getInvites() {
        return invites;
    }

    public void setInvites(String invites) {
        this.invites = invites;
    }
    /**************************************************************************/
    
    /**
     * Calls updateInviteList() to clean the invitedUsers List
     * Calls EventManager.createEvent(event,invitedUsers)
     * @return 
     */
    public String createEvent() {
        updateInviteList();
        boolean noErrors = em.createEvent(event, invitedUsers);
        if (noErrors){
            return user_home;
        }
        return user_home_with_errors;
    }
    
    /**
     * Method to suggest contacts during form compilation
     * Calls UserManager.getContacts()
     * @param query
     * @return possible contact emails matching the query
     */
    public List<String> complete(String query){
        List<String> invitedEmail = new ArrayList<>();
        List<Contact> contactList = em.getContacts();
        for (Contact c : contactList) {
            if (c.getEmail().contains(query)) {
                invitedEmail.add(c.getEmail() + "; ");
            }
        }
        return invitedEmail;
    }
    
    
    /**
     * Cleans the Arraylist<String> invitedUsers
     * From ; and " "
     */
    private void updateInviteList(){
        String[] part = invites.split(";");
        HashSet temp = new HashSet();
        invitedUsers = Arrays.asList(part);
        for (int i = 0; i < invitedUsers.size(); i++){
            invitedUsers.set(i, invitedUsers.get(i).replaceAll(" ", ""));
            temp.add(invitedUsers.get(i));
        }
        temp.remove("");
        invitedUsers = new ArrayList<>(temp);
    }
    
}
