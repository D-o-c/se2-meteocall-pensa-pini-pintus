/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.WeatherCondition;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
public class EventManager {
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    EmailSender emailS;
    
    @Inject
    UpdateManager um;

    public boolean createEvent(Event event, List<String> invitedUsers, User creator) {
        event.setBwodb(false);
        event.setBwtdb(false);
        
        //Logged user is the creator of the event
        event.setCreator(creator);
        
        
        //persists the event into the database
        
        em.persist(event);
        
        //adds the event to creator's calendar
        event.addInvited(creator, 1);
        
        return sendInvite(event,invitedUsers);
    }
    
    private boolean sendInvite(Event e, List<String> iu){
        //delete people already invited
        List<Calendar> temp = e.getInvited();
        for (Calendar temp1 : temp) {
            iu.remove(temp1.getUserEmail());
        }
        
        //deletes the creator from users invited to the event
        iu.remove(e.getCreator().getEmail());
        //Checks the existance of the emails in the user database
        for (String invitedUser : iu) {
            User u = em.find(User.class, invitedUser);
            try {
                //if exists, add event to his calendar
                e.addInvited(u, 0);
                emailS.send(invitedUser,"Invite",
                        "You received an invitation to an event, log on MeteoCal to accept!");
                
            } catch (NullPointerException exc){
                return false;
            }
            
        }
        return true;
        
    }

    public boolean updateEvent(Event currentEvent, List<String> invitedUsers) {
        currentEvent.setBwodb(false);
        currentEvent.setBwtdb(false);
        
        boolean noErrors = sendInvite(currentEvent, invitedUsers);
        currentEvent.setWeatherConditions(new ArrayList<WeatherCondition>());
        currentEvent.setWeatherConditions(new ArrayList<WeatherCondition>());
        em.merge(currentEvent);
        um.updateFromEventUpdate(currentEvent);
        return noErrors;
    }

    public Event find(long eventId) {
        return em.find(Event.class, eventId);
    }

    public void removeFromPartecipants(Event event, User user) {
        for (int i=0;i<event.getInvited().size();i++){
            if (event.getInvited().get(i).getUser().equals(user)){
                event.getInvited().get(i).setInviteStatus(-1);
            }
        }
        for (int i = 0; i < user.getEvents().size(); i++){
            if (user.getEvents().get(i).getEventId()==event.getEventId()){
                user.getEvents().get(i).setInviteStatus(-1);
            }
        }
        em.merge(event);
        em.merge(user);
    }

    public List<User> getPartecipants(Event event) {
        List<User> temp = new ArrayList<>();
        for (int i = 0; i < event.getInvited().size(); i++){
            if (event.getInvited().get(i).getInviteStatus()==1){
                temp.add(event.getInvited().get(i).getUser());
            }
        }
        return temp;
    }

    public void deleteEvent(Event event) {
        for (Calendar c: event.getInvited()){
            if (c.getInviteStatus() == 1){
                emailS.send(c.getUserEmail(),
                                "Event deleted",
                                event.getName() + "has been deleted");
                
            }
            User u = c.getUser();
            
            for (int i = 0; i < u.getEvents().size(); i++){
                if (u.getEvents().get(i).getEvent().equals(event)){
                    u.getEvents().remove(i);
                    i--;
                }
            }

            for (int i = 0; i < u.getNotifies().size(); i++){
                if (u.getNotifies().get(i).getEvent().equals(event)){
                    u.getNotifies().remove(i);
                    i--;
                }
            }
            
            em.merge(u);
        }
        
        
            
        
    //    event.setWeatherConditions(new ArrayList<WeatherCondition>());
    //    event.setUpdate(new ArrayList<Update>());
    //    event.setInvited(new ArrayList<Calendar>());
        
        Event e = em.merge(event);
        em.remove(e);
    }
    
}
