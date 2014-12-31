package it.polimi.meteocal.entity.primarykeys;

import java.io.Serializable;

/**
 *
 * @author aldo
 */
public class UpdatePK implements Serializable{
    
    private long number;
    private long eventId;

    public UpdatePK() {
    }

    public UpdatePK(long number, long eventId) {
        this.number = number;
        this.eventId = eventId;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof UpdatePK) {
            UpdatePK pk = (UpdatePK)object;
            return number == pk.number && eventId == pk.eventId;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
         return (int)(number + eventId);
    }
    
    
}
