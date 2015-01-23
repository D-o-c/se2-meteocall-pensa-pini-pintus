/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;

/**
 *
 */
@Entity (name="TOKEN")
@NamedQueries({
        @NamedQuery(name = Token.findAll, 
                query = "SELECT t FROM TOKEN t"),
        @NamedQuery(name = Token.findByUser,
                query = "SELECT t FROM TOKEN t WHERE t.user.email = ?1")
})
public class Token implements Serializable {
    
    public final static String findAll = "Token.findAll";
    public final static String findByUser = "Token.findByUser";
    
    @Id
    @Column(name = "TOKEN")
    private final String token;
    
    @ManyToOne
    @JoinColumn(name="USER_", referencedColumnName="EMAIL")
    private final User user;
    
    @Column(name = "STATUS")
    private boolean active;
    
    @Column(name = "TIME_")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private final Date time;

    public Token() {
        token = null;
        user = null;
        time = null;
    }

    public Token(String token, User user) {
        this.token = token;
        this.user = user;
        this.active = true;
        this.time = new Date();
    }

    public boolean isActive() {
        return active;
    }

    public void disable() {
        this.active = false;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }
    
    
    
    
}
