package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author aldo
 */
@Stateless
public class SignManager {

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
    public void register(User user) {        
        user.setGroupName(Group.USERS);
        user.setPublic(true);
        em.persist(user);        
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
