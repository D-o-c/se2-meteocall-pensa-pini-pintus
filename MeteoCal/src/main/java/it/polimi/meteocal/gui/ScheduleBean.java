package it.polimi.meteocal.gui;
 
import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
 
@ManagedBean
@ViewScoped
public class ScheduleBean implements Serializable {
 
    private ScheduleModel eventModel;
    
    @EJB
    private UserArea ua;
    @EJB
    private EventArea ea;
 
    private ScheduleEvent selectedEvent = new DefaultScheduleEvent();
 
    @PostConstruct
    public void init() {
        eventModel = ua.getCalendar();
       
    }
     
    
     
    public ScheduleModel getEventModel() {
        return eventModel;
    }
 
    public String today() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public String onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ScheduleEvent) selectEvent.getObject();
        return goInEventPage();
    }
    
       
    public String goInEventPage(){/*
        String title = selectedEvent.getTitle();
        int pos = title.lastIndexOf("$")+1;
        String id = title.substring(pos);*/
        String id = selectedEvent.getDescription();
        ea.setCurrentEvent(Long.parseLong(id));
        return "/event?faces-redirect=true";
    }

    public Event getCurrentEvent(){
        return ea.getCurrentEvent();
    }
    
    public List<User> getPartecipants(){
        return ea.getPartecipants();
    }
    
    public String getEventOutdoorStatus(){
        if (ea.getCurrentEvent().isOutdoor()){
            return "yes";
        }
        return "no";
    }
    
    /**
     * Called By event.xhtml
     * @return 
     */
    public String getGoogleMap() {
        
        String str = getCurrentEvent().getLocation();
        //creates an array of address-city-state
        String[] parts = str.split(",");
        
        String address = parts[0].replaceAll(" ","+");
        String city = parts[1].replaceAll(" ","");
        String state = parts[2].replaceAll(" ","");
        
        String  location = address+","+city+"+"+state;
        
        return "https://www.google.com/maps/embed/v1/place?key=AIzaSyDDm0i7Jy_achXhFjVg8LcT1kbmi8wmdV4&q="
                +location;
        
    }
       
}