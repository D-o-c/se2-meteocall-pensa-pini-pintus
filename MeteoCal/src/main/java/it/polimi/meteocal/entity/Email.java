/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author doc
 */

@Entity (name="EMAIL")

@NamedQueries({
        @NamedQuery(name = Email.findAll, 
                query = "SELECT e FROM EMAIL e WHERE e.sent = FALSE")})
    
public class Email implements Serializable {
    
    public final static String findAll = "Email.findAll";
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long emailId;
    
    @Column(name = "RECIPIENT")
    private String recipient;
    
    @Column(name = "SUBJECT")
    private String subject;
    
    @Column(name = "BODY", length = 1000)
    private String body;
    
    @Column(name = "SENT")
    private boolean sent;

    public Email() {
    }

    public Email(String recipient, String subject, String body, boolean sent) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sent = sent;
    }
    
    

    public long getEmailId() {
        return emailId;
    }

    public void setEmailId(long emailId) {
        this.emailId = emailId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
    
    
    
    
    
}
