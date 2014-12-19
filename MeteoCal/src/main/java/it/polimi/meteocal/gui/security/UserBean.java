/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui.security;

import it.polimi.meteocal.business.security.boundary.PublicArea;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class UserBean{

    @EJB
    PublicArea um;
    
    public UserBean() {
    }
    
    public String getName() {
        return um.getLoggedUser().getName();
    }
    
}