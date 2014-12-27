/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui.security;

import it.polimi.meteocal.business.security.boundary.PublicArea;
import it.polimi.meteocal.business.security.entity.User;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
public class RegistrationBean {

    @EJB
    private PublicArea pa;

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
        pa.save(user);
        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Registration Successfull",
                                    ""));
        return "";
    }
    
    public String unregister() {
        pa.unregister();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        return "/index?faces-redirect=true";
    }

}
