package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.GuestManager;
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
public class PublicArea {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    @Inject
    GuestManager gm;
    
    
    
    public boolean register (User u){
        return gm.register(u);
    }
    
    public void unregister(){
        gm.unregister(gm.getLoggedUser());
    }
    
}
