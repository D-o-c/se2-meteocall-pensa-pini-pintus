package it.polimi.meteocal.gui.search;

import it.polimi.meteocal.business.security.boundary.PublicArea;
import it.polimi.meteocal.business.security.entity.User;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author aldo
 */
@Named
@Stateless
public class SearchBean{

    @EJB
    PublicArea pa;
    
    private String searchInput;
    
    private List<User> users;
    
    private String name,surname,email;
    
    public SearchBean() {
    }

    public String getSearchInput() {
        return searchInput;
    }

    public void setSearchInput(String searchInput) {
        this.searchInput = searchInput;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
      
    public String findUser() {
        users = pa.findUser(searchInput);
        searchInput = "";
        return "search?faces-redirect=true";
    }
    
    public String findUser2() {
        users = pa.findUser(searchInput);
        searchInput = "";
        return "user/search?faces-redirect=true";
    }
    
    public String goToUserPage(String name,String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        return "/usercalendar?faces-redirect=true";
    }
    
    public String results() {
        if(users.isEmpty()) return "Results : Not Found";
        else return "Results :";
    }
      
}
