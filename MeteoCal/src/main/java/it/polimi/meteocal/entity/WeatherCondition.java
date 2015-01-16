/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.entity;

import it.polimi.meteocal.entity.primarykeys.WeatherConditionPK;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
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
    @Column(name="TIME_")
    private Date time;
    
    @ManyToOne
    @Id
    @JoinColumn(name="EVENT_ID", referencedColumnName="ID")
    private Event event;
    
    @Column(name="CODE")
    private int code;
    
    @Column(name="TEMPERATURE")
    private int temp;
    
    @Column(name="OLD_CODE")
    private int oldCode;
    
    @Column(name="TYPE")
    private String type;
    
    public WeatherCondition(){
    }
    
    
    public WeatherCondition(Date time, Event event,String type, int code){
        this.event=event;
        this.time=time;
        this.type=type;
        this.code=this.oldCode=code;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getOldCode() {
        return oldCode;
    }

    public void setOldCode(int oldCode) {
        this.oldCode = oldCode;
    }
    
}
