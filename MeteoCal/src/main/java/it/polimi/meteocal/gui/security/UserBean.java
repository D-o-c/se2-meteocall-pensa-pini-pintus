/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui.security;

import it.polimi.meteocal.business.security.boundary.UserManager;
import it.polimi.meteocal.business.security.entity.User;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
public class UserBean{

    @EJB
    UserManager um;
    
    private User user;
    
    public UserBean() {
    }

    public User getUser() {
        if (user==null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String getName() {
        return um.getLoggedUser().getName();
    }
    
    public String getEmail() {
        return um.getLoggedUser().getEmail();
    }
      
}
