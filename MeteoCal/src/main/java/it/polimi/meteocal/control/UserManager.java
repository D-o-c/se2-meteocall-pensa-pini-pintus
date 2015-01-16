/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.primarykeys.ContactPK;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 * DA COMMENTARE
 */
public class UserManager {
    
    @PersistenceContext
    EntityManager em;
    
    
    public void changeCalendarVisibility(User u){
        boolean temp = u.isPublic();
        temp ^= true;
        u.setPublic(temp);
        em.merge(u);
    }
    
    public void changePassword(User u, String p){
        u.setPassword(p);
        em.merge(u);
    }
    
    public ScheduleModel getCalendar(User u, User loggedUser){
        
        ScheduleModel calendar = new DefaultScheduleModel();
        try{
            
            List<Calendar> temp = u.getEvents();
            for (Calendar temp1 : temp) {
                if (temp1.getInviteStatus() == 1) {
                    Event evntTemp = temp1.getEvent();
                    DefaultScheduleEvent dse = new DefaultScheduleEvent(evntTemp.getName(),
                                                                        evntTemp.getBeginTime(),
                                                                        evntTemp.getEndTime());
                    if (u.equals(loggedUser) || evntTemp.isPub()){
                        dse.setDescription(Long.toString(evntTemp.getEventId()));
                    }
                    else{
                        dse.setTitle("Private event!");
                        dse.setDescription(null);
                    }
                    calendar.addEvent(dse);

                }
            }
        }
        catch (Exception e){
            
        }
        
        
        return calendar;
        
    }
    
    public int timeConsistency(User u, Event e){
        try{
            if (e.getBeginTime().after(e.getEndTime())){ //beginTime is AFTER endTime
                return -1;
            }

            List<Calendar> c = u.getEvents();
            for (Calendar c1 : c) {
                if (e.getBeginTime().before(c1.getEvent().getEndTime()) &&
                        e.getEndTime().after(c1.getEvent().getBeginTime()) &&
                        e.getEventId() != c1.getEventId() &&
                        c1.getInviteStatus()==1){
                    return -2;
                }
            }
        }
        catch(NullPointerException exc){
                
        }
        return 0;
        
    }

    public List<Event> getUserEvent(User user) {
        //QUERY??????????
        List<Event> temp = new ArrayList<>();
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getInviteStatus()==1){
                temp.add(user.getEvents().get(i).getEvent());
            }
        }
        return temp;
    }

    public int importCalendar(User user, HashSet<Event> e) {
        int status = 0;
        for (int i=0;i<user.getEvents().size();i++){
            if (!user.getEvents().get(i).getEvent().getCreator().equals(user)){
                user.getEvents().get(i).setInviteStatus(-1);
            }
        }
        List<Event> events = new ArrayList<>(e);
        
        for (Event event : events) {
            if (this.timeConsistency(user, event)==0){
                for (int j = 0; j < event.getInvited().size(); j++) {
                    if (event.getInvited().get(j).getUser().equals(user)) {
                        event.getInvited().get(j).setInviteStatus(1);
                        
                    }
                }

                for (int i = 0; i < user.getEvents().size(); i++){
                    if (event.equals(user.getEvents().get(i).getEvent())){
                        user.getEvents().get(i).setInviteStatus(1);
                    }
                }
                em.merge(event);
                em.merge(user);
            }
            else{
                status = -2;
            }
        }
        
        return status;
    
    }

    public List<Update> getNotifies(User user) {
        return user.getNotifies();
    }

    public void setNotifyRead(Update u) {
        u.setRead(true);
        em.merge(u);
    }

    public void acceptInvite(User user, Event selectedEvent) {
        
        for (int i=0;i<selectedEvent.getInvited().size();i++){
            if (selectedEvent.getInvited().get(i).getUser().equals(user)){
                selectedEvent.getInvited().get(i).setInviteStatus(1);
            }
        }
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getEvent().equals(selectedEvent)){
                user.getEvents().get(i).setInviteStatus(1);
            }
        }
        em.merge(selectedEvent);
        em.merge(user);
    }
    
    public void denyInvite(User user, Event selectedEvent){
        
        for (int i=0;i<selectedEvent.getInvited().size();i++){
            if (selectedEvent.getInvited().get(i).getUser().equals(user)){
                selectedEvent.getInvited().get(i).setInviteStatus(-1);
            }
        }
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getEvent().equals(selectedEvent)){
                user.getEvents().get(i).setInviteStatus(-1);
            }
        }
        
        em.merge(selectedEvent);
        em.merge(user);
    }

    public List<Event> getInvites(User user) {
        List<Event> temp = new ArrayList<>();
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getInviteStatus() == 0){
                temp.add(user.getEvents().get(i).getEvent());
            }
        }
        return temp;
    }

    public int getNumberOfNotReadedNotifies(User user) {
        int count=0;
        for (Update u : user.getNotifies()){
            if (!u.isRead()){
                count++;
            }
        }
        return count;
    }

    public List<Contact> getContacts(User u) {
        return u.getContacts();
    }

    public void deleteContact(User user, String contactEmail) {
        ContactPK pk = new ContactPK(contactEmail,user.getEmail());
        Contact toBeRemoved = em.find(Contact.class, pk);
        user.getContacts().remove(toBeRemoved);
        em.remove(toBeRemoved);
    }
    
}
