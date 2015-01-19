package it.polimi.meteocal.entity.primarykeys;

import java.io.Serializable;

/**
 *
 */
public class ContactPK implements Serializable{
    
    private String email;
    private String user;

    /**
     * Empty Constructor
     */
    public ContactPK() {}
    
    /**
     * @param email: email of the contact
     * @param user : email of the user owner of the contact
     */
    public ContactPK(String email, String user) {
        this.email = email;
        this.user = user;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ContactPK) {
            ContactPK pk = (ContactPK)object;
            return email.equals(pk.email) && user.equals(pk.user);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
         return (int)(email.hashCode() + user.hashCode());
    }
    
    
}
