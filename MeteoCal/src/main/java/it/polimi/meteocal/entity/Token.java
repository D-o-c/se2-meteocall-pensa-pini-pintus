/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 */
@Entity (name="TOKEN")
public class Token implements Serializable {
    
    @Id
    @Column(name = "TOKEN")
    private final UUID token;
    
    @ManyToOne
    @JoinColumn(name="USER", referencedColumnName="EMAIL")
    private final User user;
    
    @Column(name = "STATUS")
    private int status;
    
    @Column(name = "TIME_")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private final Date time;

    public Token() {
        token = null;
        user = null;
        time = null;
    }

    public Token(UUID token, User user, int status) {
        this.token = token;
        this.user = user;
        this.status = status;
        this.time = new Date();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UUID getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }
    
    
    
    
}
