package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.primarykeys.ContactPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 */
public class SearchingManager {
    
    @PersistenceContext
    EntityManager em;

    /**
     * Searches users and erase the user who searched from the result list
     * @param searchInput
     * @param whoSearched
     * @return 
     */
    public List<User> searchMatchingUser(String searchInput, User whoSearched) {
        
        List<User> resultList = em.createNamedQuery(User.findByEmailOrLikeNameSurname, User.class)
                                    .setParameter(1, searchInput+"%")
                                    .setParameter(2, searchInput)
                                    .getResultList();
        
        if(resultList.contains(whoSearched)) resultList.remove(whoSearched);
        
        return resultList;
    }

    /**
     * Adds a contact to a user
     * @param user
     * @param friend 
     */
    public void addContact(User user, User friend) {
        Contact contact = new Contact(friend.getEmail(), friend.getName(), friend.getSurname(), user);
        em.persist(contact);
        user.addContact(contact);
        em.merge(user);
    }

    /**
     * @param user
     * @param friend
     * @return user.getContacts().contains(friend)
     */
    public boolean contactExist(User user, User friend) {
        ContactPK pk = new ContactPK(friend.getEmail(), user.getEmail());
        return user.getContacts().contains(em.find(Contact.class, pk));
    }
    
    /**
     * Checks also if the events are public or not 
     * @param user
     * @return calendar of a user
     */
    public ScheduleModel getCalendar(User user, User loggedUser) {
        
        ScheduleModel calendar = new DefaultScheduleModel();
        
        try {
            
            List<Calendar> userEvents = user.getEvents();
            for(Calendar c : userEvents) {
                int inviteStatus = c.getInviteStatus();
                if(inviteStatus == 1) {
                    Event e = c.getEvent();
                    DefaultScheduleEvent dse = new DefaultScheduleEvent(e.getName(),
                                                                        e.getBeginTime(),
                                                                        e.getEndTime());
                    //DefaultScheduleEvent.setData() is used to set the ID
                    
                    if(e.isPublic() || partecipates(e,loggedUser)) {
                        dse.setData(e.getEventId());
                    }
                    else{
                        dse.setTitle("Private Event");
                    }
                    
                    calendar.addEvent(dse);
                    
                }//endif
                
            }//endfor
            
        } catch (Exception e) {}
        
        return calendar;
    }
    
    public boolean partecipates(Event e, User u){
        for (Calendar c : e.getInvited()){
            if (c.getInviteStatus() == 1 && c.getUser().equals(u)){
                return true;
            }
        }
        return false;
    }
    
}
