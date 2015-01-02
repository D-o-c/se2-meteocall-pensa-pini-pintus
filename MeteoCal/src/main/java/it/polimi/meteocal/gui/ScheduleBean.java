package it.polimi.meteocal.gui;
 
import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Event;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
 
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
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
 
    public String today() {/*
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

 
        return calendar.toString();*/
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public String onEventSelect(SelectEvent selectEvent) {
        selectedEvent = (ScheduleEvent) selectEvent.getObject();
        return goInEventPage();
    }
    
       
    public String goInEventPage(){
        String title = selectedEvent.getTitle();
        int pos = title.lastIndexOf("$")+1;
        String id = title.substring(pos);
        ea.setCurrentEvent(Long.parseLong(id));
        return "/event?faces-redirect=true";
    }

    public Event getCurrentEvent(){
        return ea.getCurrentEvent();
    }
}