/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.primarykeys.CalendarPK;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.Before;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 */
//@RunWith(Arquillian.class)
public class EventManagerTest{
    
    @Inject
    private EventManager eventManager;
    
    @PersistenceContext
    EntityManager em;
    
    Event newEvent;
    User u1;
    User u2;

    public EventManagerTest() {
    }
    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(EventManager.class)
                .addPackage(Event.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Before
    public void setUp() {/*
        eventManager = new EventManager();
        eventManager.em = mock(EntityManager.class);
        eventManager.emailSender = mock(EmailSender.class);
        eventManager.updateManager = mock(UpdateManager.class);*/
        
        newEvent = new Event();
        /*
        u1 = new User();
        u2 = new User();
        u1.setEmail("EmailUser1");
        u2.setEmail("EmailUser2");
        u1.setEvents(new ArrayList<Calendar>());
        u2.setEvents(new ArrayList<Calendar>());
        em.persist(u1);
        em.persist(u2);*/
        
        
        
    }
    
    @After
    public void tearDown() {
    }
    /*
    @Test
    public void UserManagerShouldBeInjected() {
        assertNotNull(eventManager);
    }
    
    @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }
    */
    

    /**
     * Test of createEvent method, of class EventManager.
     *//*
    @Test
    public void testCreateAndUpdateEventAndRemoveFromPartecipants() {
        eventManager.em.persist(u1);
        eventManager.em.persist(u2);
        assertNotNull(eventManager.em.find(User.class, u1.getEmail()));
        assertNotNull(eventManager.em.find(User.class, u2.getEmail()));
        
        
        List<String> invitedUser = new ArrayList<>();
        invitedUser.add("EmailUser1");
        invitedUser.add("EmailUser2");
        invitedUser.add("EmailUser3");
        
        eventManager.createEvent(newEvent, invitedUser, u2);
        verify(eventManager.em,times(1)).persist(newEvent);
        assertThat(newEvent.isBwodb(), is(false));
        assertThat(newEvent.isBwtdb(), is(false));
        
        int i=0;
        for(Calendar c : newEvent.getInvited()){
            switch(c.getUserEmail()){
                case "EmailUser1":
                    i++;
                    if (c.getInviteStatus() != 0)
                        fail("Invited status must be equals to 0");
                    break;
                case "EmailUser2":
                    if (c.getInviteStatus() != 1)
                        fail("Invited status must be equals to 1 (creator)");
                    i++;
                    break;
                default:
                    i++;
                    break;          
            }
        }
        
        if (i != 2)
            fail("Two and only two person was invited");
        
        //Update Event
        User u4 = new User();
        u4.setEmail("EmailUser4");
        eventManager.em.persist(u4);
        
        invitedUser.add("EmailUser4");
        
        eventManager.updateEvent(newEvent, invitedUser);
        
        i=0;
        for(Calendar c : newEvent.getInvited()){
            switch(c.getUserEmail()){
                case "EmailUser1":
                    i++;
                    if (c.getInviteStatus() != 0)
                        fail("Invited status must be equals to 0");
                    break;
                case "EmailUser2":
                    if (c.getInviteStatus() != 1)
                        fail("Invited status must be equals to 1 (creator)");
                    i++;
                    break;
                case "EmailUser4":
                    if(c.getInviteStatus() != 0)
                        fail("Invited status must be equals to 0");
                    i++;
                    break;
                default:
                    i++;
                    break;          
            }
        }
        
        if (i != 3)
            fail("Three and only Three person was invited");
        
        assertThat(newEvent.getWeatherConditions().size(), is(0));
        
        eventManager.removeFromPartecipants(newEvent, u4);
        
        i=0;
        for(Calendar c : newEvent.getInvited()){
            switch(c.getUserEmail()){
                case "EmailUser1":
                    i++;
                    if (c.getInviteStatus() != 0)
                        fail("Invited status must be equals to 0");
                    break;
                case "EmailUser2":
                    if (c.getInviteStatus() != 1)
                        fail("Invited status must be equals to 1 (creator)");
                    i++;
                    break;
                case "EmailUser4":
                    if(c.getInviteStatus() != -1)
                        fail("Invited status must be equals to -1");
                    i++;
                    break;
                default:
                    i++;
                    break;          
            }
        }
        
        if (i != 2)
            fail("Two and only two person was invited");
        
        
        
        
    }

    /**
     * Test of find method, of class EventManager.
     *//*
    @Test
    public void testFind() {
        eventManager.em.persist(u1);
        eventManager.createEvent(newEvent, new ArrayList<String>(), u1);
        assertThat(eventManager.find(newEvent.getEventId()).getEventId(), is(newEvent.getEventId()));
    }

    /**
     * Test of getPartecipants method, of class EventManager.
     *//*
    @Test
    public void testGetPartecipants() {
        System.out.println("getPartecipants");
        Event event = null;
        EventManager instance = new EventManager();
        List<User> expResult = null;
        List<User> result = instance.getPartecipants(event);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteEvent method, of class EventManager.
     *//*
    @Test
    public void testDeleteEvent() {
        System.out.println("deleteEvent");
        Event event = null;
        EventManager instance = new EventManager();
        instance.deleteEvent(event);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
    
}
