package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.GuestManager;
import it.polimi.meteocal.control.SearchingManager;
import it.polimi.meteocal.control.UserManager;
import it.polimi.meteocal.entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.primefaces.model.ScheduleModel;

/**
 *
 */
@Stateless
public class SearchArea {
    
    //Controls
    @Inject
    UserManager userManager;
    @Inject
    GuestManager guestManager;
    @Inject
    SearchingManager searchingManager;
        
    //list of results of the search
    List<User> usersSearched;
    //user selected from that list
    User selectedUser;
    
    public List<User> getUsersSearched() {
        return usersSearched;
    }
    
    public void selectUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public User getSelectedUser() {
        return selectedUser;
    }
    
    /**************************************************************************/
    
    /**
     * Calls searchingManager.searchMatchingUser(searchInput, loggedUser)
     * @param searchInput = input string
     */
    public void findUser(String searchInput) {
        usersSearched = searchingManager.searchMatchingUser(searchInput, guestManager.getLoggedUser());
    }
    
    /**
     * Calls searchingManager.addContact(loggedUser, selectedUser)
     */
    public void addContact() {
        searchingManager.addContact(guestManager.getLoggedUser(), selectedUser);
    }
    
    /**
     * @return searchingManager.contactExist(loggedUser, selectedUser);
     */
    public boolean contactExist() {
        return searchingManager.contactExist(guestManager.getLoggedUser(), selectedUser);
    }

    /**
     * @return searchingManager.getCalendar(selectedUser)
     */
    public ScheduleModel getCalendar() {
        return searchingManager.getCalendar(selectedUser);
    }
}
