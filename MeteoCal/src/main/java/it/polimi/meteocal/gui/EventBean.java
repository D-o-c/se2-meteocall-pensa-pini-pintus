package it.polimi.meteocal.gui;

import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.WeatherCondition;
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
    
    private final static String user_home = "/user/home?faces-redirect=true";
    
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
    
    public List<WeatherCondition> getWeather(){
        return ea.getCurrentEvent().getWeatherConditions();
    }
    /**************************************************************************/
    
    /**
     * Calls updateInviteList() to clean the invitedUsers List
 Calls EventArea.createEvent(event,invitedUsers)
     * @return 
     */
    public String createEvent(){
        switch (ua.timeConsistency(event)){
            case -2:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "You cannot have more events at the same time!",null));
                return null;
            case -1:
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Begin Time must be before End Time",null));
                return null;
            case 0:
                updateInviteList();
                boolean noErrors = ea.createEvent(event, invitedUsers);
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Event Successfully Created"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                if (noErrors){
                    return user_home;
                }  
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,"Warning", "Some Invited Users Not Found!"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return user_home;
            default:
                return null;
        }
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
            if (c.getEmail().startsWith(query)){
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
        invites = invites.replace("\n", "");
        invites = invites.replace("\r", "");
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
    
    
    public String goToChangeEventInfo(){
        return "user/changeeventinfo?faces-redirect=true";
    }
    
    public String saveEvent(){
        switch (ua.timeConsistency(ea.getCurrentEvent())){
            case -2:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "You cannot have more events at the same time!",null));
                return null;
            case -1:
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Begin Time must be before End Time",null));
                return null;
            case 0:
                this.updateInviteList();
                boolean noErrors = ea.updateCurrentEvent(invitedUsers);
                
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Event info changed succesfully"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                if (noErrors){
                    return "/event?faces-redirect=true";
                }  
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,"Warning", "Some Invited Users Not Found!"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return "/event?faces-redirect=true";
            default:
                return null;
        }
    }
    
    public boolean isCreator(){
        return ea.isCreator();
    }
    
    public boolean isPartecipants(){
        boolean uno = ea.isCreator();
        boolean due = ea.isPartecipants();
        boolean disable = uno || !due;
        return disable;
       // return ea.isCreator() || ea.isPartecipants();
    }
    
    public String removePartecipation(){
        ea.removeFromPartecipants();
        FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Succesfully removed from partecipants"));
        context.getExternalContext().getFlash().setKeepMessages(true);
                        
        return "/user/home";
    }
    
}
