package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.SearchArea;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 */
@Named
@RequestScoped
public class UserCalendarBean {
    
    private static final String user_addressbook_page_url = "/user/addressbook?faces-redirect=true";

    //Boundaries
    @EJB
    SearchArea searchArea;
    @EJB
    EventArea eventArea;
        
    /**
     * Calls searchArea.addContact()
     * @return /user/addressbook?faces-redirect=true
     */
    public String addContact() {
        searchArea.addContact();
        return user_addressbook_page_url;
    }
        
    /**
     * @return searchArea.contactExist()
     */
    public boolean contactExist(){
        return searchArea.contactExist();
    }
        
    /**
     * @return searchArea.getSelectedUser().getName()
     */
    public String getSelectedUserName(){
        return searchArea.getSelectedUser().getName();
    }
    
    /**
     * @return searchArea.getSelectedUser().getSurname()
     */
    public String getSelectedUserSurname(){
        return searchArea.getSelectedUser().getSurname();
    }
    
    /**
     * @return searchArea.getSelectedUser().getEmail()
     */
    public String getSelectedUserEmail(){
        return searchArea.getSelectedUser().getEmail();
    }
    
}
