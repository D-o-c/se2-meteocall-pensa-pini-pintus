/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author Anton
 */


@Entity (name="EVENT")
@NamedQueries({
        @NamedQuery(name = Event.findAll, 
                query = "SELECT e FROM EVENT e")
})
public class Event implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idEvent;
    
    public static final String findAll = "Event.findAll";
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "May not be empty")
    private Date beginTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "May not be empty")
    private Date endTime;
    
    @NotNull(message = "May not be empty")
    private String name;
    
    @NotNull(message = "May not be empty")
    private String description;
    
    @NotNull(message = "May not be empty")
    private boolean pub=true;
    
    @NotNull(message = "May not be empty")
    private String creatorEmail;
    
    @Pattern(regexp="[\\w*[ ]*]*[,][\\w*[ ]*]*[,][\\w*[ ]*]*",
             message="write \"address, city, state\"")
    @NotNull(message = "May not be empty")
    private String location;
    
    @OneToMany(mappedBy="event", orphanRemoval=true)
    private List<WeatherCondition> weatherConditions;
    
    public int getId(){
        return idEvent;
    }
    public String getName() {
        return name;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public List<WeatherCondition> getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(List<WeatherCondition> weatherConditions) {
        this.weatherConditions = weatherConditions;
    }

    
    public void setName(String name) {
        this.name = name;
    }
    
    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location=location;
    }
    
    public void addWeatherCondition(WeatherCondition wc){
        weatherConditions.add(wc);
        
    }
    
}
