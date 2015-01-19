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
    
    //Strings
    private static final String info = "Info";
    private static final String error = "Error";
    private static final String registration_successfull = "Registration Successfull";
    private static final String registration_failed = "Registration Failed";
    private static final String login_failed = "Login Failed";
    private static final String user_home_page_url = "/user/home?faces-redirect=true";
    private static final String index_page_url = "/index?faces-redirect=true";
    
    //Boundary
    @EJB
    PublicArea publicArea;
        
    //User credentials
    private User user;
    private String username;
    private String password;

    /**
     * Empty Constructor
     */
    public IndexBean() {}

    /**
     * @return user
     */
    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }
    
    /**
     * @return username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password 
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**************************************************************************/
    
    /**
     * Calls publicArea.register(user) 
     */
    public void register() {
       
        boolean registrationIsOk = publicArea.register(user);
        
        if(registrationIsOk) {
            this.username = user.getEmail();
            user = new User();
            FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        registration_successfull, registration_successfull));
        }
        else {
            FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, registration_failed));
        }
        
    }
    
    /**
     * Login
     * @return /user/home?faces-redirect=true
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
                            login_failed, login_failed));
            return null;
        }
    }
    
    /**
     * Logout
     * @return /index?faces-redirect=true
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        return index_page_url;
    }
    
}
