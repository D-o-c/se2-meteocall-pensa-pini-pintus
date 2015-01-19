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
public class CreateEventBean {
    
    //Strings
    private static final String info = "Info";
    private static final String warning = "Warning";
    private static final String overlapped_events_error = "You cannot have more events at the same time!";
    private static final String time_consistency_error = "Begin Time must be before End Time";
    private static final String creation_successfull = "Event Successfully Created";
    private static final String some_users_not_found = "Some Invited Users Not Found!";
    private final static String user_home_page_url = "/user/home?faces-redirect=true";

    //Boundaries
    @EJB
    EventArea eventArea;
    @EJB
    UserArea userArea;
    
    //Input string of emails of invited users
    private String invites;
    
    private Event event;

    /**
     * Empty Constructor
     */
    public CreateEventBean() {}
    
    /**
     * @return event
     */
    public Event getEvent() {
        if (event == null) {
            event = new Event();
        }
        return event;
    }

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
     * @param query = Input string used to autocomplete invited users list 
     * @return eventArea.complete(query)
     */
    public List<String> complete(String query) {
        return eventArea.complete(query);
    }
    
    /**
     * Calls userArea.timeConsistency(event)
     * Calls eventArea.createEvent(event, invites);
     * @return /user/home?faces-redirect=true
     */
    public String createEvent() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        int checkTimeConsistency = userArea.timeConsistency(event);
        
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
                boolean allUsersInvited = eventArea.createEvent(event, invites);
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, info, creation_successfull));
                context.getExternalContext().getFlash().setKeepMessages(true);
                if (allUsersInvited){
                    return user_home_page_url;
                }  
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, warning, some_users_not_found));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return user_home_page_url;
            default:
                return null;
        }
    }
        
}
