/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import it.polimi.meteocal.entity.WeatherCondition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author doc
 */
@Singleton
@Lock(LockType.READ) // allows timers to execute in parallel
@Stateless
public class UpdateManager {

    
    
    @PersistenceContext
    EntityManager em;
    
    private final List<Long> bwodb = new ArrayList<>();
    private final List<Long> bwtdb = new ArrayList<>();
    
    @Schedule(minute="*", hour="*")
    public void sendNotifies(){
        
        List<Event> events = em.createNamedQuery(Event.findAll, Event.class).getResultList();
        
        
        //Bad weather one day before (for all partecipants):
        for (Event event : events) {
            if(event.getBeginTime().getTime() - 86400000 <= (new Date()).getTime() &&
                    !bwodb.contains(event.getEventId())){
                
                List<WeatherCondition> wcs = event.getWeatherConditions();
                for (WeatherCondition wc : wcs){
                    if (wc.getCode()<=15){
                        for (Calendar c : event.getInvited()){
                            if (c.getInviteStatus()==1){
                                String desc = "For your tomorrow event bad weather is expected\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() +
                                    "Begin Time: " + event.getBeginTime() +
                                    "Location: " + event.getLocation();
                                EmailSender.send(c.getUserEmail(),
                                    "Bad weather for you event",
                                    desc);
                                Update u = new Update();
                                u.setEvent(event);
                                u.setUser(c.getUser());
                                u.setDescription(desc);
                                u.setEventId(event.getEventId());
                                u.setRead(false);
                                event.addUpdate(u);
                                c.getUser().addNotify(u);
                                em.merge(event);
                                em.merge(c.getUser());
                            }
                        }
                        
                        bwodb.add(event.getEventId());
                        break;
                    }
                }
            }
        }
        
        //Bad weather three day before (only for creator):
        
        for (Event event : events) {
            if(event.getBeginTime().getTime() - 259200000 <= (new Date()).getTime() &&
                    !bwtdb.contains(event.getEventId())){
                
                List<WeatherCondition> wcs = event.getWeatherConditions();
                for (WeatherCondition wc : wcs){
                    if (wc.getCode()<=15){
                        String desc = "For your tomorrow event bad weather is expected\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() +
                                    "Begin Time: " + event.getBeginTime() +
                                    "Location: " + event.getLocation();
                        EmailSender.send(event.getCreator().getEmail(),
                                "Bad weather for you event",
                                desc);
                        Update u = new Update();
                        u.setEvent(event);
                        u.setUser(event.getCreator());
                        u.setDescription(desc);
                        u.setEventId(event.getEventId());
                        u.setRead(false);
                        event.addUpdate(u);
                        event.getCreator().addNotify(u);
                        em.merge(event);
                        em.merge(event.getCreator());
                        bwtdb.add(event.getEventId());
                        break;
                    }
                }
            }
        }
        
        //Update if weather changes
        for (Event event : events){
            List<WeatherCondition> wcs = event.getWeatherConditions();
            
            for (WeatherCondition wc : wcs){
                if (wc.getCode() != wc.getOldCode()){
                    
                    for (Calendar c : event.getInvited()){
                        if (c.getInviteStatus()==1){
                            String desc = "The weather for this event is just changed\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() +
                                    "Begin Time: " + event.getBeginTime() +
                                    "Location: " + event.getLocation();
                            EmailSender.send(c.getUserEmail(),
                                "Weather changed",
                                desc);
                            Update u = new Update();
                            u.setEvent(event);
                            u.setUser(c.getUser());
                            u.setDescription(desc);
                            u.setEventId(event.getEventId());
                            u.setRead(false);
                            event.addUpdate(u);
                            c.getUser().addNotify(u);
                            em.merge(event);
                            em.merge(c.getUser());
                        }
                    }
                    break;
                }
            }
            
        }
        
    }
    
    public void updateFromEventUpdate(Event event) {
        for (Calendar c : event.getInvited()){
            if (c.getInviteStatus()==1){
                String desc = "The info for this event is just changed\n\n" + 
                        "Name: " + event.getName() + "\n" + 
                        "Description: " + event.getDescription() +
                        "Begin Time: " + event.getBeginTime() +
                        "Location: " + event.getLocation();
                EmailSender.send(c.getUserEmail(),
                    "Event info changed",
                    desc);
                Update u = new Update();
                u.setEvent(event);
                u.setUser(c.getUser());
                u.setDescription(desc);
                u.setEventId(event.getEventId());
                u.setRead(false);
                event.addUpdate(u);
                c.getUser().addNotify(u);
                em.merge(event);
                em.merge(c.getUser());
            }
        }
        
    }
    
    
    
}
