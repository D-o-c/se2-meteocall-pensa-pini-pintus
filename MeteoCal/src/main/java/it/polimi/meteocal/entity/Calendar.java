package it.polimi.meteocal.entity;

import it.polimi.meteocal.entity.primarykeys.CalendarPK;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Entity(name = "CALENDAR")
@NamedQueries({
    @NamedQuery(name = Calendar.findByUserEmail, 
                    query = "SELECT c FROM CALENDAR c WHERE c.userEmail = ?1")
})
@IdClass(CalendarPK.class)
public class Calendar implements Serializable{
    
    public final static String findByUserEmail = "Calendar.findByUserEmail";
    
    @Id
    @Column(name = "USER_")
    private String userEmail;
    
    @Id
    @Column(name = "EVENT")
    private long eventId;
    
    
    @NotNull
    @Column(name = "INVITE_STATUS")
    private int inviteStatus;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="USER_", referencedColumnName="EMAIL")
    private User user;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="EVENT", referencedColumnName="ID")
    private Event event;

    public Calendar(){}
    
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
