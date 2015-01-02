/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author aldo
 */
public class WeatherConditionPK implements Serializable{
    private long eventId;
    private Date time;

    public WeatherConditionPK() {
    }

    public WeatherConditionPK(long eventId, Date time) {
        this.eventId = eventId;
        this.time = time;
    }
    

    public boolean equals(Object object) {
        if (object instanceof WeatherConditionPK) {
            WeatherConditionPK pk = (WeatherConditionPK)object;
            return eventId==pk.eventId && time.equals(pk.time);
        } else {
            return false;
        }
    }

    public int hashCode() {
         return (int)(eventId + time.hashCode());
    }
    
    
}
