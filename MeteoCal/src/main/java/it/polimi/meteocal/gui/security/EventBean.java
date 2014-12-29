/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui.security;

import it.polimi.meteocal.business.security.boundary.EventArea;
import it.polimi.meteocal.business.security.boundary.UserArea;
import it.polimi.meteocal.business.security.entity.Contact;
import it.polimi.meteocal.business.security.entity.Event;
import it.polimi.meteocal.business.security.entity.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class EventBean {
    
    
    
    
    @EJB
    private EventArea ea;

    private Event event;
    
    @EJB
    UserArea ua;
    
    private String invites;

    public String getInvites() {
        return invites;
    }

    public void setInvites(String invites) {
        this.invites = invites;
    }

    public EventBean() {
    }

    public Event getEvent() {
        if (event == null) {
            event = new Event();
        }
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String createEvent() {
        event.setCreatorEmail(ua.getLoggedUser().getEmail());
        ea.save(event);
        return "/user/home?faces-redirect=true&eventcreated=true";
    }
    
    public List<String> complete(String query){
        
        List<String> eCont = new ArrayList<String>();
        List<Contact> cList = ua.getContacts();
        for (Contact cList1 : cList) {
            if (cList1.getEmail().contains(query)) {
                eCont.add(cList1.getEmail() + "; ");
            }
        }
        return eCont;
    }
    
    
    
    /*
    public String unregister() {
        um.unregister();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        return "/index?faces-redirect=true";
    }
    
    
    
    
    
    
    
    private String title;
    private Date today=new Date();
    private Date beginTime;
    private Date endTime;
    private String description;
    private boolean pub=true;
    private String location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        //this.today = today;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    
    
    public String getTodayDate(){        
        return Calendar.DATE + "/" + Calendar.MONTH + 1 + "/" + Calendar.YEAR;
    }
    */
    
    
}
