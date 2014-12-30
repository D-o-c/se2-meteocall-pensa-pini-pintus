package it.polimi.meteocal.boundary.publicarea;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
public class LoginBean {
    
    private static final String user_home_page_url = "/user/home?faces-redirect=true";
    private static final String index_page_url = "/index?faces-redirect=true";
    
    @Inject
    private Logger logger;
    
    private String username;
    private String password;

    /**
     * Empty Constructor
     */
    public LoginBean() {}

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
    /**************************************************************************/
    
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
                            "Login Failed","Login Failed"));
            logger.log(Level.SEVERE,"Login Failed");
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
        logger.log(Level.INFO, "User Logged out");
        return index_page_url;
    }
}
