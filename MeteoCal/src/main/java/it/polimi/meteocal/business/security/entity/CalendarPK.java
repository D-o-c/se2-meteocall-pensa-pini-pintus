/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.entity;

import java.io.Serializable;

/**
 *
 * @author doc
 */
public class CalendarPK implements Serializable{
    
    
    private String userEmail;
    private long idEvent;
    
    public int hashCode() {
    return (int)(userEmail.hashCode() + idEvent);
  }
 
  public boolean equals(Object object) {
    if (object instanceof CalendarPK) {
      CalendarPK otherId = (CalendarPK) object;
      return (otherId.userEmail.equals(this.userEmail)) && (otherId.idEvent == this.idEvent);
    }
    return false;
  }
    
}
