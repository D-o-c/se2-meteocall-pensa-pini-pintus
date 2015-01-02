package it.polimi.meteocal.entity.primarykeys;

import java.io.Serializable;

/**
 *
 * @author aldo
 */
public class ContactPK implements Serializable{
    private String email;
    private String user;

    public ContactPK() {
    }
    
    /**
     * @param email: email del contatto
     * @param user : email dell'utente che possiede il contatto
     */
    public ContactPK(String email, String user) {
        this.email = email;
        this.user = user;
    }

    public boolean equals(Object object) {
        if (object instanceof ContactPK) {
            ContactPK pk = (ContactPK)object;
            return email.equals(pk.email) && user.equals(pk.user);
        } else {
            return false;
        }
    }

    public int hashCode() {
         return (int)(email.hashCode() + user.hashCode());
    }
    
    
}