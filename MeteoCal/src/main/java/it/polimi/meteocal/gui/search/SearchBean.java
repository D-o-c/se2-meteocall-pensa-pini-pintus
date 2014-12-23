/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui.search;

import it.polimi.meteocal.business.security.boundary.UserManager;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
public class SearchBean{

    @EJB
    UserManager um;
    
    private String email;
    
    public SearchBean() {
    }

    public String getEmail() {
        if (email==null) {
            email = "";
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String find() {
        return um.find(email);
    }
      
}
