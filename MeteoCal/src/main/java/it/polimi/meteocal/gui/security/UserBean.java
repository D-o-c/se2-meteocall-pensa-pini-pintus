/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui.security;

import it.polimi.meteocal.business.security.boundary.UserArea;
import it.polimi.meteocal.business.security.entity.Contact;
import it.polimi.meteocal.business.security.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
@ManagedBean
public class UserBean{

    @EJB
    UserArea ua;
    
    private User user;
    
    private boolean publicCalendar;
    
    private String newEmail;
    private String newPassword;
    private String password;
    
            
    public UserBean() {
    }

    public User getUser() {
        if (user==null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String getName() {
        return ua.getLoggedUser().getName();
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

    public String getCalendarVisibility() {
        String visibility;
        if(ua.getLoggedUser().isPublic()) visibility = "Public";
        else visibility = "Private";
        return visibility;
    }
    
    public String changeCalendarVisibility() {
        ua.changeCalendarVisibility(publicCalendar);
        return "home?faces-redirect=true";
    }
    
    public String changeEmail() {
        boolean changeIsOk = ua.changeEmail(newEmail,password);
        if(changeIsOk) {
            return "home?faces-redirect=true";
        }
        else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Error"));
            return null;
        }
        
    }
    
    public String changePassword() {
        boolean changeIsOk = ua.changePassword(password,newPassword);
        if(changeIsOk) {
            return "home?faces-redirect=true";
        }
        else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Please Try Again"));
            return null;
        }
    }
    
    public List<Contact> getContacts() {
        return ua.getContacts();
    }
    
    public String results() {
        if(ua.getContacts().isEmpty()) return "Your Addressbook Is Empty";
        else return "";
    }
    
    public String addContact(String email, String name, String surname) {
       ua.addContact(email, name, surname, getUser());
       return "user/addressbook?faces-redirect=true";
    }
    
    public String deleteContact(String contact_email) {
        ua.deleteContact(contact_email);
        return "addressbook?faces-redirect=true";
    }
    
    public boolean exist(String cEmail){
        return ua.exist(cEmail);
    }
      
}
