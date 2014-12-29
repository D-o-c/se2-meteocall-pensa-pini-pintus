/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

/**
 *
 * @author doc
 */
@Entity(name = "CALENDAR")
@IdClass(CalendarPK.class)
public class Calendar implements Serializable{
    
    @Id
    private String userEmail;
    
    @Id
    private long idEvent;
    
    
    @NotNull
    private int inviteStatus;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="USEREMAIL", referencedColumnName="EMAIL")
    private User user;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="IDEVENT", referencedColumnName="IDEVENT")
    private Event event;

    
    public Calendar(){
        
    }
    
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(long idEvent) {
        this.idEvent = idEvent;
    }

    

    public void setInviteStatus(int inviteStatus) {
        this.inviteStatus=inviteStatus;
    }

    public int getInviteStatus() {
        return inviteStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
    
}
