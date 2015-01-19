package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.Contact;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 */
@Named
@RequestScoped
public class AddressBookBean {
    
    //String
    private static final String user_addressbook_page_url = "/user/addressbook?faces-redirect=true";

    //Boundary
    @EJB
    UserArea userArea;
    
    /**
     * @return userArea.getContacts()
     */
    public List<Contact> getContacts() {
        return userArea.getContact();
    }
    
    /**
     * Calls userArea.deleteContact(Contact contact)
     * @param contact = contact to be deleted 
     * @return /user/addressbook?faces-redirect=true
     */
    public String deleteContact(Contact contact) {
        userArea.deleteContact(contact);
        return user_addressbook_page_url;
    }
    
}
