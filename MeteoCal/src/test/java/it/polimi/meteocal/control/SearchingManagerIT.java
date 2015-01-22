/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.util.List;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.primefaces.model.ScheduleModel;

/**
 *
 */
@RunWith(Arquillian.class)
public class SearchingManagerIT {
    
    public SearchingManagerIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of searchMatchingUser method, of class SearchingManager.
     */
    @Test
    public void testSearchMatchingUser() {
        System.out.println("searchMatchingUser");
        String searchInput = "";
        User whoSearched = null;
        SearchingManager instance = new SearchingManager();
        List<User> expResult = null;
        List<User> result = instance.searchMatchingUser(searchInput, whoSearched);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addContact method, of class SearchingManager.
     */
    @Test
    public void testAddContact() {
        System.out.println("addContact");
        User user = null;
        User friend = null;
        SearchingManager instance = new SearchingManager();
        instance.addContact(user, friend);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of contactExist method, of class SearchingManager.
     */
    @Test
    public void testContactExist() {
        System.out.println("contactExist");
        User user = null;
        User friend = null;
        SearchingManager instance = new SearchingManager();
        boolean expResult = false;
        boolean result = instance.contactExist(user, friend);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCalendar method, of class SearchingManager.
     */
    @Test
    public void testGetCalendar() {
        System.out.println("getCalendar");
        User user = null;
        User loggedUser = null;
        SearchingManager instance = new SearchingManager();
        ScheduleModel expResult = null;
        ScheduleModel result = instance.getCalendar(user, loggedUser);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of partecipates method, of class SearchingManager.
     */
    @Test
    public void testPartecipates() {
        System.out.println("partecipates");
        Event e = null;
        User u = null;
        SearchingManager instance = new SearchingManager();
        boolean expResult = false;
        boolean result = instance.partecipates(e, u);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
