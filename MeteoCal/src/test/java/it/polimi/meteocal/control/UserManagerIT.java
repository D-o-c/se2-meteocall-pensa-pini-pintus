/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.Update;
import it.polimi.meteocal.entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.primefaces.model.ScheduleEvent;

/**
 *
 */
@RunWith(Arquillian.class)
public class UserManagerIT {
    
    @Inject
    UserManager userManager;
    
    @Inject
    EventManager eventManager;
    
    @PersistenceContext
    EntityManager em;
    
    @Resource
    UserTransaction utx;
    
    User user;
    User invitedUser;
    Event e1;
    Event e2;
    Event import1;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                            .addPackage(UserManager.class.getPackage())
                            .addPackage(User.class.getPackage())
                            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }
    
    
    @Before
    public void setUp() throws Exception{
        user = new User();
        user.setEmail("Email@User.tst");
        user.setGroupName(Group.USERS);
        user.setName("NameUserTest");
        user.setPassword("PasswordUserTest");
        user.setPublic(true);
        user.setSurname("SurnameUserTest");
        
        if(em.find(User.class, "Email@User.tst") == null){
            utx.begin();
                em.persist(user);
            utx.commit();
        }
        
        
    }
    
    @Test
    @InSequence(1)
    public void userManagerAndEntityManagerShouldBeInjected() {
        assertNotNull(userManager);
        assertNotNull(userManager.em);
        assertNotNull(em);
    }

    
    @Test
    public void testChangeCalendarVisibility() throws Exception{
        assertTrue(user.isPublic());
        utx.begin();
            userManager.changeCalendarVisibility(user);
        utx.commit();
        
        assertFalse(user.isPublic());
        
        utx.begin();
            userManager.changeCalendarVisibility(user);
        utx.commit();
        
        assertTrue(user.isPublic());
    }

    @Test
    public void testChangePassword() throws Exception{
        assertTrue(user.getPassword().equals(PasswordEncrypter.encryptPassword("PasswordUserTest")));
        utx.begin();
        try{
            userManager.changePassword(user, "shrtpsw");    //shortPassword
            fail("Short password saved");
        }
        catch(AssertionError e){
            userManager.changePassword(user, "LoNgPaSsWoRd");
        }
        utx.commit();
        assertTrue(user.getPassword().equals(PasswordEncrypter.encryptPassword("LoNgPaSsWoRd")));
    }

    @Test
    @InSequence(2)
    public void testGetCalendarAndTimeConsistencyAndGetUserEventsAndGetInvites() throws Exception{
        e1 = new Event();
        e2 = new Event();
        e1.setBeginTime(new Date(new Date().getTime() + 86400000));    //tomorrow
        e1.setDescription("Event1");
        e1.setEndTime(new Date(new Date().getTime() + 172800000));     //the day after tomorrow
        e1.setLocation("a,b,c");
        e1.setPublic(false);
        e1.setName("Event1");
        e1.setEventId(1);
        
        e2.setBeginTime(new Date(new Date().getTime() + 259200000));
        e2.setDescription("Event2");
        e2.setEndTime(new Date(new Date().getTime() + 345600000));
        e2.setLocation("a,b,c");
        e2.setPublic(true);
        e2.setName("Event2");
        e2.setEventId(2);
        
        invitedUser = new User();
        invitedUser.setEmail("invited@user.it");
        invitedUser.setName("Invited");
        invitedUser.setPassword("InvitedUser");
        invitedUser.setSurname("User");
        invitedUser.setGroupName(Group.USERS);
        invitedUser.setPublic(true);
        
        utx.begin();
            em.persist(invitedUser);
        utx.commit();
        
        List<String> invite = new ArrayList<>();
        invite.add(invitedUser.getEmail());
        
        utx.begin();
            eventManager.createEvent(e1, invite, user);
            eventManager.createEvent(e2, invite, user);
        utx.commit();
        
        List<Long> calendar = new ArrayList<>();
        calendar.add(e1.getEventId());
        calendar.add(e2.getEventId());
        List<Long> eventToCompare = new ArrayList<>();
        for (ScheduleEvent dse : userManager.getCalendar(user).getEvents())
            eventToCompare.add((long) dse.getData());
            
        
        assertTrue(calendar.equals(eventToCompare));
        
        
        //getUserEvents
        List<Long> listOfUserEvents = new ArrayList<>();
        for (Event e : userManager.getUserEvent(user))
            listOfUserEvents.add(e.getEventId());
        
        assertTrue(calendar.equals(listOfUserEvents));

        
        
        //time consistency
        
        Event tempEvent1 = new Event();
        Event tempEvent2 = new Event();
        Event tempEvent3 = new Event();
        tempEvent1.setBeginTime(new Date(new Date().getTime() + 172800000));
        tempEvent1.setDescription("Event1");
        tempEvent1.setEndTime(new Date(new Date().getTime() + 86400000));
        tempEvent1.setLocation("a,b,c");
        tempEvent1.setPublic(false);
        tempEvent1.setName("Event1");
        tempEvent1.setEventId(3);
        
        tempEvent2.setBeginTime(new Date(new Date().getTime() + 86400000));
        tempEvent2.setDescription("Event2");
        tempEvent2.setEndTime(new Date(new Date().getTime() + 345600000));
        tempEvent2.setLocation("a,b,c");
        tempEvent2.setPublic(true);
        tempEvent2.setName("Event2");
        tempEvent2.setEventId(4);
        
        tempEvent3.setBeginTime(new Date(new Date().getTime() + 518400000));
        tempEvent3.setDescription("Event2");
        tempEvent3.setEndTime(new Date(new Date().getTime() + 604800000));
        tempEvent3.setLocation("a,b,c");
        tempEvent3.setPublic(true);
        tempEvent3.setName("Event2");
        tempEvent3.setEventId(5);
        
        assertTrue(userManager.timeConsistency(user, tempEvent1) == -1);
        assertTrue(userManager.timeConsistency(user, tempEvent2) == -2);
        assertTrue(userManager.timeConsistency(user, tempEvent3) == 0);
        
        
        //getInvites
        
        List<Long> listOfInvitedUserEvents = new ArrayList<>();
        for (Event e : userManager.getUserEvent(invitedUser))
            listOfUserEvents.add(e.getEventId());
        
        assertTrue(calendar.equals(listOfInvitedUserEvents));
        
        
        
        
    }

    
    @Test
    @InSequence(3)
    public void testAnswerInviteAndImportCalendar() throws Exception{
        import1 = new Event();
        import1.setBeginTime(new Date(new Date().getTime() + 172800000));
        import1.setDescription("Import1");
        import1.setEndTime(new Date(new Date().getTime() + 86400000));
        import1.setLocation("a,b,c");
        import1.setPublic(false);
        import1.setName("Import1");
        import1.setEventId(6);
        
        List<String> invite = new ArrayList<>();
        invite.add(user.getEmail());
        
        utx.begin();
            eventManager.createEvent(import1, invite, invitedUser);
        utx.commit();
        
        for (Calendar c : user.getEvents()){
            if (c.getEvent().equals(import1)){
                assertTrue(c.getInviteStatus() == 0);
            }
        }
        
        utx.begin();
            userManager.answerInvite(user, import1, 1);
        utx.commit();
        
        for (Calendar c : user.getEvents()){
            if (c.getEvent().equals(import1)){
                assertTrue(c.getInviteStatus() == 1);
            }
        }
        utx.begin();
            userManager.answerInvite(user, import1, -1);
        utx.commit();
        
        for (Calendar c : user.getEvents()){
            if (c.getEvent().equals(import1)){
                assertTrue(c.getInviteStatus() == -1);
            }
        }
        
        HashSet<Event> toImport = new HashSet<>();
        toImport.add(import1);
        
        utx.begin();
            userManager.importCalendar(user, toImport);
        utx.commit();
        
        for (Calendar c : user.getEvents()){
            if (c.getEvent().equals(import1)){
                assertTrue(c.getInviteStatus() == 1);
            }
            if (c.getEvent().equals(e1)){
                assertTrue(c.getInviteStatus() == 1);
            }
            if (c.getEvent().equals(e2)){
                assertTrue(c.getInviteStatus() == 1);
            }
        }
        
        
        
        
    }

    
    @Test
    @InSequence(4)
    public void testSetNotifyReadAndSetAllNotifiesReadAndAllReadAndGetNumberOfNotReadNotifies() throws Exception{
        assertTrue(user.getNotifies().isEmpty());
        utx.begin();
            eventManager.updateEvent(import1, import1, new ArrayList<String>());
            eventManager.updateEvent(import1, import1, new ArrayList<String>());
            eventManager.updateEvent(import1, import1, new ArrayList<String>());
        utx.commit();
        
        List<Update> userNotifies = user.getNotifies();
        Update temp = null;
        
        assertTrue(userNotifies.size() == 3);
        for (Update u : userNotifies){
            assertTrue(u.getEvent().equals(import1));
            assertTrue(u.getUser().equals(user));
            assertFalse(u.isRead());
            temp = u;
        }
        
        assertFalse(userManager.allRead(user));
        assertTrue(userManager.getNumberOfNotReadNotifies(user) == 3);
        assertNotNull(temp);
        //SET NOTIFIES READ
        utx.begin();
            userManager.setNotifyRead(temp);
        utx.commit();
        
        assertTrue(userManager.getNumberOfNotReadNotifies(user) == 2);
        assertTrue(temp.isRead());
        //SET ALL NOTIFIES READ
        utx.begin();
            userManager.setAllNotifyRead(user);
        utx.commit();
        
        userNotifies = user.getNotifies();
        
        assertTrue(userNotifies.size() == 3);
        for (Update u : userNotifies){
            assertTrue(u.getEvent().equals(import1));
            assertTrue(u.getUser().equals(user));
            assertTrue(u.isRead());
        }
        
        assertTrue(userManager.allRead(user));
        assertTrue(userManager.getNumberOfNotReadNotifies(user) == 0);
    }

    /**
     * Test of deleteContact method, of class UserManager.
     */
    @Test
    public void testDeleteContact() {
        System.out.println("deleteContact");
        User user = null;
        Contact contact = null;
        UserManager instance = new UserManager();
        instance.deleteContact(user, contact);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
