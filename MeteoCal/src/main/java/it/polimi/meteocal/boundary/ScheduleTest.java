/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.boundary;

import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.security.Principal;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author doc
 */
@Singleton
@Lock(LockType.READ) // allows timers to execute in parallel
@Stateless
public class ScheduleTest {
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    @Schedule(minute = "*", hour = "*")
    private void test(){
        
        em.find(Event.class, (long) 1).setPublic(false);
    }
    
    
}
