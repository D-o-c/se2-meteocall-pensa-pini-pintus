package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.WeatherCondition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
@Singleton
public class UpdateManager {

    //Constants
    private static final int one_day = 86400000;
    private static final int three_days = 259200000;
    //Codes
    private int[] badWeather;
    
    @PersistenceContext
    EntityManager em;
    //Control
    @Inject
    EmailSender emailSender;
    
    public void sendNotifies(){
        badWeather = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                    13, 14, 15, 16, 17, 18, 19, 35, 36, 37, 38,
                    39, 40, 41, 42, 43, 45, 46, 47};
                    
        Date today = new Date();
        
        List<Event> events = em.createNamedQuery(Event.findAll, Event.class).getResultList();
        
        //Bad weather one day before (for all partecipants):
        for (Event event : events) {
            //SEND NOTIFY IF
            //no notifies yet
            //today < event.begin <= today + 1 (less than a day to the event begin)
            //!event.end < today (event is not ended yet)
            if(event.getBeginTime().getTime() <= (new Date()).getTime() + one_day
                    && !event.isBwodb() &&
                    event.getBeginTime().after(today) &&
                    !event.getEndTime().before(today)){
                
                List<WeatherCondition> wcs = event.getWeatherConditions();
                for (WeatherCondition wc : wcs){
                    if(contains(badWeather, wc.getCode())){
                        for (Calendar c : event.getInvited()){
                            if (c.getInviteStatus()==1){
                                String desc = "For your tomorrow event bad weather is expected\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() + "\n" +
                                    "Begin Time: " + event.getBeginTime() + "\n" +
                                    "Location: " + event.getLocation();
                                emailSender.send(c.getUserEmail(),
                                    "Bad weather for you event",
                                    desc);
                                
                                desc = desc.replace("\n", "<br/>");
                                Update u = new Update();
                                u.setEvent(event);
                                u.setUser(c.getUser());
                                
                                u.setEmail(c.getUserEmail());
                                
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
            //SEND NOTIFY IF
            //no notifies yet
            //today < event.begin <= today + 3 (less than 3 days to the event begin)
            //!event.end < today (event is not ended yet)
            if(event.getBeginTime().getTime() <= (new Date()).getTime() + three_days
                    && !event.isBwtdb()  &&
                    event.getBeginTime().after(today) &&
                    !event.getEndTime().before(today)){
                
                List<WeatherCondition> wcs = event.getWeatherConditions();
                for (WeatherCondition wc : wcs){
                    if (contains(badWeather, wc.getCode())){
                        String sunnyDay = findSunnyDay(event, wc.getTime());
                        String desc = "For one of your next events bad weather is expected\n\n" + 
                                    "Name: " + event.getName() + "\n" + 
                                    "Description: " + event.getDescription() + "\n" +
                                    "Begin Time: " + event.getBeginTime() + "\n" +
                                    "Location: " + event.getLocation() + "\n\n" +
                                    "The first sunny day is: " + sunnyDay;
                        emailSender.send(event.getCreator().getEmail(),
                                "Bad weather for you event",
                                desc);
                                
                        desc = desc.replace("\n", "<br/>");
                        Update u = new Update();
                        u.setEvent(event);
                        u.setUser(event.getCreator());
                        
                        u.setEmail(event.getCreator().getEmail());
                        
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
            //STOP UPDATE WHEATHER CHANGES IF EVENT IS BEGIN YET
            //!(event.begin > today) --> (event.begin <= today)
            /*if (!event.getBeginTime().after(today)){
                break;
            }*/
            //STOP UPDATE WHEATHER CHANGES IF
            //event.end < today (event is ended)
            if (event.getEndTime().before(today)){
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
                            emailSender.send(c.getUserEmail(),
                                "Weather changed",
                                desc);
                            
                            desc = desc.replace("\n", "<br/>");
                            Update u = new Update();
                            u.setEvent(event);
                            u.setUser(c.getUser());
                            
                            u.setEmail(c.getUserEmail());
                            
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
    
    /**
     * 
     * @param event 
     */
    public void updateFromEventUpdate(Event event) {
        User creator = event.getCreator();
        Date today = new Date();
        for (Calendar c : event.getInvited()){
            //UPDATE EVENT CHANGES IF
            //user is invited
            //user is not the creator
            //!event.end < today  (event is not ended yet)
            //(It's still possible to change event info after event is ended)
            if (c.getInviteStatus() == 1 &&
                !c.getUser().equals(creator) &&
                !event.getEndTime().before(today)){
                String desc = "The info for this event is just changed\n\n" + 
                        "Name: " + event.getName() + "\n" + 
                        "Description: " + event.getDescription() + "\n"+
                        "Begin Time: " + event.getBeginTime() + "\n" +
                        "Location: " + event.getLocation();
                emailSender.send(c.getUserEmail(),
                    "Event info changed",
                    desc);
                
                desc = desc.replace("\n", "<br/>");
                Update u = new Update();
                u.setEvent(event);
                u.setUser(c.getUser());
                
                u.setEmail(c.getUserEmail());
                
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
    
    /**
     * @param event
     * @param d
     * @return first sunny day for an event 
     */
    private String findSunnyDay(Event event, Date d){
        List<Date> possibleDate = new ArrayList<>();
        
        for (WeatherCondition wc : event.getWeatherConditions()){
            if (!contains(badWeather, wc.getCode()) && wc.getTime().after(d)){
                possibleDate.add(wc.getTime());
            }
        }
        
        if (possibleDate.isEmpty()){
            return "Very hard to find it";
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
    
    /**
     * @param array : int[]
     * @param v : int
     * @return true if v is in array 
     */
    private boolean contains (int[] array, int v){
        for (int i: array){
            if (i==v)
                return true;
        }
        return false;
    }
 
}
