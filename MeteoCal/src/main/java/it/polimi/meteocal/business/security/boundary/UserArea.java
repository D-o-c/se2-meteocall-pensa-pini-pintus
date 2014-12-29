package it.polimi.meteocal.business.security.boundary;

import it.polimi.meteocal.business.security.control.PasswordEncrypter;
import it.polimi.meteocal.business.security.entity.Contact;
import it.polimi.meteocal.business.security.entity.ContactPK;
import it.polimi.meteocal.business.security.entity.Group;
import it.polimi.meteocal.business.security.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author aldo
 */
@Stateless
public class UserArea {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;

    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    public List<User> findUser(String searchInput) {
        List<User> users = em.createNamedQuery(User.findByEmailOrLikeNameSurname, User.class)
                                .setParameter(1, searchInput+"%")
                                .setParameter(2, searchInput)
                                .getResultList();
        if(users.contains(getLoggedUser())) {
            users.remove(getLoggedUser());
        }
        return users; 
    }
    
    public void changeCalendarVisibility(boolean input) {
        String email = getLoggedUser().getEmail();
        em.find(User.class, email).setPublic(input);    
    }
    
    public boolean changeEmail(String email, String password) {
        String old_email = getLoggedUser().getEmail();
        String enc_old_psw = getLoggedUser().getPassword();
        String enc_new_psw = PasswordEncrypter.encryptPassword(password);
        if(enc_new_psw.equals(enc_old_psw)) {
            em.find(User.class, old_email).setEmail(email);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean changePassword(String password, String newPassword) {
        String email = getLoggedUser().getEmail();
        String enc_old_psw = getLoggedUser().getPassword();
        String enc_new_psw = PasswordEncrypter.encryptPassword(password);
        if(enc_new_psw.equals(enc_old_psw)) {
            em.find(User.class, email).setPassword(newPassword);
            return true;
        }
        else {
            return false;
        }
    }
    
    public List<Contact> getContacts() {
       /* String user_email = getLoggedUser().getEmail();
        List<Contact> contacts = em.createNamedQuery(Contact.findByUserEmail, Contact.class)
                                            .setParameter(1, user_email)
                                            .getResultList();
        return contacts;*/
        return getLoggedUser().getContacts();
    }
    
    public void addContact(String email, String name, String surname, User user) {
       Contact contact = new Contact(email, name, surname, user);
       em.persist(contact);
       getLoggedUser().addContact(contact);
    }
    
    public void deleteContact(String contact_email) {
        ContactPK pk = new ContactPK(contact_email,getLoggedUser().getEmail());
        Contact contact = em.find(Contact.class, pk);
        em.remove(contact);
    }

    public boolean exist(String cEmail) {
        List<Contact> cList=getContacts();
        ContactPK cpk = new ContactPK(cEmail, getLoggedUser().getEmail());
        return cList.contains(em.find(Contact.class, cpk));
    }
    
    
}
