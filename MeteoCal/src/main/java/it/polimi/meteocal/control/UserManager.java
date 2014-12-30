package it.polimi.meteocal.control;

import it.polimi.meteocal.business.security.PasswordEncrypter;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.ContactPK;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author aldo
 */
@Stateless
public class UserManager {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    /**
     * Calls EntityManager.find(User.class, principal.getName())
     * @return the logger user
     */
    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    /**
     * Calls EntityManager.find(User.class, PrimaryKey)
     * Sets the logged user privacy
     * @param input 
     */
    public void changeCalendarVisibility(boolean input) {
        em.find(User.class, getLoggedUser().getEmail()).setPublic(input);    
    }
    
    /**
     * Controls validity of input password and possibly save that password
     * @param inputPassword
     * @param newPassword
     * @return if change has been made correctly
     */
    public boolean changePassword(String inputPassword, String newPassword) {
        String email = getLoggedUser().getEmail();
        String enc_old_psw = getLoggedUser().getPassword();
        String enc_new_psw = PasswordEncrypter.encryptPassword(inputPassword);
        if(enc_new_psw.equals(enc_old_psw)) {
            //update password
            em.find(User.class, email).setPassword(newPassword);
            return true;
        }
        else {
            return false;
        }
    }
    
}
