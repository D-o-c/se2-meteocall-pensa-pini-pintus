/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.entity;

import it.polimi.meteocal.entity.Event;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Anton
 */
@Entity (name= "WEATHER_CONDITION")
@IdClass(WeatherConditionPK.class)
public class WeatherCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Temporal(TemporalType.DATE)
    private Date time;
    
    @ManyToOne
    @Id
    @JoinColumn(name="ID")
    private Event event;
    
   
    private String type;
    
    public WeatherCondition(){
    }
    
    
    public WeatherCondition(Date time, Event event,String type){
        this.event=event;
        this.time=time;
        this.type=type;
    }
    
    
    public Date getTime() {
        return time;
     //   return null;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
