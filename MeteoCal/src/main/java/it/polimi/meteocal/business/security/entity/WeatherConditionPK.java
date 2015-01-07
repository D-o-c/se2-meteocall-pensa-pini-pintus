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
    private long event;
    private Date time;

    public WeatherConditionPK() {
    }

    public WeatherConditionPK(long event, Date time) {
        this.event = event;
        this.time = time;
    }
    

    public boolean equals(Object object) {
        if (object instanceof WeatherConditionPK) {
            WeatherConditionPK pk = (WeatherConditionPK)object;
            return event==pk.event && time.equals(pk.time);
        } else {
            return false;
        }
    }

    public int hashCode() {
         return (int)(event + time.hashCode());
    }
    
    
}
