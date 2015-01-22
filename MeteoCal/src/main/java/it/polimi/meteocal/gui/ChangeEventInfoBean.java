package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 */
@Named
@RequestScoped
public class ChangeEventInfoBean {
    
    //Strings
    private static final String info = "Info";
    private static final String warning = "Warning";
    private static final String error = "Error";
    private static final String try_again = "An error occurred, please try again.";
    private static final String overlapped_events_error = "You have another event at the same time! Do you think you are Dolly?";
    private static final String time_consistency_error = "Begin Time must be before End Time! Do you have a DeLorean Time Machine?";
    private static final String creation_successfull = "Event info changed succesfully";
    private static final String deletion_successfull = "Event deleted succesfully";
    private static final String some_users_not_found = "Event changed succesfully, but some Invited Users Not Found! Stop inviting unknown person!";
    private static final String event_page_url = "/event?faces-redirect=true";
    private static final String user_home_page_url = "/user/home?faces-redirect=true";
    
    //Buondaries
    @EJB
    UserArea userArea;
    @EJB
    EventArea eventArea;
    
    Event tempEvent;
    
    //Input string of emails of invited users
    private String invites;

    /**
     * Empty Constructor
     */
    public ChangeEventInfoBean() {}
    
    /**
     * @return invites
     */
    public String getInvites() {
        return invites;
    }

    /**
     * @param invites 
     */
    public void setInvites(String invites) {
        this.invites = invites;
    }

    /**************************************************************************/
    
    /**
     * @return eventArea.isCreator()
     */
    public boolean isCreator() {
        return eventArea.isCreator();
    }
    
    /**
     * @return eventArea.getCurrentEvent()
     */
    public Event getCurrentEvent() {
        if (tempEvent == null){
            tempEvent = new Event(eventArea.getCurrentEvent());
        }
        return tempEvent;
    }
    
    /**
     * @param query = Input string used to autocomplete invited users list 
     * @return eventArea.complete(query)
     */
    public List<String> complete(String query) {
        return eventArea.complete(query);
    }
    
    /**
     * Calls eventArea.getCurrentEvent() </br>
     * Calls userArea.timeConsistency(currentEvent) </br>
     * Calls eventArea.updateCurrentEvent(invites)
     * @return /event?faces-redirect=true ("null" in case of errors)
     */
    public String saveEvent() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        
       // Event currentEvent = eventArea.getCurrentEvent();
        
        int checkTimeConsistency = userArea.timeConsistency(tempEvent);
        
        switch (checkTimeConsistency){
            case -2:
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, overlapped_events_error, ""));
                context.getExternalContext().getFlash().setKeepMessages(true);
                break;
            case -1:
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, time_consistency_error, ""));
                return null;
            case 0:
               break;
            default:
                return null;
        }
         int allUsersInvited = eventArea.updateCurrentEvent(invites, tempEvent);
                if (allUsersInvited == -2){
                    context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, error, try_again));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                }
                else if (allUsersInvited == -1){
                    context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, warning, some_users_not_found));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                }
                else{
                    context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, info, creation_successfull));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                }
                return event_page_url;
    }
    
    /**
     * Calls eventArea.deleteEvent()
     * @return /user/home?faces-redirect=true
     */
    public String deleteEvent() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        if (eventArea.deleteEvent() == 0){
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, info, deletion_successfull));
            context.getExternalContext().getFlash().setKeepMessages(true);
                
            return user_home_page_url;
        }
        else{
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, error, try_again));
            context.getExternalContext().getFlash().setKeepMessages(true);
            return event_page_url;
        }
        
        
        
    }
    
}
