/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.primarykeys.ContactPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 */
public class SearchingManager {
    
    @PersistenceContext
    EntityManager em;

    public List<User> searchUser(String searchInput) {
        return em.createNamedQuery(User.findByEmailOrLikeNameSurname, User.class)
                                        .setParameter(1, searchInput+"%")
                                        .setParameter(2, searchInput)
                                        .getResultList();
    }

    public void addContact(User user, User friend) {
        Contact contact = new Contact(friend.getEmail(), friend.getName(), friend.getSurname(), user);
        em.persist(contact);
        user.addContact(contact);
        em.merge(user);
    }

    public boolean exist(User user, User friend) {
        ContactPK pk = new ContactPK(friend.getEmail(), user.getEmail());
        return user.getContacts().contains(em.find(Contact.class, pk));
    }
    
    
    
}
