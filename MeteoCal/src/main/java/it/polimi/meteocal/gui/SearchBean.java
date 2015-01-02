package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.SearchArea;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.User;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author aldo
 */
@Named
@RequestScoped
public class SearchBean{

    private static final String search_page_url = "search?faces-redirect=true";
    private static final String search_page_url_2 = "user/search?faces-redirect=true";
    private static final String selected_user_page_url = "/usercalendar?faces-redirect=true";
    private static final String addressbok_page_url = "addressbook?faces-redirect=true";
    private static final String user_addressbook_page_url =
            "user/addressbook?faces-redirect=true";
    
    @EJB
    SearchArea sm;
    
    private String searchInput;
    
    public SearchBean() {}

    /**************************** Getter and Setter ***************************/
    public String getSearchInput() {
        return searchInput;
    }

    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }
    /**************************************************************************/
    
    /**
     * Calls SearchArea.findUser(searchinput) from a user/page
     * @return search page
     */
    public String findUser() {
        sm.findUser(searchInput);
        return search_page_url;
    }
    
    /**
     * Calls SearchArea.findUser(searchinput)
     * @return search page
     */
    public String findUser2() {
        sm.findUser(searchInput);
        return search_page_url_2;
    }
    
    /**
     * Calls SearchArea.getSelectedUser
     * @return the User "clicked" in the search page
     */
    public User getSelectedUser() {
        return sm.getSelectedUser();
    }
    
    /**
     * Calls SearchArea.selectUser(searchedUser) to set the User "clicked"
     * @return usercalendar page
     */
    public String selectUser(User searchedUser) {
        sm.selectUser(searchedUser);
        return selected_user_page_url;
    }
    
    /**
     * @return SearchArea.resultsLabel() e.g. "Results:/Results : Not Found"
     */
    public String resultsLabel() {
        return sm.resultsLabel();
    }
         
    /**
     * Calls SearchArea.getUserSearched()
     * @return List of users searched 
     */
    public List<User> getUsers() {
        return sm.getUsersSearched();
    }
    
    /**
     * Calls SearchArea.getContacts()
     * @return List of contacts of logged user
     */
    public List<Contact> getContacts() {
        return sm.getContacts();
    }
    
    /**
     * Calls SearchArea.addContact()
     * @return addressbook page
     */
    public String addContact() {
        sm.addContact(  getSelectedUser().getEmail(),
                        getSelectedUser().getName(),
                        getSelectedUser().getSurname()
                        );
        return user_addressbook_page_url;
    }
    
    /**
     * Calls SearchArea.deleteContact(String contactEmail)
     * @return addressbook page
     */
    public String deleteContact(String contactEmail) {
        sm.deleteContact(contactEmail);
        return addressbok_page_url;
    }
    
    /**
     * Calls SearchArea.exist()
     * @return if the user searched belongs to logged user contacts
     */
    public boolean exist(){
        return sm.exist(getSelectedUser().getEmail());
    }
}