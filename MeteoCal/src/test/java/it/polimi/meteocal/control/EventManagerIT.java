/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.User;
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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Before;

/**
 *
 */
@RunWith(Arquillian.class)
public class EventManagerIT {
    
    @Inject
    EventManager eventManager;
    
    @PersistenceContext
    EntityManager em;
    
    @Resource
    private UserTransaction utx;
    
    Event newEvent;
    User u1;
    User u2;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                            .addPackage(EventManager.class.getPackage())
                            .addPackage(Event.class.getPackage())
                            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }
    
    @Before
    public void setUp() throws Exception{
        
        newEvent = new Event();
        newEvent.setBeginTime(new Date(new Date().getTime() + 86400000));    //tomorrow
        newEvent.setDescription("Event Description");
        newEvent.setEndTime(new Date(new Date().getTime() + 172800000));     //the day after tomorrow
        newEvent.setLocation("a,b,c");
        newEvent.setPublic(true);
        newEvent.setName("Event Name");
    //    newEvent.setEventId(1);
        
        u1 = new User();
        u2 = new User();
        u1.setEmail("Email@User.n1");
        u1.setGroupName(Group.USERS);
        u1.setName("NameUser1");
        u1.setPassword("PasswordUser1");
        u1.setPublic(true);
        u1.setSurname("SurnameUser1");
        u2.setEmail("Email@User.n2");
        u2.setGroupName(Group.USERS);
        u2.setName("NameUser2");
        u2.setPassword("PasswordUser2");
        u2.setPublic(true);
        u2.setSurname("SurnameUser2");
        
        
        
        
        
    }
    
    @Test
    @InSequence(1)
    public void eventManagerAndEntityManagerShouldBeInjected() {
        assertNotNull(eventManager);
        assertNotNull(eventManager.em);
        assertNotNull(eventManager.emailSender);
        assertNotNull(eventManager.updateManager);
        assertNotNull(eventManager.weatherManager);
        assertNotNull(em);
    }
    
    @Test
    @InSequence(2)
    public void testCreateAndUpdateEventAndRemoveFromPartecipantsAndGetPartecipants() throws Exception{
        
        utx.begin();
            em.persist(u1);
            em.persist(u2);
        utx.commit();
        
        assertNotNull(em.find(User.class, u1.getEmail()));
        assertNotNull(em.find(User.class, u2.getEmail()));
        
        
        List<String> invitedUser = new ArrayList<>();
        invitedUser.add("Email@User.n1");
        invitedUser.add("Email@User.n2");
        invitedUser.add("Email@User.n3");
        
        utx.begin();
            eventManager.createEvent(newEvent, invitedUser, u2);
        utx.commit();
        
     /*   newEvent = em.find(Event.class, newEvent.getEventId());
        u1 = em.find(User.class, u1.getEmail());
        u2 = em.find(User.class, u2.getEmail());*/
        
        assertFalse(newEvent.isBwodb());
        assertFalse(newEvent.isBwtdb());
        
        int i=0;
        for(Calendar c : newEvent.getInvited()){
            switch(c.getUserEmail()){
                case "Email@User.n1":
                    i++;
                    if (c.getInviteStatus() != 0)
                        fail("Invited status must be equals to 0");
                    break;
                case "Email@User.n2":
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
        u4.setEmail("Email@User.n4");
        u4.setGroupName(Group.USERS);
        u4.setName("NameUser4");
        u4.setPassword("PasswordUser4");
        u4.setPublic(true);
        u4.setSurname("SurnameUser4");
        
        utx.begin();
            eventManager.em.persist(u4);
        utx.commit();
        
        invitedUser.add("Email@User.n4");
        
        utx.begin();
            eventManager.updateEvent(newEvent, newEvent, invitedUser);
        utx.commit();
        
    /*    newEvent = em.find(Event.class, newEvent.getEventId());
        u1 = em.find(User.class, u1.getEmail());
        u2 = em.find(User.class, u2.getEmail());
        u4 = em.find(User.class, u4.getEmail());
        */
        i=0;
        for(Calendar c : newEvent.getInvited()){
            switch(c.getUserEmail()){
                case "Email@User.n1":
                    i++;
                    if (c.getInviteStatus() != 0)
                        fail("Invited status must be equals to 0");
                    break;
                case "Email@User.n2":
                    if (c.getInviteStatus() != 1)
                        fail("Invited status must be equals to 1 (creator)");
                    i++;
                    break;
                case "Email@User.n4":
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
        
        assertTrue(newEvent.getWeatherConditions().isEmpty());
        
        //Remove from partecipants
        utx.begin();
            eventManager.removeFromPartecipants(newEvent, u4);
        utx.commit();
        
   /*     newEvent = em.find(Event.class, newEvent.getEventId());
        u1 = em.find(User.class, u1.getEmail());
        u2 = em.find(User.class, u2.getEmail());
        u4 = em.find(User.class, u4.getEmail());*/
        
        i=0;
        for(Calendar c : newEvent.getInvited()){
            switch(c.getUserEmail()){
                case "Email@User.n1":
                    i++;
                    if (c.getInviteStatus() != 0)
                        fail("Invited status must be equals to 0");
                    break;
                case "Email@User.n2":
                    if (c.getInviteStatus() != 1)
                        fail("Invited status must be equals to 1 (creator)");
                    i++;
                    break;
                case "Email@User.n4":
                    if(c.getInviteStatus() != -1)
                        fail("Invited status must be equals to -1");
                    break;
                default:
                    i++;
                    break;          
            }
        }
        
        if (i != 2)
            fail("Two and only two person was invited");
        
        //get partecipants
        
        List<User> partecipants = eventManager.getPartecipants(newEvent);
        List<String> emailOfPartecipants = new ArrayList<>();
        List<String> partecipantsToCompare = new ArrayList<>();
        partecipantsToCompare.add(u2.getEmail());
        
        for (User u : partecipants)
            emailOfPartecipants.add(u.getEmail());
        
        assertTrue(emailOfPartecipants.equals(partecipantsToCompare));
        
        
    }
    
    @Test
    @InSequence(3)
    public void testFind() {
        assertTrue(eventManager.find(newEvent.getEventId()).getEventId() == newEvent.getEventId());
    }
    
    @Test
    @InSequence(4)
    public void deleteEvent() throws Exception{
        utx.begin();
            eventManager.deleteEvent(newEvent);
        utx.commit();
        assertNull(em.find(Event.class, newEvent.getEventId()));
        
        u1 = em.find(User.class, u1.getEmail());
        u2 = em.find(User.class, u2.getEmail());
        
        for (Calendar c : u1.getEvents()){
            if (c.getEvent().equals(newEvent))
                fail("Event needs to be removed from calendar");
        }
        for (Calendar c : u2.getEvents()){
            if (c.getEvent().equals(newEvent))
                fail("Event needs to be removed from calendar");
        }
    }

    
    
}
