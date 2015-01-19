package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.GuestManager;
import it.polimi.meteocal.entity.User;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 */
@Stateless
public class PublicArea {
    
    //Controls
    @Inject
    GuestManager guestManager;
    
    /**
     * Calls guestManager.register(user)
     * @param user
     * @return true if registration is ok
     */
    public boolean register (User user){
        return guestManager.register(user);
    }
    
    /**
     * Calls guestManager.unregister(guestManager.getLoggedUser())
     */
    public void unregister(){
        guestManager.unregister(guestManager.getLoggedUser());
    }
    
}
