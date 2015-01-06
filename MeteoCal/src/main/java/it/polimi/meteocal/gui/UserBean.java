package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.control.EmailSender;
import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
@ManagedBean
public class UserBean{

    private static final String home_page_url_pub = "home?faces-redirect=true&visibilitychanged=true";
    private static final String home_page_url_psw = "home?faces-redirect=true&passwordchanged=true";
    
    @EJB
    UserArea ua;
        
    private String newEmail;
    private String newPassword;
    private String password;
    private List<Event> invites;

    
    /**
     * Empty Constructor
     */
    public UserBean() {}
    
    

    /**************************** Getter and Setter ***************************/    
    public String getName() {
        return ua.getLoggedUser().getName();
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public User getLoggedUser(){
        return ua.getLoggedUser();
    }
    
    public Locale getLocaleDefault(){
        return Locale.getDefault();
    }

    public List<Event> getInvites() {
        invites = new ArrayList<>();
        for (int i = 0; i < ua.getLoggedUser().getEvents().size(); i++){
            if (ua.getLoggedUser().getEvents().get(i).getInviteStatus() == 0){
                invites.add(ua.getLoggedUser().getEvents().get(i).getEvent());
            }
        }
        return invites;
    }
    
    public int getNumberOfNotifies(){
        invites = new ArrayList<>();
        for (int i = 0; i < ua.getLoggedUser().getEvents().size(); i++){
            if (ua.getLoggedUser().getEvents().get(i).getInviteStatus() == 0){
                invites.add(ua.getLoggedUser().getEvents().get(i).getEvent());
            }
        }
        return invites.size();
    }
    
    public Event getSelectedEvent() {
        return ua.getSelectedEvent();
    }

    public void setSelectedEvent(Event selectedEvent) {
        ua.setSelectedEvent(selectedEvent);
    }
    /**************************************************************************/
    
    /**
     * Calls UserArea to set the visibility of the calendar
     * @return user home page
     */
    public String changeCalendarVisibility() {
        ua.changeCalendarVisibility();
        return home_page_url_pub;
    }
    
    /**
     * Calls UserArea to set the new password
     * @return user home page if password change is correct
     */
    public String changePassword() {
        boolean changeIsOk = ua.changePassword(password,newPassword);
        if(changeIsOk) {
            return home_page_url_psw;
        }
        else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,"Please Try Again",null));
            return null;
        }
    }
    
    public String accept(){
        if (ua.timeConsistency(ua.getSelectedEvent()) == -2){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Another Event",
                                            "You already have another event at the same time! Delete it before!");
         
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return null;
        }
        ua.accept();
        return "home?faces-redirect=true";
    }
    
    public String deny(){
        ua.deny();
        return "home";
        
    }
    
    /**
     * return a list of event to which the user participates, for export them
     * @return 
     */
    public List<Event> getUserEvent(){
        return ua.getUserEvent();
    }
    
}
