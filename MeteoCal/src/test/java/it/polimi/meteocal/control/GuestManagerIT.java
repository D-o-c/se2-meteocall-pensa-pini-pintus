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
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 */
@RunWith(Arquillian.class)
public class GuestManagerIT {
    
    @Inject
    GuestManager guestManager;
    
    @PersistenceContext
    EntityManager em;
    
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
    public void setUp() {
        
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
    public void testRegister(){
        guestManager.register(newUser);
        assertThat(newUser.getGroupName(), is(Group.USERS));
        assertThat(newUser.isPublic(), is(true));
        verify(guestManager.em,times(1)).persist(newUser);
        
        User newUserSecond = new User();
        newUserSecond.setEmail("test@email.it");
        newUserSecond.setName("test");
        newUserSecond.setPassword("password");
        newUserSecond.setSurname("test");
        
        verify(guestManager.em, times(0)).persist(newUserSecond);
        
        assertTrue(newUser.isPublic());
        assertThat(newUser.getPassword(), is(PasswordEncrypter.encryptPassword("password")));
           
    }
    
    @Test
    @InSequence(2)
    public void testToken(){
        
        assertThat(guestManager.sendToken("Wrong Email"), is(-1));
        assertThat(guestManager.sendToken(newUser.getEmail()), is(0));
        
        String firstToken;
        List<Token> userTokenList = em.createNamedQuery(Token.findByUser,
                                    Token.class).setParameter(1,
                                        newUser.getEmail()).getResultList();
        
        assertThat(userTokenList.size(), is(1));
        //Save first token and verify that it is actived
        firstToken = userTokenList.get(0).getToken();
        
        assertTrue(em.find(Token.class, firstToken).isActive());
        
        guestManager.sendToken(newUser.getEmail());
        //save second token and verify that the first one is deactivated and the second is actived
        assertFalse(em.find(Token.class, firstToken).isActive());
        
        userTokenList = em.createNamedQuery(Token.findByUser,
                                    Token.class).setParameter(1,
                                        newUser.getEmail()).getResultList();
        
        assertThat(userTokenList.size(), is(2));
        
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
        wrongUser.setEmail("wrongUserEmail");
        em.persist(wrongUser);
        
        assertThat(guestManager.changeLostPassword(newUser.getEmail(), "Wrong Token", "newPassword"), is(-1));
        assertThat(guestManager.changeLostPassword(newUser.getEmail(), firstToken, "newPassword"), is(-1));
        assertThat(guestManager.changeLostPassword("Null Email", secondToken, "newPassword"), is(-2));
        assertThat(guestManager.changeLostPassword(wrongUser.getEmail(), secondToken, "newPassword"), is(-2));
        assertThat(guestManager.changeLostPassword(newUser.getEmail(), secondToken, "newPassword"), is(0));
        
        assertThat(newUser.getPassword(), is(PasswordEncrypter.encryptPassword("newPassword")));
        assertFalse(em.find(Token.class, secondToken).isActive());        
    }

    
    
    
    @Test
    @InSequence(3)
    public void unregister(){
        
        Event tmpevnt = new Event();
        tmpevnt.setCreator(newUser);
        
        guestManager.unregister(newUser);
        
        User substitute = em.find(User.class, "undefined@email.com");
        assertNotNull(substitute);
        
        assertThat(tmpevnt.getCreator(), is(substitute));
        
        assertNull(em.find(User.class, newUser.getEmail()));
    }
    
}
