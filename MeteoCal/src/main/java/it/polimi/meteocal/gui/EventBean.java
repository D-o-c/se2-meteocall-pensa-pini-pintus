package it.polimi.meteocal.gui;

import it.polimi.meteocal.boundary.EventArea;
import it.polimi.meteocal.boundary.UserArea;
import it.polimi.meteocal.entity.User;
import it.polimi.meteocal.entity.WeatherCondition;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;


/**
 *
 */
@Named
@RequestScoped
public class EventBean{
    
    //Strings
    private static final String info = "Info";
    private static final String remove_successfull = "Successfully removed from partecipants";
    private static final String gmap_url = "https://www.google.com/maps/embed/v1/place?key=AIzaSyDDm0i7Jy_achXhFjVg8LcT1kbmi8wmdV4&q=";
    private final static String user_home_page_url = "/user/home?faces-redirect=true";
    private final static String user_changeeventinfo_page_url = "/user/changeeventinfo?faces-redirect=true";
    
    //Boundaries
    @EJB
    EventArea eventArea;
    @EJB
    UserArea userArea;
    
    /**
     * Empty Constructor
     */
    public EventBean() {}
    
    /**
     * @return eventArea.getCurrentEvent().getName()
     */
    public String getEventName(){
        return eventArea.getCurrentEvent().getName();
    }
    
    /**
     * @return eventArea.getCurrentEvent().getDescription()
     */
    public String getEventDescription(){
        return eventArea.getCurrentEvent().getDescription();
    }
    
    /**
     * yes or no
     * @return eventArea.getCurrentEvent().isOutdoor()
     */
    public String getEventOutdoorStatus(){
        if (eventArea.getCurrentEvent().isOutdoor()) return "yes";
        return "no";
    }
    
    /**
     * block or none
     * @return eventArea.getCurrentEvent().isOutdoor()
     */
    public String isWeatherVisible(){
        if (eventArea.getCurrentEvent().isOutdoor()) return "block";
        return "none";
    }
    
    /**
     * @return eventArea.getCurrentEvent().getBeginTime().toString()
     */
    public String getEventBeginTime(){
        return eventArea.getCurrentEvent().getBeginTime().toString();
    }
    
    /**
     * @return eventArea.getCurrentEvent().getEndTime().toString()
     */
    public String getEventEndTime(){
        return eventArea.getCurrentEvent().getEndTime().toString();
    }
    
    /**
     * @return eventArea.getCurrentEvent().getCreator().getName() + getSurname()
     */
    public String getEventCreator(){
        return  eventArea.getCurrentEvent().getCreator().getName()+" "+
                eventArea.getCurrentEvent().getCreator().getSurname();
    }
    
    /**
     * @return eventArea.getCurrentEvent().getLocation()
     */
    public String getEventLocation(){
        return eventArea.getCurrentEvent().getLocation();
    }
    
    /**
     * @return eventArea.getCurrentEvent().getWeatherConditions()
     */
    public List<WeatherCondition> getWeather(){
        return eventArea.getCurrentEvent().getWeatherConditions();
    }
    
    /**
     * @return eventArea.isCreator()
     */
    public boolean cannotChangeInfo() {
        return !eventArea.isCreator();
    }
    
    /**
     * @return eventArea.isCreator() || !eventArea.isPartecipants()
     */
    public boolean cannotDeletePartecipation(){
        return eventArea.isCreator() || !eventArea.isPartecipants();
    }
    
    /**
     * @return eventArea.getPartecipants()
     */
    public List<User> getPartecipants(){
        return eventArea.getPartecipants();
    }
    
       
    /**
     * @return /user/changeeventinfo?faces-redirect=true
     */
    public String changeEventInfo() {
        return user_changeeventinfo_page_url;
    }
        
    /**
     * Calls eventArea.removeFromPartecipants()
     * @return 
     */
    public String removePartecipation() {
        eventArea.removeFromPartecipants();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, info, remove_successfull));
        context.getExternalContext().getFlash().setKeepMessages(true);                
        return user_home_page_url;
    }
    
}
