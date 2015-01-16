/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.PublicArea;
import it.polimi.meteocal.entity.User;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Named
@RequestScoped
public class IndexBean {
    
    private static final String user_home_page_url = "/user/home?faces-redirect=true";
    private static final String index_page_url = "/index?faces-redirect=true";
    
    @EJB
    PublicArea pa;
    
 //   @Inject
 //   private Logger logger;
    
    private String username;
    private String password;
    private User user;

    /**
     * Empty Constructor
     */
    public IndexBean() {}

    /**************************** Getter and Setter ***************************/
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    /**************************************************************************/
    
    
    /**
     * Calls PublicArea.register() 
     */
    public void register() {
       
        if(pa.register(user)) {
            this.username = user.getEmail();
            user = new User();
            FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Info", "Registration Successfull"));
        }
        else {
            FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error", "Registration Failed"));
        }
        
    }
    
    
    
    
    /**
     * Login
     * @return user_home_page_url
     */
    public String login() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context
                .getExternalContext().getRequest();
        try {
            request.login(this.username, this.password);
            return user_home_page_url;
        } catch (ServletException e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error","Login Failed"));
 //         logger.log(Level.SEVERE,"Login Failed");
            return null;
        }
    }
    
    /**
     * Logout
     * @return index_page_url
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
  //      logger.log(Level.INFO, "User Logged out");
        return index_page_url;
    }
    
}
