package it.polimi.meteocal.gui;

import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.boundary.PublicArea;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
public class RegistrationBean {

    private static final String index_page_url = "/index?faces-redirect=true";
    
    @EJB
    private PublicArea sm;

    private User user;

    /**
     * Empty Constructor
     */
    public RegistrationBean() {}

    /**************************** Getter and Setter ***************************/
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
     * Calls PublicArea.register(User user) 
     */
    public void register() {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Info", "Trying to register..."));
        boolean ok = sm.register(user);
        if(ok) {
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
     * Calls PublicArea.unregister(), invalidate the session
     * @return index page
     */
    public String unregister() {
        sm.unregister();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Unregistration Successfull"));
        context.getExternalContext().getFlash().setKeepMessages(true);
        
        return index_page_url;
    }

}
