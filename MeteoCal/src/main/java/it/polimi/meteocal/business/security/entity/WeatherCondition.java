/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Anton
 */
@Entity (name= "WEATHERCONDITION")
public class WeatherCondition implements Serializable {
    
    @Id
    @Temporal(TemporalType.DATE)
    private Date time;
    
    @ManyToOne
    @Id
    @JoinColumn(name="ID")
    private Event eventId;
    
    private String type;

    public Date getTime() {
        return time;
     //   return null;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event eventId) {
        this.eventId = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
