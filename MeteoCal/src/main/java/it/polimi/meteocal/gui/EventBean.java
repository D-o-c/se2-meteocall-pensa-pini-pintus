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
 * @author doc
 */
@Named
@RequestScoped
public class EventBean{
    
    private final static String user_home = "/user/home?faces-redirect=true";
    
    @EJB
    EventArea ea;
    @EJB
    UserArea ua;
    
 
    
    
    
    /**
     * Empty Constructor
     */
    public EventBean() {}
    /*
    public Event getCurrentEvent(){
        return ea.getCurrentEvent();
    }*/
    
    public String getEventName(){
        return ea.getCurrentEvent().getName();
    }
    
    
    public String getEventDescription(){
        return ea.getCurrentEvent().getDescription();
    }
    
    
    public String getEventOutdoorStatus(){
        if (ea.getCurrentEvent().isOutdoor()){
            return "yes";
        }
        return "no";
    }
    
    public String isWeatherVisible(){
        if (ea.getCurrentEvent().isOutdoor()){
            return "block";
        }
        return "none";
    }
    
    
    public String getEventBeginTime(){
        return ea.getCurrentEvent().getBeginTime().toString();
    }
    
    
    public String getEventEndTime(){
        return ea.getCurrentEvent().getEndTime().toString();
    }
    
    public String getEventLocation(){
        return ea.getCurrentEvent().getLocation();
    }
    
    public String getEventCreator(){
        return ea.getCurrentEvent().getCreator().getName() + ea.getCurrentEvent().getCreator().getSurname();
    }
    
    
    
    public List<WeatherCondition> getWeather(){
        return ea.getCurrentEvent().getWeatherConditions();
    }
    
    public boolean isCreator(){
        return ea.isCreator();
    }
    
    public boolean isPartecipants(){
        return ea.isCreator() || !ea.isPartecipants();
    }
    
    public String removePartecipation(){
        ea.removeFromPartecipants();
        FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Info", "Succesfully removed from partecipants"));
        context.getExternalContext().getFlash().setKeepMessages(true);
                        
        return "/user/home";
    }
    
    public List<User> getPartecipants(){
        return ea.getPartecipants();
    }
    
    /**
     * Called By event.xhtml
     * @return
     */
    public String getGoogleMap() {
       
        String str = ea.getCurrentEvent().getLocation();
        //creates an array of address-city-state
        String[] parts = str.split(",");
       
        String address = parts[0].replaceAll(" ","+");
        String city = parts[1].replaceAll(" ","");
        String state = parts[2].replaceAll(" ","");
       
        String  location = address+","+city+"+"+state;
       
        return "https://www.google.com/maps/embed/v1/place?key=AIzaSyDDm0i7Jy_achXhFjVg8LcT1kbmi8wmdV4&q="
                +location;
       
    }
    
}
