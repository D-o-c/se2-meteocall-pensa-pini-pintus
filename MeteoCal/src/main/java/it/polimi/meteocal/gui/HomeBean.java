/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    
    @EJB
    UserArea ua;
    
    public String getName(){
        return ua.getName();
    }
    
    
}
