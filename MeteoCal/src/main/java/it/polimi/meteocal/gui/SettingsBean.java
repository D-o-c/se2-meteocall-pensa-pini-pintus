/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.PublicArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class SettingsBean {
     
    private static final String index_page_url = "/index?faces-redirect=true";
    private static final String home_page = "home?faces-redirect=true";
    
    
    @EJB
    PublicArea pa;
    @EJB
    UserArea ua;
    
    private String newEmail;
    private String password;
    private String newPassword;
    private UploadedFile file;

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
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public boolean isFileNull(){
        return this.file == null;
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
    
    /**
     * Calls UserArea to set the visibility of the calendar
     * @return user home page
     */
    public String changeCalendarVisibility() {
        ua.changeCalendarVisibility();
        String message;
        if(ua.isLoggedUserPublic()) message = "public";
        else message = "private";
        
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Info", "Now your calendar is " + message));
        context.getExternalContext().getFlash().setKeepMessages(true);
        return home_page;
    }
    
    
    /**
     * Calls PublicArea.unregister(), invalidate the session
     * @return index page
     */
    public String unregister() {
        pa.unregister();
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Unregistration Successfull"));
        context.getExternalContext().getFlash().setKeepMessages(true);
        
        return index_page_url;
    }
    
    public boolean isLoggedUserPublic(){
        return ua.isLoggedUserPublic();
    }
}
