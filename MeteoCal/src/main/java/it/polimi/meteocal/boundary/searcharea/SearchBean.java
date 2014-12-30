package it.polimi.meteocal.boundary.searcharea;

import it.polimi.meteocal.control.SearchManager;
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
    SearchManager sm;
    
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
    
    public String findUser() {
        sm.findUser(searchInput);
        return search_page_url;
    }
    
    public String findUser2() {
        sm.findUser(searchInput);
        return search_page_url_2;
    }
    
    public User getSelectedUser() {
        return sm.getSelectedUser();
    }
    
    public String selectUser(User searchedUser) {
        sm.selectUser(searchedUser);
        return selected_user_page_url;
    }
    
    public String resultsLabel() {
        return sm.resultsLabel();
    }
    
    public String contactsResultsLabel() {
        return sm.contactsResultsLabel();
    }
      
    public List<User> getUsers() {
        return sm.getUsersSearched();
    }
    
    public List<Contact> getContacts() {
        return sm.getContacts();
    }
    
    public String addContact() {
        sm.addContact(  getSelectedUser().getEmail(),
                        getSelectedUser().getName(),
                        getSelectedUser().getSurname()
                        );
        return user_addressbook_page_url;
    }
    
    public String deleteContact(Contact contact) {
        sm.deleteContact(contact);
        return addressbok_page_url;
    }
    
    public boolean exist(){
        return sm.exist(getSelectedUser().getEmail());
    }
}
