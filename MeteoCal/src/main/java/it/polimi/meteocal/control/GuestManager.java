package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Group;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
    public boolean register(User user){
        if (em.find(User.class, user.getEmail())==null){
            user.setGroupName(Group.USERS);
            user.setPublic(true);
            em.persist(user);
       emailSender.send(user.getEmail() ,
                        "MeteoCal Registration",
                        "Congratulations, you signed up on MeteoCal successfully");
            return true;
        }
        return false;
        
    }
    
    public void unregister(User loggedUser){
        
        //tuple della tabella Calendario dell'utente loggedUser
        List<Calendar> calendars = em.createNamedQuery(Calendar.findByUserEmail)
                .setParameter(1, loggedUser.getEmail())
                .getResultList();
        //rimozione dalle tuple dalla tabella Calendario
        for (Calendar calendar : calendars) {
            em.remove(calendar);
        }
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
    
}
