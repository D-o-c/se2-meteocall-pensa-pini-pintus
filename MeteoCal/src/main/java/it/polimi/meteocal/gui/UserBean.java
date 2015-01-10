package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import it.polimi.meteocal.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
@ManagedBean
public class UserBean{

    private static final String home_page = "home?faces-redirect=true";
    
    @EJB
    UserArea ua;
        
    private String newEmail;
    private String newPassword;
    private String password;
    private List<Event> invites;
    private UploadedFile file;
 
    

    
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
        int count=0;
        for (Update u : ua.getLoggedUser().getNotifies()){
            if (!u.isRead()){
                count++;
            }
        }
        return invites.size() + count;
    }
    
    public String setNotifyRead(Update u){
        ua.setNotifyRead(u);
        return "/user/notifications?faces-redirect=true";        
    }
    
    public boolean isRead(Update u){
        return u.isRead();
    }
    
    public Event getSelectedEvent() {
        return ua.getSelectedEvent();
    }

    public void setSelectedEvent(Event selectedEvent) {
        ua.setSelectedEvent(selectedEvent);
    }
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public List<Update> getUpdate(){
        return ua.getUpdate();
    }
    /**************************************************************************/
    
    /**
     * Calls UserArea to set the visibility of the calendar
     * @return user home page
     */
    public String changeCalendarVisibility() {
        ua.changeCalendarVisibility();
        String message;
        if(getLoggedUser().isPublic()) message = "public";
        else message = "private";
        
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Info", "Now your calendar is " + message));
        context.getExternalContext().getFlash().setKeepMessages(true);
        return home_page;
    }
    
    /**
     * Calls UserArea to set the new password
     * @return user home page if password change is correct
     */
    public String changePassword() {
        boolean changeIsOk = ua.changePassword(password,newPassword);
        if(changeIsOk) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Password Successfully Changed"));
            context.getExternalContext().getFlash().setKeepMessages(true);
            return home_page;
        }
        else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,"Password Change Failure!","Please Try Again"));
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
        FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Invitation accepted"));
                context.getExternalContext().getFlash().setKeepMessages(true);
        return "home?faces-redirect=true";
    }
    
    public String deny(){
        ua.deny();
        FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Invitation refused"));
                context.getExternalContext().getFlash().setKeepMessages(true);
        return "home";
        
    }
    
    /**
     * return a list of event to which the user participates, for export them
     * @return 
     */
    public List<Event> getUserEvent(){
        return ua.getUserEvent();
    }
     
    public String upload() {
        FacesContext context = FacesContext.getCurrentInstance();
        String extension = file.getFileName().substring(file.getFileName().length()-3).toLowerCase();
        int status = 0;
        switch (extension) {
            case "xls":
                status = ua.importXLScalendar(file);
                break;
            case "csv":
                status = ua.importCSVcalendar(file);
                break;
            case "xml":
                status = ua.importXMLcalendar(file);
                break;
            default:
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR",
                        "Upload only xls, csv or xml file");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
        }
        
        switch (status){
            case -1:
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR",
                        "Verify that the file is well conformed");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return null;
            case -2:
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info",
                                "Calendar not completely imported because same event has problem with time consistency"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return "/user/home";
            default:
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Calendar imported succesfully"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return "/user/home";
        }
    }
    
}
