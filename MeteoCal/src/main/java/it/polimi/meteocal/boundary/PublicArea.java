package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.EmailSender;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author aldo
 */
@Stateless
public class PublicArea {

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
     * Sets Group Name = Group.USERS
     * Sets the user to public
     * Calls EntityManager.persist(user)
     * @param user 
     */
    public boolean register(User user) {
        try {
            String s = em.find(User.class, user.getEmail()).getEmail();
            return false;
        } catch(NullPointerException e) {
            user.setGroupName(Group.USERS);
            user.setPublic(true);
            em.persist(user);
            //EmailSender.send(user.getEmail() ,
            //            "MeteoCal Registration",
            //            "Congratulations, you signed up on MeteoCal successfully");
            return true;
        }       
    }

    /**
     * Calls loggedUser = this.getLoggedUser()
     * Calls EntityManager.remove(loggedUser)
     */
    public void unregister() {
        User loggedUser = getLoggedUser();
        em.remove(loggedUser);
    }
    
}
