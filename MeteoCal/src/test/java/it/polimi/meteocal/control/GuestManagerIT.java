/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.Token;
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
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(Arquillian.class)
public class GuestManagerIT {
    
    @Inject
    GuestManager guestManager;
    
    @Inject
    EventManager eventManager;
    
    @PersistenceContext
    EntityManager em;
    
    @Resource
    private UserTransaction utx;
    
    User newUser;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        System.out.println("Before deployment");
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                            .addPackage(GuestManager.class.getPackage())
                            .addPackage(User.class.getPackage())
                            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }
    
    @Before
    public void setUp() throws Exception{
        
        newUser = new User();
        newUser.setEmail("test@email.it");
        newUser.setName("test");
        newUser.setPassword("password");
        newUser.setSurname("test");
        
        
        
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void eventManagerAndEntityManagerShouldBeInjected() {
        assertNotNull(guestManager);
        assertNotNull(guestManager.em);
        assertNotNull(guestManager.emailSender);
        assertNotNull(guestManager.tokenManager);
        assertNotNull(em);
    }
    
    @Test
    @InSequence(1)
    public void testRegister() throws Exception{
        assertNull(em.find(User.class, "test@email.it"));
        
        utx.begin();
        
            assertTrue(guestManager.register(newUser));
        
        utx.commit();
        
        assertTrue(newUser.getGroupName().equals(Group.USERS));
        assertTrue(newUser.isPublic());
        assertTrue(newUser.getPassword().equals(PasswordEncrypter.encryptPassword("password")));
        assertNotNull(em.find(User.class, "test@email.it"));
        
        User newUserSecond = new User();
        newUserSecond.setEmail("test@email.it");
        newUserSecond.setName("test2");
        newUserSecond.setPassword("password2");
        newUserSecond.setSurname("test2");
        
        utx.begin();
        
            assertFalse(guestManager.register(newUserSecond));
        
        utx.commit();
        
        User tmp = em.find(User.class, "test@email.it");
        
        assertTrue(tmp.getEmail().equals(newUserSecond.getEmail()));
        assertFalse(tmp.getName().equals(newUserSecond.getName()));
           
    }
    
    @Test
    @InSequence(2)
    public void testToken() throws Exception{
        
        int temp = guestManager.sendToken("Wrong Email");
        assertTrue(temp == -1);
        utx.begin();
            temp = guestManager.sendToken(newUser.getEmail());
        utx.commit();
        assertTrue(temp == 0);
        
        String firstToken;
        List<Token> userTokenList = em.createNamedQuery(Token.findByUser,
                                    Token.class).setParameter(1,
                                        newUser.getEmail()).getResultList();
        
        assertTrue(userTokenList.size() == 1);
        //Save first token and verify that it is actived
        firstToken = userTokenList.get(0).getToken();
        
        assertTrue(em.find(Token.class, firstToken).isActive());
        
        utx.begin();
            guestManager.sendToken(newUser.getEmail());
        utx.commit();
        //save second token and verify that the first one is deactivated and the second is actived
        assertFalse(em.find(Token.class, firstToken).isActive());
        
        userTokenList = em.createNamedQuery(Token.findByUser,
                                    Token.class).setParameter(1,
                                        newUser.getEmail()).getResultList();
        
        assertTrue(userTokenList.size() == 2);
        
        String secondToken = null;
        
        for (Token t : userTokenList){
            if(t.getToken().equals(firstToken)){
                continue;
            }
            secondToken = t.getToken();
        }
        
        assertNotNull(secondToken);
        
        assertTrue(em.find(Token.class, secondToken).isActive());
        
        //Change password with token
        
        User wrongUser = new User();
        wrongUser.setEmail("wrong@email.it");
        wrongUser.setName("wrong");
        wrongUser.setPassword("wrong");
        wrongUser.setSurname("wrong");
        wrongUser.setGroupName(Group.USERS);
        wrongUser.setPublic(true);
        
        utx.begin();
        
        em.persist(wrongUser);
        utx.commit();
        
        
        utx.begin();
            assertTrue(guestManager.changeLostPassword(newUser.getEmail(), "Wrong Token", "newPassword") == -1);
            assertTrue(guestManager.changeLostPassword(newUser.getEmail(), firstToken, "newPassword") == -1);
            assertTrue(guestManager.changeLostPassword("Null Email", secondToken, "newPassword") == -2);
            assertTrue(guestManager.changeLostPassword(wrongUser.getEmail(), secondToken, "newPassword") == -2);
            assertTrue(guestManager.changeLostPassword(newUser.getEmail(), secondToken, "newPassword") == 0);
        utx.commit();
        
        assertTrue(newUser.getPassword().equals(PasswordEncrypter.encryptPassword("newPassword")));
        assertFalse(em.find(Token.class, secondToken).isActive());        
    }

    
    
    
    @Test
    @InSequence(3)
    public void unregister() throws Exception{
        
        Event tmpevnt = new Event();
        tmpevnt.setBeginTime(new Date());
        tmpevnt.setDescription("");
        tmpevnt.setEndTime(new Date());
        tmpevnt.setLocation("");
        tmpevnt.setPub(true);
        tmpevnt.setName("");
        
        utx.begin();
            eventManager.createEvent(tmpevnt, new ArrayList<String>(), newUser);
        utx.commit();
        
        utx.begin();
            
            guestManager.unregister(newUser);
            
        utx.commit();
        
        assertTrue(tmpevnt.getCreator().getEmail().equals("undefined@email.com"));
        
        assertNull(em.find(User.class, newUser.getEmail()));
    }
    
}
