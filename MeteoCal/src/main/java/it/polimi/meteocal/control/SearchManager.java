package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.primarykeys.ContactPK;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author user
 */
@Stateless
public class SearchManager {
    
    @PersistenceContext
    EntityManager em;
    
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
     * Calls EntityManager.find(User.class, principal.getName())
     * @return the logger user
     */
    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    /**
     * Search users by email or name or surname by a search input
     * @param searchInput
     */
    public void findUser(String searchInput) {
        usersSearched = em.createNamedQuery(User.findByEmailOrLikeNameSurname, User.class)
                                .setParameter(1, searchInput+"%")
                                .setParameter(2, searchInput)
                                .getResultList();
        User loggedUser = getLoggedUser();
        if(usersSearched.contains(loggedUser)) {
            usersSearched.remove(loggedUser);
        }
    }
    
    /**
     * Label if usersSearched.isEmpty()
     * @return "Results : Not Found"
     */
    public String resultsLabel() {
        if(usersSearched.isEmpty()) return "Results : Not Found";
        else return "Results :";
    }
    
    /**
     * @return List of contacts of the logged user
     */
    public List<Contact> getContacts() {
        return getLoggedUser().getContacts();
    }
    
    /**
     * Creates a new contact
     * Calls EntityManager.persist(contact)
     * Adds the contact to the logged user
     * @param email
     * @param name
     * @param surname 
     */
    public void addContact(String email, String name, String surname) {
       Contact contact = new Contact(email, name, surname, getLoggedUser());
       em.persist(contact);
       getLoggedUser().addContact(contact);
    }
    
    /**
     * Deletes a contact of the logged user
     * Calls EntityManager.find(Contact.class, Primary Key)
     * Calls EntityManager.remove(contact)
     * @param contact 
     */
    public void deleteContact(String contactEmail) {
        ContactPK pk = new ContactPK(contactEmail,getLoggedUser().getEmail());
        Contact toBeRemoved = em.find(Contact.class, pk);
        getLoggedUser().getContacts().remove(toBeRemoved);
        em.remove(toBeRemoved);
    }

    /**
     * Check if the logged user has the contact
     * Calls EntityManager.find(Contact.class, Primary Key)
     * @param contactEmail
     * @return if the logged user has the contact
     */
    public boolean exist(String contactEmail) {
        ContactPK pk = new ContactPK(contactEmail, getLoggedUser().getEmail());
        return getLoggedUser().getContacts().contains(em.find(Contact.class, pk));
    }
}
