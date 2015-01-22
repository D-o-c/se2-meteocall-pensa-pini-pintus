package it.polimi.meteocal.entity.primarykeys;

import java.io.Serializable;

/**
 *
 */
public class UpdatePK implements Serializable{
    
    private long number;
    private long eventId;
    private String email;

    /**
     * Empty Constructor
     */
    public UpdatePK() {}

    /**
     * Constructor
     * @param number
     * @param eventId
     * @param email 
     */
    public UpdatePK(long number, long eventId, String email) {
        this.number = number;
        this.eventId = eventId;
        this.email = email;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof UpdatePK) {
            UpdatePK pk = (UpdatePK)object;
            return number == pk.number && eventId == pk.eventId && email.equals(pk.email);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
         return (int)(number + eventId + email.hashCode());
    }
    
    
}
