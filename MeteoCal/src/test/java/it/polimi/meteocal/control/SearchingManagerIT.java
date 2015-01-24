/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.primarykeys.ContactPK;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author user
 */
@RunWith(Arquillian.class)
public class SearchingManagerIT {
    
    @Inject
    SearchingManager searchingManager;
    
    @Inject
    EventManager eventManager;
    
    @Inject
    UserManager userManager;
    
    @PersistenceContext
    EntityManager em;
    
    @Resource
    UserTransaction utx;
    
    User user, userSecond;
    Event event_1,event_2,event_3,event_4;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                            .addPackage(SearchingManager.class.getPackage())
                            .addPackage(User.class.getPackage())
                            .addClass(ContactPK.class)
                            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }
    
    @Before
    public void setUp() throws Exception{
        user = new User();
        user.setEmail("email@email.com");
        user.setGroupName(Group.USERS);
        user.setName("name");
        user.setPassword("password!");
        user.setPublic(true);
        user.setSurname("surname");
        
        if(em.find(User.class, "email@email.com") == null){
            utx.begin();
                em.persist(user);
            utx.commit();
        }
        
        userSecond = new User();
        userSecond.setEmail("email@provider.com");
        userSecond.setGroupName(Group.USERS);
        userSecond.setName("name");
        userSecond.setPassword("password!");
        userSecond.setPublic(true);
        userSecond.setSurname("surname");
        
        if(em.find(User.class, "email@provider.com") == null){
            utx.begin();
                em.persist(userSecond);
            utx.commit();
        }
        
        Date today = new Date();
        long one_day = 86400000;
        
        event_1 = new Event();
        event_1.setEventId((long)1);
        event_1.setPublic(true);
        event_1.setBeginTime(new Date(today.getTime() + one_day));
        event_1.setEndTime(new Date(today.getTime() + 2*one_day));
        event_1.setName("Event 1");
        event_1.setDescription("Event Description");
        event_1.setLocation("a,b,c");
        event_1.setOutdoor(false);
        
        event_2 = new Event();
        event_2.setEventId((long)2);
        event_2.setPublic(true);
        event_2.setBeginTime(new Date(today.getTime() + 3*one_day));
        event_2.setEndTime(new Date(today.getTime() + 4*one_day));
        event_2.setName("Event 2");
        event_2.setDescription("Event Description");
        event_2.setLocation("a,b,c");
        event_2.setOutdoor(false);
        
        event_3 = new Event();
        event_3.setEventId((long)3);
        event_3.setPublic(false);
        event_3.setBeginTime(new Date(today.getTime() + 5*one_day));
        event_3.setEndTime(new Date(today.getTime() + 6*one_day));
        event_3.setName("Event 3");
        event_3.setDescription("Event Description");
        event_3.setLocation("a,b,c");
        event_3.setOutdoor(false);
        
        event_4 = new Event();
        event_4.setEventId((long)4);
        event_4.setPublic(false);
        event_4.setBeginTime(new Date(today.getTime() + 7*one_day));
        event_4.setEndTime(new Date(today.getTime() + 8*one_day));
        event_4.setName("Event 4");
        event_4.setDescription("Event Description");
        event_4.setLocation("a,b,c");
        event_4.setOutdoor(false);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    @InSequence(1)
    public void userManagerAndEntityManagerShouldBeInjected() {
        assertNotNull(searchingManager);
        assertNotNull(searchingManager.em);
        assertNotNull(eventManager);
        assertNotNull(eventManager.em);
        assertNotNull(userManager);
        assertNotNull(userManager.em);
        assertNotNull(em);
    }

    /**
     * Test of searchMatchingUser method, of class SearchingManager.
     */
    @Test
    @InSequence(2)
    public void testSearchMatchingUser() throws Exception {
        
        List<User> usersFound = searchingManager.searchMatchingUser("na", user);
        List<String> usersFoundEmails = new ArrayList<>();
        for(User u : usersFound) {
            String s = u.getEmail();
            usersFoundEmails.add(s);
        }
        assertTrue(usersFoundEmails.contains(userSecond.getEmail()));
        assertFalse(usersFoundEmails.contains(user.getEmail()));
        
        usersFound = searchingManager.searchMatchingUser("su", user);
        usersFoundEmails = new ArrayList<>();
        for(User u : usersFound) {
            String s = u.getEmail();
            usersFoundEmails.add(s);
        }
        assertTrue(usersFoundEmails.contains(userSecond.getEmail()));
        assertFalse(usersFoundEmails.contains(user.getEmail()));
        
        usersFound = searchingManager.searchMatchingUser("email@provider.com", user);
        usersFoundEmails = new ArrayList<>();
        for(User u : usersFound) {
            String s = u.getEmail();
            usersFoundEmails.add(s);
        }
        assertTrue(usersFoundEmails.contains(userSecond.getEmail()));
    }

    /**
     * Test of addContact and contactExist method, of class SearchingManager.
     */
    @Test
    @InSequence(3)
    public void testContact() throws Exception {
        ContactPK pk = new ContactPK(userSecond.getEmail(),user.getEmail());
        Contact contact = em.find(Contact.class, pk);
        //contact should not exist in DB
        assertNull(contact);
        assertFalse(contains(user.getContacts(),contact));
        assertFalse(searchingManager.contactExist(user, userSecond));
        
        utx.begin();
            searchingManager.addContact(user, userSecond);
        utx.commit();
        
        contact = em.find(Contact.class, pk);
        //contact should exist in DB
        assertNotNull(contact);
        assertTrue(contains(user.getContacts(),contact));
        assertTrue(searchingManager.contactExist(user, userSecond));
           
    }

    /**
     * Test of getCalendar method, of class SearchingManager.
     */
    @Test
    @InSequence(4)
    public void testGetCalendar() throws Exception{
        
        List<String> invitedUsers = new ArrayList<>();
        invitedUsers.add(user.getEmail());
        
        utx.begin();
            //event is public and logged user partecipates
            eventManager.createEvent(event_1, invitedUsers, userSecond);
            userManager.answerInvite(user, event_1, 1);
            //event is public and logged user do not partecipates
            eventManager.createEvent(event_2, new ArrayList<String>(), userSecond);
            userManager.answerInvite(user, event_2, -1);
            //event is private and logged user partecipates
            eventManager.createEvent(event_3, invitedUsers, userSecond);
            userManager.answerInvite(user, event_3, 1);
            //event is private and logged user do not partecipates
            eventManager.createEvent(event_4, new ArrayList<String>(), userSecond);
            userManager.answerInvite(user, event_4, -1);
        utx.commit();
        
        
        List<ScheduleEvent> eventsOfUserSecond =
                searchingManager.getCalendar(userSecond, user).getEvents();
        for(ScheduleEvent se : eventsOfUserSecond) {
            if(se.getTitle().equals(event_1.getName())){
                assertTrue(event_1.getEventId() == (long) se.getData());
            }
            else if(se.getTitle().equals(event_2.getName())){
                assertTrue(event_2.getEventId() == (long) se.getData());
            }
            else if(se.getTitle().equals(event_3.getName())){
                assertTrue(event_3.getEventId() == (long) se.getData());
            }
            else if(se.getTitle().equals(event_4.getName())){
                assertTrue(se.getTitle().equals("Private Event"));
            }
        }
        
    }

    /**
     * Test of partecipates method, of class SearchingManager.
     */
    @Test
    @InSequence(5)
    public void testPartecipates() {
        event_1 = em.find(Event.class,event_1.getEventId());
        assertTrue(searchingManager.partecipates(event_1,user));
        event_2 = em.find(Event.class,event_2.getEventId());
        assertFalse(searchingManager.partecipates(event_2,user));
        event_3 = em.find(Event.class,event_3.getEventId());
        assertTrue(searchingManager.partecipates(event_3,user));
        event_4 = em.find(Event.class,event_4.getEventId());
        assertFalse(searchingManager.partecipates(event_4,user));
    }
 
    private boolean contains(List<Contact> list, Contact contact) {
        for(Contact c : list) {
            if(c.equals(contact)) {
                return true;
            }
        }
        return false;
    }
    
}
