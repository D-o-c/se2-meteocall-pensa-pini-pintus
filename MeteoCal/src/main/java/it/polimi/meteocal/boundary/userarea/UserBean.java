package it.polimi.meteocal.boundary.userarea;

import it.polimi.meteocal.control.UserManager;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
public class UserBean{

    private static final String home_page_url = "home?faces-redirect=true";
    
    @EJB
    UserManager um;
    
    private boolean publicCalendar;
    
    private String newEmail;
    private String newPassword;
    private String password;
    
    /**
     * Empty Constructor
     */
    public UserBean() {}

    /**************************** Getter and Setter ***************************/    
    public String getName() {
        return um.getLoggedUser().getName();
    }

    public boolean isPublicCalendar() {
        return publicCalendar;
    }

    public void setPublicCalendar(boolean publicCalendar) {
        this.publicCalendar = publicCalendar;
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

    /**************************************************************************/
    
    /**
     * Calls UserManager to get the visibility of the calendar
     * @return The current visibility of the calendar
     */
    public String getCalendarVisibility() {
        String visibility;
        if(um.getLoggedUser().isPublic()) visibility = "Public";
        else visibility = "Private";
        return visibility;
    }
    
    /**
     * Calls UserManager to set the visibility of the calendar
     * @return user home page
     */
    public String changeCalendarVisibility() {
        um.changeCalendarVisibility(publicCalendar);
        return home_page_url;
    }
    
    /**
     * Calls UserManager to set the new password
     * @return user home page if opassword change is correct
     */
    public String changePassword() {
        boolean changeIsOk = um.changePassword(password,newPassword);
        if(changeIsOk) {
            return home_page_url;
        }
        else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Please Try Again"));
            return null;
        }
    }
      
}
