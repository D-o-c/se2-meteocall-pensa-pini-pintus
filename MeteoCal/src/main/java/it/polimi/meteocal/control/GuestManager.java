package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.Token;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;
import javax.persistence.LockModeType;

/**
 *
 */
public class GuestManager {
    
    @PersistenceContext
    EntityManager em;
    
    //Controls
    @Inject
    Principal principal;
    @Inject
    EmailSender emailSender;
    @Inject
    TokenManager tokenManager;
    
    public boolean register(User user){
        if (em.find(User.class, user.getEmail())==null){
            user.setGroupName(Group.USERS);
            user.setPublic(true);
            em.persist(user);
        emailSender.send(user.getEmail() ,
                        "Registration",
                        "Congratulations, you signed up on MeteoCal successfully");
            return true;
        }
        return false;
        
    }
    
    public void unregister(User loggedUser){
        
        //tuple della tabella Evento con creatore il loggedUser
        List<Event> events = em.createNamedQuery(Event.findByCreator)
                .setParameter(1, loggedUser.getEmail())
                .getResultList();
        //creazione di uno user "undefined" ed eventuale persist
        User u = em.find(User.class, "undefined@email.com");
        if(u == null){
        
            u = new User();
            u.setEmail("undefined@email.com");
            u.setName("name");
            u.setSurname("surname");
            u.setPublic(false);
            u.setGroupName(Group.USERS);
            u.setPassword(PasswordEncrypter.encryptPassword("undefined"));
            em.persist(u);
        }
        
        //update del creator degli eventi del loggedUser
        //il creator diventa "undefined" e l'evento viene salvato
        for (Event e : events) {
            e.setCreator(u);
            
            em.merge(e);
        }
        //tutte le tuple della tabella Evento
        List<Event> allEvents = em.createNamedQuery(Event.findAll).getResultList();
        for (Event allEvent : allEvents) {
            List<Calendar> invited = allEvent.getInvited();
            for(int j = 0; j < invited.size(); j++) {
                //singola tupla di calendar
                Calendar c = invited.get(j);
                if(c.getUser().equals(loggedUser)) {
                    invited.remove(c);
                }
                
            }
        }
        
        //rimozione dal database di loggedUser
        em.remove(loggedUser);
    }
    
    public User getLoggedUser(){
        return em.find(User.class, principal.getName());
    }
    
    /**
     * 
     * @param email
     * @return -1: User doesn't exist <br/>
     *          0: Token sent
     */
    public int sendToken(String email) {
        User u = em.find(User.class, email);
        if (u == null){
            return -1;
        }
        
        tokenManager.deleteAllToken(u);
        
        String temp = UUID.randomUUID().toString();
        while (em.find(Token.class, temp) != null){
            temp = UUID.randomUUID().toString();
        }
        
        Token t = new Token(temp, u);
        em.persist(t);
        
        emailSender.send(email ,
                        "Recovery password",
                        "To recover your MeteoCal password use this token:\n" + t.getToken());
        
        return 0;
    }
    
    /**
     * 
     * @param email
     * @param tokenString
     * @param password
     * @return -1: if token doesn't exist <br/>
     *         -2: if user doesn't exist <br/>
     *          0: if password is changed successfully
     */
    public int changeLostPassword(String email, String tokenString, String password){
        Token t = em.find(Token.class, tokenString);
        
        em.lock(t, LockModeType.PESSIMISTIC_WRITE);
        
        User u = em.find(User.class, email);
        if (t == null || !t.isActive()){
            return -1;
        }
        else if (!t.getUser().equals(u) || u == null){
            return -2;
        }

        
        u.setPassword(password);
        em.merge(u);
        
        t.disable();
        em.merge(t);
        
        em.lock(t, LockModeType.NONE);
        
        return 0;
    }
    
}
