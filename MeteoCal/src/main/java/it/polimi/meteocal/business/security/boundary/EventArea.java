/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.boundary;

import it.polimi.meteocal.business.security.entity.Group;
import it.polimi.meteocal.business.security.entity.Event;
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

    public void save(Event event) {
        em.persist(event);
    }
    
    public List<Event> findAll(){
        return em.createNamedQuery(Event.findAll, Event.class)
                                .getResultList();
    }
    
}
