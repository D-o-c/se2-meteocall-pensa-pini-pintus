/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import boundaries.PublicArea;
import entities.User;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author doc
 */
@Named
@RequestScoped
public class RegistrationBean {

    @EJB
    private PublicArea um;

    private User user;

    public RegistrationBean() {
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String register() {
        um.save(user);
        return "user/home?faces-redirect=true";
    }

}
