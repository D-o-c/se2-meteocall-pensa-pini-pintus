package it.polimi.meteocal.gui;
 
import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    
    public void onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ScheduleEvent) selectEvent.getObject();
        ea.setCurrentEvent(selectedEvent.getDescription());
        //return goInEventPage();
    }

    public Event getCurrentEvent(){
        return ea.getCurrentEvent();
    }
   
    

}