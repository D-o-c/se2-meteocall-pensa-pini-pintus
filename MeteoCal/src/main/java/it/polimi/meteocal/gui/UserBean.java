package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.control.EmailSender;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
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
public class UserBean{

    private static final String home_page_url_pub = "home?faces-redirect=true&visibilitychanged=true";
    private static final String home_page_url_psw = "home?faces-redirect=true&passwordchanged=true";
    
    @EJB
    UserArea ua;
        
    private String newEmail;
    private String newPassword;
    private String password;
    private EmailSender se;

    
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
    /**************************************************************************/
    
    /**
     * Calls UserArea to set the visibility of the calendar
     * @param pub
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
    
    public void sendEmail() throws MessagingException{
        List<String> lista = new ArrayList<>();
        lista.add("mpini91@gmail.com");
        lista.add("aldo.pintus@gmail.com");
        lista.add("pensa.dario@gmail.com");
        EmailSender.send(lista, "viva lo spam", "prova invio più destinatari");
    }
}
