/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Token;
import it.polimi.meteocal.entity.User;
import java.util.Date;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

/**
 *
 * 
 */
@Singleton
@Lock(LockType.WRITE) // not allows timers to execute in parallel
@Stateless
public class TokenManager {
    
    private static final long one_day = 86400000;
    
    @PersistenceContext
    EntityManager em;
    
    List<Token> tokenList;
    
    
    @Schedule(minute="*", hour="*")
    public void deleteTokenAfterOneDay(){
        long today = (new Date()).getTime();
        tokenList = em.createNamedQuery(Token.findAll, Token.class).getResultList();
        
        for (Token t : tokenList){
            em.lock(t, LockModeType.PESSIMISTIC_WRITE);
            if (today-t.getTime().getTime() >= one_day){
                t.disable();
                em.merge(t);
            }
            em.lock(t, LockModeType.NONE);
        }
        
    }

   public void deleteAllToken(User u) {
        List<Token> userTokenList = em.createNamedQuery(Token.findByUser, Token.class).setParameter(1, u.getEmail()).getResultList();
        for (Token t : userTokenList){
            em.lock(t, LockModeType.PESSIMISTIC_WRITE);
            
            t.disable();
            em.merge(t);
            
            em.lock(t, LockModeType.NONE);
        }
    }
    
}
