package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 */
@Named
@RequestScoped
public class NotificationBean {
    
    //Strings
    private static final String info = "Info";
    private static final String accepted = "Invitation accepted! :)";
    private static final String refused = "Invitation refused! :(";
    private static final String another_event_error = "You already have another event at the same time! Delete it before!";
    private static final String user_notifications_page_url = "/user/notifications?faces-redirect=true";
    private static final String home_page_url = "home?faces-redirect=true";
    
    //Boundary
    @EJB
    UserArea userArea;
    
    /**
     * Empty Constructor
     */
    public NotificationBean() {}
    
    /**
     * @return userArea.getInvites()
     */
    public List<Event> getInvites() {
        return userArea.getInvites();
    }
    
    /**
     * @return userArea.getNumberOfNotifies()
     */
    public int getNumberOfNotifies(){
        return userArea.getNumberOfNotifies();
    }
    
    /**
     * Calls userArea.setNotifyRead(update)
     * @param update
     * @return /user/notifications?faces-redirect=true
     */
    public String setNotifyRead(Update update){
        userArea.setNotifyRead(update);
        return user_notifications_page_url;        
    }
    
    /**
     * Calls userArea.setAllNotifyRead
     * @return /user/notifications?faces-redirect=true
     */
    public String setAllNotifyRead(){
        userArea.setAllNotifyRead();
        return user_notifications_page_url;        
    }
    
    public boolean allRead() {
        return userArea.allRead();
    }
    
    /**
     * @return userArea.getSelectedEvent()
     */
    public Event getSelectedEvent() {
        return userArea.getSelectedEvent();
    }

    /**
     * Calls userArea.setSelectedEvent(selectedEvent)
     * @param selectedEvent 
     */
    public void setSelectedEvent(Event selectedEvent) {
        userArea.setSelectedEvent(selectedEvent);
    }
    
    /**
     * @return userArea.getUpdates()
     */
    public List<Update> getUpdates() {
        return userArea.getUpdates();
    }
    
    /**
     * Calls userArea.accept()
     */
    public void accept() {
        
        Event event = userArea.getSelectedEvent();
        
        int checkTimeConsistency = userArea.timeConsistency(event);
        
        if (checkTimeConsistency == -2){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, info, another_event_error);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return;
        }
        userArea.accept();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, info, accepted));
        context.getExternalContext().getFlash().setKeepMessages(true);
    }
    
    /**
     * Calls userArea.deny()
     */
    public void deny(){
        userArea.deny();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, info, refused));
        context.getExternalContext().getFlash().setKeepMessages(true);
    }
    
}
