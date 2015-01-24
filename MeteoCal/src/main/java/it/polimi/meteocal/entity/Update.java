package it.polimi.meteocal.entity;

import it.polimi.meteocal.entity.primarykeys.UpdatePK;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Entity (name = "UPDATE_")
@IdClass(UpdatePK.class)
@NamedQueries({
    @NamedQuery(name = Update.countNotRead, 
                    query = "SELECT COUNT(u) FROM UPDATE_ u WHERE u.read = false AND u.email = ?1"),
    @NamedQuery(name = Update.findByRecipient, 
                query = "SELECT u FROM UPDATE_ u WHERE u.user.email = ?1")
})
public class Update implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String countNotRead = "Update.countNotRead";
    public static final String findByRecipient = "Update.findByRecipient";
    
    @Id
    @Column(name = "EVENT")
    private long eventId;
    
    @Id
    @Column(name = "RECIPIENT")
    private String email;
   
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "UPDATE_NUMBER")
    private long number;
    
    @NotNull
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="RECIPIENT", referencedColumnName="EMAIL")
    private User user;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name="EVENT", referencedColumnName="ID")
    private Event event;
    
    @Column(name = "READ_")
    @NotNull
    private boolean read;

    public Update() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
    
    public boolean equals(Update u){
        return event.equals(u.event) && user.equals(u.user) && number == u.number;
    }

    
    
}
