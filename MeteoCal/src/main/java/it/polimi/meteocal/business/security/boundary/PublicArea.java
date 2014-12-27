package it.polimi.meteocal.business.security.boundary;

import it.polimi.meteocal.business.security.entity.Group;
import it.polimi.meteocal.business.security.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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

    public void save(User user) {        
        user.setGroupName(Group.USERS);
        em.persist(user);        
    }

    public void unregister() {
        em.remove(getLoggedUser());
    }

    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    public List<User> findUser(String searchInput) {
        List<User> users = em.createNamedQuery(User.findByEmailOrLikeNameSurname, User.class)
                                .setParameter(1, searchInput+"%")
                                .setParameter(2, searchInput)
                                .getResultList();
        if(users.contains(getLoggedUser())) {
            users.remove(getLoggedUser());
        }
        return users; 
    }
    
    

}
