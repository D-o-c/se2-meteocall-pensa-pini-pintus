/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.PublicArea;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FlowEvent;

/**
 *
 */

@ManagedBean
@ViewScoped
public class PasswordLostBean implements Serializable{
    
    
    private static final String password_short = "Password must have at least 4 characters";
    private static final String password_changed = "Password Successfully Changed";
    private static final String no_user_found = "User doesn't exist";
    private static final String no_token_found = "Token doesn't exist";
    
    private static final String info = "Info";
    private static final String error = "Error";
    private static final String index_page_url = "/index?faces-redirect=true";
    
    @EJB
    PublicArea publicArea;
        
    
    private String username;
    private String token;
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    
    
    
    public String recoveryPasswordProcess(FlowEvent event){
        if (event.getOldStep().equals("emailtab")){
            if (publicArea.sendPasswordToken(username) == -1){
                //user doesn't exist
                FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error , no_user_found));
                return "email";
            }
            return "tokentab";
        }
        return event.getNewStep();
    }
    
    public String changeLostPassword(){
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        if (newPassword.length() < 9){
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, error, password_short));
            return null;
        }
        
        switch(publicArea.changeLostPassword(username, token, newPassword)){
            case -2:
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, no_user_found));
                return null;
            case -1:
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, no_token_found));
                return null;
            case 0:
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        password_changed ,info));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return index_page_url;
            default:
                return null;
        }  
        
    }
    
}
