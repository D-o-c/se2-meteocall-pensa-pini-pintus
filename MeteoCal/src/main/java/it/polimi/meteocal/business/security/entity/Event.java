/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
    private long idEvent;
    
    public static final String findAll = "Event.findAll";
    
    
    @OneToMany(mappedBy="event", cascade = CascadeType.PERSIST)
    private List<Calendar> invited;
    
    
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
    
    
    public String getName() {
        return name;
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

    public List<Calendar> getInvited() {
        return invited;
    }

    public long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(long idEvent) {
        this.idEvent = idEvent;
    }
    
    public void addInvited(User user) {
        Calendar calendar = new Calendar();
        calendar.setUser(user);
        calendar.setEvent(this);
        calendar.setIdEvent(this.getIdEvent());
        calendar.setUserEmail(user.getEmail());
        calendar.setInviteStatus(0);
        
        if (invited==null){
            invited=new ArrayList<>();
        }
        this.invited.add(calendar);
        user.getEvents().add(calendar);
    }
    
 
    
}
