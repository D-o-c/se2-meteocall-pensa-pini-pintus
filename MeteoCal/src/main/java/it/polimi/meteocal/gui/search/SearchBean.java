/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui.search;

import it.polimi.meteocal.business.security.boundary.PublicArea;
import it.polimi.meteocal.business.security.entity.User;
import java.util.List;
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
    PublicArea pa;
    
    private String email;
    
    private List<User> users;
    
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public String findAll() {
        users = pa.findAll();
        return "search?faces-redirect=true";
    }
    
    public String findAll2() {
        users = pa.findAll();
        return "";
    }
    
    public String getUsersInfo() {
        String s = "";
        for(int i = 0; i < users.size(); i++) {
            s += users.get(i).getName()+" "+users.get(i).getSurname()+"---";
        }
        return s;
    }
      
}
