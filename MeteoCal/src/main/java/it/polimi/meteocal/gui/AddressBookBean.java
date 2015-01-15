/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
        private static final String user_addressbook_page_url = "/user/addressbook?faces-redirect=true";

    
    @EJB
    UserArea ua;
    
    public List<Contact> getContacts(){
        return ua.getContact();
    }
    
    /**
     * Calls SearchArea.deleteContact(String contactEmail)
     * @return addressbook page
     */
    public String deleteContact(String contactEmail) {
        ua.deleteContact(contactEmail);
        return user_addressbook_page_url;
    }
    
}
