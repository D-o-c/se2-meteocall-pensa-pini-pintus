package it.polimi.meteocal.business.security.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

/**
 *
 * @author aldo
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
    private String email;
    
    @ManyToOne
    @Id
    @JoinColumn(name="USER_EMAIL")
    private User user;
    
    @NotNull(message = "May not be empty")
    private String name;
    @NotNull(message = "May not be empty")
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
