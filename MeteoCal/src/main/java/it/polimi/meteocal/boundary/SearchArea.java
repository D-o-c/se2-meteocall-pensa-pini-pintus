package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.GuestManager;
import it.polimi.meteocal.control.SearchingManager;
import it.polimi.meteocal.control.UserManager;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.primefaces.model.ScheduleModel;

/**
 *
 */
@Stateless
public class SearchArea {
    
    
    
    @Inject
    UserManager um;
    
    @Inject
    GuestManager gm;
    
    @Inject
    SearchingManager sm;
    
    @Inject
    Principal principal;
    
    List<User> usersSearched;
    User selectedUser;
    
    /**************************** Getter and Setter ***************************/
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
     * Search users by email or name or surname by a search input
     * @param searchInput
     */
    public void findUser(String searchInput) {
        usersSearched = sm.searchUser(searchInput);
        User loggedUser = gm.getLoggedUser();
        if(usersSearched.contains(loggedUser)) {
            usersSearched.remove(loggedUser);
        }
    }
    
    /**
     * Creates a new contact
     * Calls EntityManager.persist(contact)
     * Adds the contact to the logged user
     * @param email
     * @param name
     * @param surname 
     */
    public void addContact() {
        sm.addContact(gm.getLoggedUser(), selectedUser);
    }
    
    

    /**
     * Check if the logged user has the contact
     * Calls EntityManager.find(Contact.class, Primary Key)
     * @param contactEmail
     * @return if the logged user has the contact
     */
    public boolean exist() {
        return sm.exist(gm.getLoggedUser(), selectedUser);
    }

    public ScheduleModel getCalendar() {
        return um.getCalendar(selectedUser, gm.getLoggedUser());
    }
}
