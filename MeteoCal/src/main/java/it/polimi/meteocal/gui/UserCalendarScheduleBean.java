package it.polimi.meteocal.gui;
 
import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.SearchArea;
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
public class UserCalendarScheduleBean implements Serializable {
 
    //Boundaries
    @EJB
    SearchArea searchArea;
    @EJB
    EventArea eventArea;
    
    //Primefaces object for calendar
    private ScheduleModel scheduleModel; 
    private ScheduleEvent scheduleEvent = new DefaultScheduleEvent();
     
    @PostConstruct
    public void init() {
        scheduleModel = searchArea.getCalendar();    
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
        Event e = eventArea.getCurrentEvent();
        /*if(!e.isPub()) {
            //Description in the dialogue box becomes empty
            e = new Event();
            return e;
        }
        else {*/
            return e;
        //}
    }
    
    /**
     * @return !eventArea.getCurrentEvent().isPub()
     */
    public boolean isPrivate() {
        try{
            boolean a = eventArea.getCurrentEvent().isPublic();
            boolean b = searchArea.isPartecipants(eventArea.getCurrentEvent());
            return !(a || b);
        }
        catch(Exception e){
            return false;
        }
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