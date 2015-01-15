/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    @EJB
    UserArea ua;
    
    //private List<Event> invites;
    
    public List<Event> getInvites() {
        return ua.getInvites();
    }
    
    public int getNumberOfNotifies(){
        return ua.getNumberOfNotifies();
    }
    
    public String setNotifyRead(Update u){
        ua.setNotifyRead(u);
        return "/user/notifications?faces-redirect=true";        
    }
    
    public Event getSelectedEvent() {
        return ua.getSelectedEvent();
    }

    public void setSelectedEvent(Event selectedEvent) {
        ua.setSelectedEvent(selectedEvent);
    }
    
    public List<Update> getUpdate(){
        return ua.getUpdate();
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
    
}
