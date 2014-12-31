package it.polimi.meteocal.entity;

import it.polimi.meteocal.entity.primarykeys.UpdatePK;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Anton
 */
@Entity (name = "UPDATE")
@IdClass(UpdatePK.class)
public class Update implements Serializable {
    private static final long serialVersionUID = 1L;
   
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "UPDATE_NUMBER")
    private long number;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "ID")
    private Event eventId;
    
    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event event) {
        this.eventId = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    


    
}
