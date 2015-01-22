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

/**
 * 
 */
@ManagedBean
@ViewScoped
public class HomeScheduleBean implements Serializable {
 
    //Boundaries
    @EJB
    UserArea userArea;
    @EJB
    EventArea eventArea;
    
    //Primefaces object for calendar
    private ScheduleModel scheduleModel; 
    private ScheduleEvent scheduleEvent = new DefaultScheduleEvent();
     
    @PostConstruct
    public void init() {
        scheduleModel = userArea.getCalendar();    
    }

    /**
     * @return scheduleModel
     */
    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    /**
     * @param scheduleModel 
     */
    public void setScheduleModel(ScheduleModel scheduleModel) {
        this.scheduleModel = scheduleModel;
    }
       
    /**************************************************************************/
    
    /**
     * @param selectEvent 
     */
    public void onEventSelect(SelectEvent selectEvent) {
        scheduleEvent = (ScheduleEvent) selectEvent.getObject();
        eventArea.setCurrentEvent(scheduleEvent.getData());
    }
    
    /**
     * @return eventArea.getCurrentEvent()
     */
    public Event getCurrentEvent(){
        return eventArea.getCurrentEvent();
    }
    
    /**
     * @return Today
     */
    public String today() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
     
}