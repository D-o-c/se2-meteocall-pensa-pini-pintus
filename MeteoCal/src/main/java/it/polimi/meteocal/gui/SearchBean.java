package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.SearchArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.User;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 */
@Named
@RequestScoped
public class SearchBean{

    //Strings
    private static final String search_page_url = "/user/search?faces-redirect=true";
    private static final String usercalendar_page_url = "/usercalendar?faces-redirect=true";
    
    //Boundaries
    @EJB
    SearchArea searchArea;
    @EJB
    UserArea userArea;
    @EJB
    EventArea eventArea;
    
    //Input string to search users
    private String searchInput;
    
    /**
     * Empty Constructor
     */
    public SearchBean() {}

    /**
     * @return searchInput
     */
    public String getSearchInput() {
        return searchInput;
    }

    /**
     * @param searchInput 
     */
    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }
    
    /**************************************************************************/
    
    /**
     * Calls searchArea.findUser(searchInput)
     * @return /user/search?faces-redirect=true
     */
    public String findUser() {
        searchArea.findUser(searchInput);
        return search_page_url;
    }
        
    /**
     * @return searchArea.getSelectedUser()
     */
    public User getSelectedUser() {
        return searchArea.getSelectedUser();
    }
    
    /**
     * Calls SearchArea.selectUser(searchedUser)
     * @param searchedUser
     * @return /usercalendar?faces-redirect=true
     */
    public String selectUser(User searchedUser) {
        searchArea.selectUser(searchedUser);
        return usercalendar_page_url;
    }
             
    /**
     * Calls SearchArea.getUserSearched()
     * @return searchArea.getUsersSearched() 
     */
    public List<User> getUsersSearched() {
        return searchArea.getUsersSearched();
    }
    
    
    
    
    
    
    
    
    
}
