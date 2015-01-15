/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class ChangeEventInfoBean {
    
    @EJB
    UserArea ua;
    
    @EJB
    EventArea ea;
    
    private String invites;

    public String getInvites() {
        return invites;
    }

    public void setInvites(String invites) {
        this.invites = invites;
    }
    
    public boolean isCreator(){
        return ea.isCreator();
    }
    
    public Event getCurrentEvent(){
        return ea.getCurrentEvent();
    }
    
    public List<String> complete(String query){
        return ea.complete(query);
    }
    
    
    
    
    public String saveEvent(){
        switch (ua.timeConsistency(ea.getCurrentEvent())){
            case -2:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "You cannot have more events at the same time!",null));
                return null;
            case -1:
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Begin Time must be before End Time",null));
                return null;
            case 0:
                boolean noErrors = ea.updateCurrentEvent(invites);
                
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Event info changed succesfully"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                if (noErrors){
                    return "/event?faces-redirect=true";
                }  
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,"Warning", "Some Invited Users Not Found!"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return "/event?faces-redirect=true";
            default:
                return null;
        }
    }
    
    public String deleteEvent(){
        ea.deleteEvent();
        
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Event deleted succesfully"));
        context.getExternalContext().getFlash().setKeepMessages(true);
                
        return "/user/home?faces-redirect=true";
    }
    
    
    
}
