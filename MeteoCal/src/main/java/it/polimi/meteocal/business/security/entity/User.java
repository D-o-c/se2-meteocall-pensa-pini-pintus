package it.polimi.meteocal.business.security.entity;

import it.polimi.meteocal.business.security.control.PasswordEncrypter;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
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
 * @author aldo
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
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    @NotNull(message = "May not be empty")
    private String email;
    
    @OneToMany(mappedBy="user")
    private List<Calendar> events;
    
    
    @OneToMany(mappedBy="user", orphanRemoval=true)
    private List<Contact> contacts;
    
    @Size(min=4, message="At least 4 characters")
    @NotNull(message = "May not be empty")
    private String password;
    
    @NotNull(message = "May not be empty")
    private String groupName;
    
    @NotNull(message = "May not be empty")
    private String name;
    
    @NotNull(message = "May not be empty")
    private String surname;
    
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
    
    
}
