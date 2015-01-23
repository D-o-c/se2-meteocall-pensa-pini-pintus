package it.polimi.meteocal.entity;

import it.polimi.meteocal.control.PasswordEncrypter;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 */
@Entity(name = "USERS")
@NamedQueries({
        @NamedQuery(name = User.findByEmailOrLikeNameSurname, 
                query = "SELECT u FROM USERS u WHERE ((u.name LIKE ?1) OR "
                                                    + "(u.surname LIKE ?1) OR "
                                                    + "(u.email = ?2)) AND"
                                                    + "(u.public_ = true)")
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String findByEmailOrLikeNameSurname = "User.findByEmailOrLikeNameSurname";
    

    @Id
    @Pattern(regexp = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?",
            message = "invalid email")
    @NotNull(message = "Email may not be empty")
    @Column(name = "EMAIL")
    private String email;
    
    @OneToMany(mappedBy="user")
    private List<Calendar> events;
    
    @OneToMany(mappedBy="user")
    private List<Update> notifies;
    
    
    @OneToMany(mappedBy="user", orphanRemoval=true)
    private List<Contact> contacts;
    
    @Size(min=4, message="Password length at least 4 characters")
    @NotNull(message = "Password may not be empty")
    @Column(name = "PASSWORD")
    private String password;
    
    @NotNull(message = "May not be empty")
    @Column(name = "GROUPNAME")
    private String groupName;
    
    @NotNull(message = "Name may not be empty")
    @Column(name = "NAME")
    private String name;
    
    @NotNull(message = "Surname may not be empty")
    @Column(name = "SURNAME")
    private String surname;
    
    @NotNull
    @Column(name = "PUBLIC_")
    private boolean public_;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
    
    public String getSurname(){
        return surname;
    }
    
    public void setSurname(String surname){
        this.surname=surname;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PasswordEncrypter.encryptPassword(password);
    }

    public boolean isPublic() {
        return public_;
    }

    public void setPublic(boolean b) {
        this.public_ = b;
    }
    
    public void addContact(Contact contact) {
        contacts.add(contact);
        if(contact.getUser() != this) {
            contact.setUser(this);
        }
    }

    public List<Calendar> getEvents() {
        return events;
    }

    public void setEvents(List<Calendar> events) {
        this.events = events;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public List<Update> getNotifies() {
        return notifies;
    }

    public void setNotifies(List<Update> notifies) {
        this.notifies = notifies;
    }
    
    public void addNotify(Update u){
        this.notifies.add(u);
    }
    
    public boolean equals(User user) {
        return this.email.equals(user.email);
    }
    
}
