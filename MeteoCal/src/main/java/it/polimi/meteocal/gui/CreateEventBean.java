package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class CreateEventBean {
    
    private final static String user_home = "/user/home?faces-redirect=true";

    
    @EJB
    EventArea ea;
    @EJB
    UserArea ua;
    
    private String invites;
    private Event event;
    
    public Event getEvent() {
        if (event == null) {
            event = new Event();
        }
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    public String getInvites() {
        return invites;
    }

    public void setInvites(String invites) {
        this.invites = invites;
    }
    
    /**
     * Calls updateInviteList() to clean the invitedUsers List
        Calls EventArea.createEvent(event,invitedUsers)
     * @return 
     */
    public String createEvent(){
        switch (ua.timeConsistency(event)){
            case -2:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "You cannot have more events at the same time!",null));
                return null;
            case -1:
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Begin Time must be before End Time",null));
                return null;
            case 0:
                boolean noErrors = ea.createEvent(event, invites);
                
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Event Successfully Created"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                if (noErrors){
                    return user_home;
                }  
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,"Warning", "Some Invited Users Not Found!"));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return user_home;
            default:
                return null;
        }
    }
    
    public List<String> complete(String query){
        return ea.complete(query);
    }
    
}
