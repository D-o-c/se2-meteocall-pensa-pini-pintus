package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.PublicArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 */
@Named
@RequestScoped
public class SettingsBean {
     
    //Strings
    private static final String info = "Info";
    private static final String warning = "Warning";
    private static final String error = "Error";
    private static final String choose = "Choose a file!";
    private static final String invalid_extension = "Upload only xls, csv or xml file";
    private static final String bad_formed_file = "Verify that the file is well conformed";
    private static final String successfull = "Calendar imported succesfully";
    private static final String time_consistency_error = 
            "Calendar not completely imported because same event has problem with time consistency";
    private static final String password_short = "Password must have at least 4 characters";
    private static final String password_changed = "Password Successfully Changed";
    private static final String try_again = "Please Try Again";
    private static final String public_calendar = "Now your calendar is PUBLIC";
    private static final String private_calendar = "Now your calendar is PRIVATE";
    private static final String unregistration = "Unregistration Successfull";
    private static final String index_page_url = "/index?faces-redirect=true";
    private static final String home_page_url = "home?faces-redirect=true";
    
    //Boundaries
    @EJB
    PublicArea publicArea;
    @EJB
    UserArea userArea;
    
    //logged user credentials
    private String password;
    private String newPassword;
    
    //import calendar
    private UploadedFile file;

    /**
     * Empty Constructor
     */
    public SettingsBean(){}
    
    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @return file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param password 
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param newPassword 
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * @param file 
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    /**************************************************************************/
    
    /**
     * @return userArea.isLoggedUserPublic()
     */
    public boolean isLoggedUserPublic(){
        return userArea.isLoggedUserPublic();
    }
    
    /**
     * @return userArea.getUserEvent()
     */
    public List<Event> getUserEvent(){
        return userArea.getUserEvent();
    }
    
    public String done(FileUploadEvent event){
        return home_page_url;
    }
    
    public void upload(FileUploadEvent event){
        this.file=event.getFile();
        this.upload();
    }
    
    /**
     * Calls userArea.import***calendar
     * @return home?faces-redirect=true
     */
    public String upload() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        String extension;
        
        try {
            extension = getFileExtension(file);
        } catch(Exception e){
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, error, choose));
            return null;
        }
        
        int status = 0;
        switch (extension) {
            case "xls":
                status = userArea.importXLScalendar(file);
                break;
            case "csv":
                status = userArea.importCSVcalendar(file);
                break;
            case "xml":
                status = userArea.importXMLcalendar(file);
                break;
            default:
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, error,invalid_extension));
                return null;
        }
        
        switch (status){
            case -1:
                context.addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, error, bad_formed_file));
                return null;
            case -2:
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, error, time_consistency_error));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return home_page_url;
            default:
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, info, successfull));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return home_page_url;
        }
    }
    
    /**
     * Calls userArea.changePassword(password,newPassword)
     * @return home?faces-redirect=true
     */
    public String changePassword() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        if (newPassword.length() < 9){
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, error, password_short));
            return null;
        }
        
        boolean changeIsOk = userArea.changePassword(password,newPassword);
        
        if(changeIsOk) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, info, password_changed));
            context.getExternalContext().getFlash().setKeepMessages(true);
            return home_page_url;
        }
        else {
            context.addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, error, try_again));
            return null;
        }
    }
    
    /**
     * Calls userArea.changeCalendarVisibility()
     * @return home?faces-redirect=true
     */
    public String changeCalendarVisibility() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        userArea.changeCalendarVisibility();
                
        if(userArea.isLoggedUserPublic()) {
            context.addMessage(null, new FacesMessage(info, public_calendar));
            context.getExternalContext().getFlash().setKeepMessages(true);
            return home_page_url;
        }
        else {
            context.addMessage(null, new FacesMessage(info, private_calendar));
            context.getExternalContext().getFlash().setKeepMessages(true);
            return home_page_url;
        }
    }
    
    /**
     * Calls publicArea.unregister()
     * @return /index?faces-redirect=true
     */
    public String unregister() {
        
        publicArea.unregister();
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, info, unregistration));
        context.getExternalContext().getFlash().setKeepMessages(true);
        
        return index_page_url;
    }
    
    /**
     * @param file
     * @return extension of a file 
     */
    private String getFileExtension(UploadedFile file) {
        return file.getFileName().substring(file.getFileName().length()-3).toLowerCase();
    }
    
}
