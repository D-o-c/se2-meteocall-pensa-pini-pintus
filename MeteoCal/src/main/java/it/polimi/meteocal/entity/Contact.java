package it.polimi.meteocal.entity;

import it.polimi.meteocal.entity.primarykeys.ContactPK;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Entity(name = "CONTACT")/*
@NamedQueries({
    @NamedQuery(name = Contact.findByUserEmail, 
                    query = "SELECT c FROM CONTACT c WHERE c.user.email = ?1 ORDER BY c.surname")
})*/
@IdClass(ContactPK.class)
public class Contact implements Serializable {
    private static final long serialVersionUID = 1L;
   // public static final String findByUserEmail = "Contact.findByUserEmail";
    
    @Id
    @Column(name="CONTACT_EMAIL")
    private String email;
    
    @ManyToOne
    @Id
    @JoinColumn(name="OWNER_EMAIL", referencedColumnName="EMAIL")
    private User user;
    
    @NotNull(message = "May not be empty")
    @Column(name="CONTACT_NAME")
    private String name;
    
    @NotNull(message = "May not be empty")
    @Column(name="CONTACT_SURNAME")
    private String surname;
    
    public Contact() {
    }

    public Contact(String email, String name, String surname, User user) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.user = user;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
