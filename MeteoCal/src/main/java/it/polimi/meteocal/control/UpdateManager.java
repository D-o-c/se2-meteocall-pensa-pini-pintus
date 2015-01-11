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
@Lock(LockType.WRITE) //not allows timers to execute in parallel
@Stateless
public class UpdateManager {

    
    
    @PersistenceContext
    EntityManager em;
    
    @Schedule(minute="*", hour="*")
    public void sendNotifies(){
         Date today = new Date();
        //em.flush();
        
        List<Event> events = em.createNamedQuery(Event.findAll, Event.class).getResultList();
        
        
        //Bad weather one day before (for all partecipants):
        for (Event event : events) {
            if(event.getBeginTime().getTime() - 86400000 <= (new Date()).getTime() &&
                    !event.isBwodb() && event.getBeginTime().after(today)){
                
                List<WeatherCondition> wcs = event.getWeatherConditions();
                for (WeatherCondition wc : wcs){
                    if (wc.getCode()<=15){
                        for (Calendar c : event.getInvited()){
                            if (c.getInviteStatus()==1){
                                String desc = "For your tomorrow event bad weather is expected\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() + "\n" +
                                    "Begin Time: " + event.getBeginTime() + "\n" +
                                    "Location: " + event.getLocation();
                                EmailSender.send(c.getUserEmail(),
                                    "Bad weather for you event",
                                    desc);
                                desc = desc.replace("\n", "<br/>");
                                Update u = new Update();
                                u.setEvent(event);
                                u.setUser(c.getUser());
                                u.setDescription(desc);
                                u.setEventId(event.getEventId());
                                u.setRead(false);
                                event.addUpdate(u);
                                c.getUser().addNotify(u);
                                event.setBwodb(true);
                                em.persist(u);
                                em.merge(event);
                                em.merge(c.getUser());
                                //em.flush();
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        //Bad weather three day before (only for creator):
        
        for (Event event : events) {
            if(event.getBeginTime().getTime() - 259200000 <= (new Date()).getTime() &&
                    !event.isBwtdb()  && event.getBeginTime().after(today)){
                
                List<WeatherCondition> wcs = event.getWeatherConditions();
                for (WeatherCondition wc : wcs){
                    if (wc.getCode()<=15){
                        String sunnyDay = findSunnyDay(event, wc.getTime());
                        String desc = "For one of your next events bad weather is expected\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() + "\n" +
                                    "Begin Time: " + event.getBeginTime() + "\n" +
                                    "Location: " + event.getLocation() + "\n\n" +
                                    "The first sunny day is: " + sunnyDay;
                        EmailSender.send(event.getCreator().getEmail(),
                                "Bad weather for you event",
                                desc);
                        desc = desc.replace("\n", "<br/>");
                        Update u = new Update();
                        u.setEvent(event);
                        u.setUser(event.getCreator());
                        u.setDescription(desc);
                        u.setEventId(event.getEventId());
                        u.setRead(false);
                        event.addUpdate(u);
                        event.getCreator().addNotify(u);
                        event.setBwtdb(true);
                        em.persist(u);
                        em.merge(event);
                        em.merge(event.getCreator());
                        break;
                    }
                }
            }
        }
        
        //Update if weather changes
        for (Event event : events){
            
            if (!event.getBeginTime().after(today)){
                break;
            }
            List<WeatherCondition> wcs = event.getWeatherConditions();
            
            for (WeatherCondition wc : wcs){
                if (wc.getCode() != wc.getOldCode()){
                    
                    for (Calendar c : event.getInvited()){
                        if (c.getInviteStatus()==1){
                            String desc = "The weather for this event is just changed\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() + "\n" +
                                    "Begin Time: " + event.getBeginTime() + "\n" +
                                    "Location: " + event.getLocation();
                            EmailSender.send(c.getUserEmail(),
                                "Weather changed",
                                desc);
                            desc = desc.replace("\n", "<br/>");
                            Update u = new Update();
                            u.setEvent(event);
                            u.setUser(c.getUser());
                            u.setDescription(desc);
                            u.setEventId(event.getEventId());
                            u.setRead(false);
                            event.addUpdate(u);
                            c.getUser().addNotify(u);
                            wc.setOldCode(wc.getCode());
                            em.persist(u);
                            em.merge(wc);
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
                        "Description: " + event.getDescription() + "\n"+
                        "Begin Time: " + event.getBeginTime() + "\n" +
                        "Location: " + event.getLocation();
                EmailSender.send(c.getUserEmail(),
                    "Event info changed",
                    desc);
                desc = desc.replace("\n", "<br/>");
                Update u = new Update();
                u.setEvent(event);
                u.setUser(c.getUser());
                u.setDescription(desc);
                u.setEventId(event.getEventId());
                u.setRead(false);
                event.addUpdate(u);
                c.getUser().addNotify(u);
                em.persist(u);
                em.merge(event);
                em.merge(c.getUser());
            }
        }
        
    }
    
    private String findSunnyDay(Event e, Date d){
        List<Date> possibleDate = new ArrayList<>();
        
        for (WeatherCondition wc : e.getWeatherConditions()){
            if (wc.getCode() >= 20 && wc.getTime().after(d)){
                possibleDate.add(wc.getTime());
            }
        }
        if (possibleDate.isEmpty()){
            return "very hard to find it";
        }
        
        int choosed = 0;
        long between = possibleDate.get(0).getTime() - d.getTime();
        for (int i = 1; i < possibleDate.size(); i++){
            if (possibleDate.get(i).getTime() - d.getTime() < between){
                between = possibleDate.get(i).getTime() - d.getTime();
                choosed = i;
            }
        }
        
        return possibleDate.get(choosed).toString();
        
    }
    
    
    
}
