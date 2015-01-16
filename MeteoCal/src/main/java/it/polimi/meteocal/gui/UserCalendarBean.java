/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.SearchArea;
import it.polimi.meteocal.entity.Event;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 */
@Named
@RequestScoped
public class UserCalendarBean {
    
    private static final String user_addressbook_page_url = "user/addressbook?faces-redirect=true";

    
    @EJB
    SearchArea sa;
    
    @EJB
    EventArea ea;
    
    
    
    
    /**
     * Calls SearchArea.addContact()
     * @return addressbook page
     */
    public String addContact() {
        sa.addContact();
        return user_addressbook_page_url;
    }
    
    public Event getCurrentEvent(){
        return ea.getCurrentEvent();
    }
    
    /**
     * Calls SearchArea.exist()
     * @return if the user searched belongs to logged user contacts
     */
    public boolean exist(){
        return sa.exist();
    }
    
    ScheduleEvent selectedEvent = new DefaultScheduleEvent();
    private ScheduleModel eventModel;
    private boolean privateEvent = false;

    public boolean isPrivateEvent() {
        return privateEvent;
    }

    public void setPrivateEvent(boolean privateEvent) {
        this.privateEvent = privateEvent;
    }
    
    @PostConstruct
    public void init() {
        eventModel = sa.getCalendar();
       
    }
   
    public ScheduleModel getEventModel() {
        return eventModel;
    }
    
    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ScheduleEvent) selectEvent.getObject();
        ea.setCurrentEvent(selectedEvent.getDescription());
        privateEvent = selectedEvent.getDescription() == null;
    }
    
    public String getSelectedUserName(){
        return sa.getSelectedUser().getName();
    }
    
    public String getSelectedUserSurname(){
        return sa.getSelectedUser().getSurname();
    }
    
    public String getSelectedUserEmail(){
        return sa.getSelectedUser().getEmail();
    }
    
}
