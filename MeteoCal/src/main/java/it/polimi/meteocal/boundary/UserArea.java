package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.PasswordEncrypter;
import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author aldo
 */
@Stateless
public class UserArea {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    Event selectedEvent; //event to accept or deny
    
    /**
     * Calls EntityManager.find(User.class, principal.getName())
     * @return the logger user
     */
    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
    
    /**
     * Calls EntityManager.find(User.class, PrimaryKey)
     * Sets the logged user privacy
     */
    public void changeCalendarVisibility() {
        boolean temp = getLoggedUser().isPublic();
        temp ^= true;
        getLoggedUser().setPublic(temp);
    }
    
    /**
     * Controls validity of input password and possibly save that password
     * @param inputPassword
     * @param newPassword
     * @return if change has been made correctly
     */
    public boolean changePassword(String inputPassword, String newPassword) {
        String email = getLoggedUser().getEmail();
        String enc_old_psw = getLoggedUser().getPassword();
        String enc_new_psw = PasswordEncrypter.encryptPassword(inputPassword);
        if(enc_new_psw.equals(enc_old_psw)) {
            //update password
            em.find(User.class, email).setPassword(newPassword);
            return true;
        }
        else {
            return false;
        }
    }
    
    public ScheduleModel getCalendar(){
        ScheduleModel calendar = new DefaultScheduleModel();
        List<Calendar> temp = getLoggedUser().getEvents();
        for (Calendar temp1 : temp) {
            if (temp1.getInviteStatus() == 1) {
                Event evntTemp = temp1.getEvent();
                DefaultScheduleEvent dse = new DefaultScheduleEvent(evntTemp.getName(),
                                                                    evntTemp.getBeginTime(),
                                                                    evntTemp.getEndTime());
                dse.setDescription(Long.toString(evntTemp.getEventId()));
                calendar.addEvent(dse);
                
            }
        }
        
        
        return calendar;
    }
    /**
     * Verify timeConsistency of the @param and between event (with inviteStatus == 1) of logged user and @param
     * @param event
     * @return 
     *      -1: if beginTime is AFTER endTime<br/>
     *      -2: if the logged user has another event at the same time<br/>
     *      0: if there isn't problem
     */
    public int timeConsistency(Event event){
        try{
            if (event.getBeginTime().after(event.getEndTime())){ //beginTime is AFTER endTime
                return -1;
            }

            List<Calendar> c = this.getLoggedUser().getEvents();
            for (Calendar c1 : c) {
                if (event.getBeginTime().before(c1.getEvent().getEndTime()) &&
                        event.getEndTime().after(c1.getEvent().getBeginTime()) &&
                        event.getEventId() != c1.getEventId() &&
                        c1.getInviteStatus()==1){
                    return -2;
                }
            }
        }
        catch(NullPointerException e){
                
        }
        return 0;
    }
    
    public void accept(){
        for (int i=0;i<selectedEvent.getInvited().size();i++){
            if (selectedEvent.getInvited().get(i).getUser().getEmail().equals(this.getLoggedUser().getEmail())){
                selectedEvent.getInvited().get(i).setInviteStatus(1);
            }
        }
        for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
            if (this.getLoggedUser().getEvents().get(i).getEventId()==selectedEvent.getEventId()){
                this.getLoggedUser().getEvents().get(i).setInviteStatus(1);
            }
        }
        //e.addInvited(this.getLoggedUser(), 1);
        em.merge(selectedEvent);
        em.merge(this.getLoggedUser());
    }
    public void deny(){
        for (int i=0;i<selectedEvent.getInvited().size();i++){
            if (selectedEvent.getInvited().get(i).getUser().getEmail().equals(this.getLoggedUser().getEmail())){
                selectedEvent.getInvited().get(i).setInviteStatus(-1);
            }
        }
        for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
            if (this.getLoggedUser().getEvents().get(i).getEventId()==selectedEvent.getEventId()){
                this.getLoggedUser().getEvents().get(i).setInviteStatus(-1);
            }
        }
        em.merge(selectedEvent);
        em.merge(this.getLoggedUser());
    }

    /**
     * return a list of event to which the user participates, for export them
     * @return 
     */
    public List<Event> getUserEvent() {
        List<Event> temp = new ArrayList<>();
        for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
            if (this.getLoggedUser().getEvents().get(i).getInviteStatus()==1){
                temp.add(this.getLoggedUser().getEvents().get(i).getEvent());
            }
        }
        return temp;
    }
    
    
    
}


