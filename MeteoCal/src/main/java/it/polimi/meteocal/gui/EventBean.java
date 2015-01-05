package it.polimi.meteocal.gui;

import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.mail.MessagingException;


/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class EventBean{
    
    private final static String user_home_with_errors =
            "/user/home?faces-redirect=true&eventcreatedwitherror=true";
    private final static String user_home = 
            "/user/home?faces-redirect=true&eventcreated=true";
    
    @EJB
    EventArea ea;
    @EJB
    UserArea ua;
    
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
    
    public List<String> getInvitedUser(){
        return invitedUsers;
    }
    
    public Event getCurrentEvent(){
        return ea.getCurrentEvent();
    }
    /**************************************************************************/
    
    /**
     * Calls updateInviteList() to clean the invitedUsers List
 Calls EventArea.createEvent(event,invitedUsers)
     * @return 
     * @throws javax.mail.MessagingException 
     */
    public String createEvent() throws MessagingException {
        
        if (!timeConsistency(this.event)){
            return null;
        }
        updateInviteList();
        boolean noErrors = ea.createEvent(event, invitedUsers);
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
        List<Contact> contactList = ea.getContacts();
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
        if (invites == null){
            invites = "";
        }
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
    
    private boolean timeConsistency(Event event){
        if (event.getBeginTime().after(event.getEndTime())){ //beginTime is AFTER endTime
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Begin Time must be before End Time"));
            return false;
        }
        
        List<Calendar> c = ua.getLoggedUser().getEvents();
        for (Calendar c1 : c) {
            if (event.getBeginTime().before(c1.getEvent().getEndTime()) &&
                    event.getEndTime().after(c1.getEvent().getBeginTime()) && event.getEventId() != c1.getEventId()){
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "You cannot have more events at the same time!"));
                return false;
            }
        }
        return true;
    }
    
    public String goToChangeEventInfo(){
        return "user/changeeventinfo?faces-redirect=true";
    }
    
    public String saveEvent() throws MessagingException{
        if (timeConsistency(ea.getCurrentEvent())){
            this.updateInviteList();
            ea.updateCurrentEvent(invitedUsers);
            return "/event?faces-redirect=true";
        }
        return null;
    }
    
    public boolean isCreator(){
        return ea.isCreator();
    }
    
}
