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
    private static final String overlapped_events_error = "You cannot have more events at the same time!";
    private static final String time_consistency_error = "Begin Time must be before End Time";
    private static final String creation_successfull = "Event info changed succesfully";
    private static final String deletion_successfull = "Event deleted succesfully";
    private static final String some_users_not_found = "Some Invited Users Not Found!";
    private static final String event_page_url = "/event?faces-redirect=true";
    private static final String user_home_page_url = "/user/home?faces-redirect=true";
    
    //Buondaries
    @EJB
    UserArea userArea;
    @EJB
    EventArea eventArea;
    
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
        return eventArea.getCurrentEvent();
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
        
        Event currentEvent = eventArea.getCurrentEvent();
        
        int checkTimeConsistency = userArea.timeConsistency(currentEvent);
        
        switch (checkTimeConsistency){
            case -2:
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, overlapped_events_error, null));
                return null;
            case -1:
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, time_consistency_error, null));
                return null;
            case 0:
                boolean allUsersInvited = eventArea.updateCurrentEvent(invites);
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, info, creation_successfull));
                context.getExternalContext().getFlash().setKeepMessages(true);
                if (allUsersInvited){
                    return event_page_url;
                }  
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, warning, some_users_not_found));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return event_page_url;
            default:
                return null;
        }
    }
    
    /**
     * Calls eventArea.deleteEvent()
     * @return /user/home?faces-redirect=true
     */
    public String deleteEvent() {
        
        eventArea.deleteEvent();
        
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, info, deletion_successfull));
        context.getExternalContext().getFlash().setKeepMessages(true);
                
        return user_home_page_url;
    }
    
}
