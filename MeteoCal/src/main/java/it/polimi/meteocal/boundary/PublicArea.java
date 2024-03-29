package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.GuestManager;
import it.polimi.meteocal.entity.User;
import java.net.InetAddress;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
    
    public int sendPasswordToken(String s){
        return guestManager.sendToken(s);
    }

    public int changeLostPassword(String email, String token, String password) {
        return guestManager.changeLostPassword(email, token, password);
    }
    
}
