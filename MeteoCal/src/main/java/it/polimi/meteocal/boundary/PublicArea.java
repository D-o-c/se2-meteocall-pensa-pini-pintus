package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.EmailSender;
import it.polimi.meteocal.control.PasswordEncrypter;
import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author aldo
 */
@Stateless
public class PublicArea {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;

    /**
     * Calls EntityManager.find(User.class, principal.getName())
     * @return the logger user
     */
    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    /**
     * Sets Group Name = Group.USERS
     * Sets the user to public
     * Calls EntityManager.persist(user)
     * @param user 
     * @return  
     */
    public boolean register(User user) {
        try {
            String s = em.find(User.class, user.getEmail()).getEmail();
            return false;
        } catch(NullPointerException e) {
            user.setGroupName(Group.USERS);
            user.setPublic(true);
            em.persist(user);
            EmailSender.send(user.getEmail() ,
                        "MeteoCal Registration",
                        "Congratulations, you signed up on MeteoCal successfully");
            return true;
        }       
    }

    /**
     * Calls loggedUser = this.getLoggedUser()
     * Calls EntityManager.remove(loggedUser)
     */
    public void unregister() {
        User loggedUser = getLoggedUser();
        
        //tuple della tabella Calendario dell'utente loggedUser
        List<Calendar> calendars = em.createNamedQuery(Calendar.findByUserEmail)
                .setParameter(1, loggedUser.getEmail())
                .getResultList();
        //rimozione dalle tuple dalla tabella Calendario
        for(int i = 0; i < calendars.size(); i++) {
            em.remove(calendars.get(i));
        }
        //tuple della tabella Evento con creatore il loggedUser
        List<Event> events = em.createNamedQuery(Event.findByCreator)
                .setParameter(1, loggedUser.getEmail())
                .getResultList();
        //creazione di uno user "undefined" ed eventuale persist
        User u = new User();
        u.setEmail("undefined@email.com");
        u.setName("name");
        u.setSurname("surname");
        u.setPublic(false);
        u.setGroupName(Group.USERS);
        u.setPassword(PasswordEncrypter.encryptPassword("undefined"));
        User test = em.find(User.class, "undefined@email.com");
        if(test == null) {
            em.persist(u);
        }
        else {
            u = test;
        }
        //update del creator degli eventi del loggedUser
        //il creator diventa "undefined" e l'evento viene salvato
        for(int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
                       
            e.setCreator(u);
            
            em.persist(e);
        }
        //tutte le tuple della tabella Evento
        List<Event> allEvents = em.createNamedQuery(Event.findAll).getResultList();
        for(int i = 0; i < allEvents.size(); i++) {
            
            List<Calendar> invited = allEvents.get(i).getInvited();
            
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
    
}
