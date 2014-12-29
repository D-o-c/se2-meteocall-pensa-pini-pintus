/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.boundary;

import it.polimi.meteocal.business.security.entity.Calendar;
import it.polimi.meteocal.business.security.entity.Group;
import it.polimi.meteocal.business.security.entity.Event;
import it.polimi.meteocal.business.security.entity.User;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author doc
 */
@Stateless
public class EventArea {
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;

    public int save(Event event, List<String> invitedUsers) {
        int status =0;
        User creator = em.find(User.class, principal.getName()); //seleziona l'utente creatore
        event.setCreatorEmail(creator.getEmail()); //salva la sua mail nell'evento
        em.persist(event); //salva l'evento nel DB
        
        event.addInvited(creator, 1); //inserisce l'evento nel calendario dell'utente creatore 
        invitedUsers.remove(creator.getEmail()); //toglie l'utente creatore tra gli invitati (se presente)
        
        for (String invitedUser : invitedUsers) {
            User u=em.find(User.class, invitedUser);
            try{
                event.addInvited(u, 0);
            }
            catch (NullPointerException e){
                status= 1;
            }
            
        }
        return status;
    }
    
    public List<Event> findAll(){
        return em.createNamedQuery(Event.findAll, Event.class)
                                .getResultList();
    }
    
}
