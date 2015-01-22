/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.*;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Before;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 */
@RunWith(Arquillian.class)
public class EventManagerIT {
    
    @Inject
    EventManager eventManager;
    
    @PersistenceContext
    EntityManager em;
    
    Event newEvent;
    User u1;
    User u2;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                            .addPackage(EventManager.class.getPackage())
                            .addPackage(User.class.getPackage())
                            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }
    
    @Before
    public void setUp() {
        
        newEvent = new Event();
        
        u1 = new User();
        u2 = new User();
        u1.setEmail("EmailUser1");
        u2.setEmail("EmailUser2");
        u1.setEvents(new ArrayList<Calendar>());
        u2.setEvents(new ArrayList<Calendar>());
        em.persist(u1);
        em.persist(u2);
        
        
        
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
    public void testCreateAndUpdateEventAndRemoveFromPartecipantsAndGetPartecipants() {
        System.out.println("First");
        assertNotNull(em.find(User.class, u1.getEmail()));
        assertNotNull(em.find(User.class, u2.getEmail()));
        
        
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
        
        //Remove from partecipants
        
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
        
        //get partecipants
        
        List<User> partecipants = eventManager.getPartecipants(newEvent);
        List<User> partecipantsToCompare = new ArrayList<>();
        partecipantsToCompare.add(u1);
        partecipantsToCompare.add(u2);
        assertThat(partecipants, is(partecipantsToCompare));
        
        
    }
    
    @Test
    @InSequence(3)
    public void testFind() {
        System.out.println("Second");
        assertThat(eventManager.find(newEvent.getEventId()).getEventId(), is(newEvent.getEventId()));
    }
    
    @Test
    @InSequence(4)
    public void deleteEvent(){
        eventManager.deleteEvent(newEvent);
        assertNull(em.find(Event.class, newEvent.getEventId()));
        
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
