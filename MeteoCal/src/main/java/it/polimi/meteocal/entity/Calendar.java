package it.polimi.meteocal.entity;

import it.polimi.meteocal.entity.primarykeys.CalendarPK;
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
    @Column(name = "USER_EMAIL")
    private String userEmail;
    
    @Id
    @Column(name = "EVENT_ID")
    private long eventId;
    
    
    @NotNull
    @Column(name = "INVITE_STATUS")
    private int inviteStatus;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="USER_EMAIL", referencedColumnName="EMAIL")
    private User user;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="EVENT_ID", referencedColumnName="ID")
    private Event event;

    //ATTENZIONE NON CANCELLARE IL COSTRUTTORE!!!!!
    public Calendar(){
        
    }
    
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
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
