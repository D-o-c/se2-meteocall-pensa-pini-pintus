package it.polimi.meteocal.entity.primarykeys;

import java.io.Serializable;

/**
 *
 */
public class CalendarPK implements Serializable{
    
    
    private String userEmail;
    private long eventId;
    
    public int hashCode() {
    return (int)(userEmail.hashCode() + eventId);
  }
 
  public boolean equals(Object object) {
    if (object instanceof CalendarPK) {
      CalendarPK otherId = (CalendarPK) object;
      return (otherId.userEmail.equals(this.userEmail)) && (otherId.eventId == this.eventId);
    }
    return false;
  }
    
}
