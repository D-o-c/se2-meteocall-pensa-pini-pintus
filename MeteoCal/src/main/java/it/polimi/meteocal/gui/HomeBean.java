package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.UserArea;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 */
@Named
@RequestScoped
public class HomeBean {
    
    //Boundary
    @EJB
    UserArea userArea;
        
    /**
     * @return userArea.getName()
     */
    public String getName(){
        return userArea.getName();
    }
    
}
