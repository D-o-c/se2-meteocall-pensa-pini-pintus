package it.polimi.meteocal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 */


@Entity (name="EVENT")
@NamedQueries({
        @NamedQuery(name = Event.findAll, 
                query = "SELECT e FROM EVENT e"),
        @NamedQuery(name = Event.findByCreator, 
                    query = "SELECT e FROM EVENT e WHERE e.creator.email = ?1")
})
public class Event implements Serializable {
   
    public final static String findByCreator = "Event.findByCreator";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long eventId;
   
    public static final String findAll = "Event.findAll";
   
   
    @OneToMany(mappedBy="event", cascade = CascadeType.PERSIST, orphanRemoval=true)
    private List<Calendar> invited;
    
    @OneToMany(mappedBy="event", cascade = CascadeType.PERSIST, orphanRemoval=true)
    private List<Update> update;
   
   
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Begin Time may not be empty")
    @Column(name = "BEGIN_TIME")
    @Future
    private Date beginTime;
   
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "End Time may not be empty")
    @Column(name = "END_TIME")
    @Future
    private Date endTime;
   
    @NotNull(message = "Name may not be empty")
    @Column(name = "NAME")
    private String name;
   
    @NotNull(message = "Description may not be empty")
    @Column(name = "DESCRIPTION")
    private String description;
   
    @NotNull(message = "May not be empty")
    @Column(name = "PUBLIC_")
    private boolean pub = true;
   
    @NotNull(message = "May not be empty")
    @Column(name = "OUTDOOR")
    private boolean outdoor = false;
   
    @NotNull(message = "May not be empty")
    @ManyToOne
    @JoinColumn(name="CREATOR", referencedColumnName="EMAIL")
    private User creator;
   
    @Pattern(regexp="[\\w*[ ]*]*[,][\\w*[ ]*]*[,][\\w*[ ]*]*",
             message="write \"address, city, state\"")
    @NotNull(message = "Location may not be empty")
    @Column(name = "LOCATION")
    private String location;
    
    @Column(name="BWODB")
    @NotNull
    private boolean bwodb; //BadWeatherOneDayBefore
    
    @Column(name="BWTDB")
    @NotNull
    private boolean bwtdb; //BadWeatherThreeDayBefore
    
    @OneToMany(mappedBy="event", orphanRemoval=true)
    private List<WeatherCondition> weatherConditions;

    public Event(Event e) {
        this.name = e.name;
        this.description = e.description;
        this.beginTime = e.beginTime;
        this.bwodb = e.bwodb;
        this.bwtdb = e.bwtdb;
        this.creator = e.creator;
        this.endTime = e.endTime;
        this.eventId = e.eventId;
        this.invited = e.invited;
        this.location = e.location;
        this.outdoor = e.outdoor;
        this.pub = e.pub;
        this.update = e.update;
        this.weatherConditions = e.weatherConditions;
    }

    public Event() {}

    public List<WeatherCondition> getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(List<WeatherCondition> weatherConditions) {
        this.weatherConditions = weatherConditions;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public boolean isOutdoor() {
        return outdoor;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location=location;
    }

    public List<Calendar> getInvited() {
        return invited;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
    
    public void addInvited(User user, int inviteStatus) {
        Calendar calendar = new Calendar();
        calendar.setUser(user);
        calendar.setEvent(this);
        calendar.setEventId(this.getEventId());
        calendar.setUserEmail(user.getEmail());
        calendar.setInviteStatus(inviteStatus);
        
        if (invited==null){
            invited=new ArrayList<>();
        }
        this.invited.add(calendar);
        user.getEvents().add(calendar);
    }
    
    public void setPublic(boolean b) {
        pub = b;
    }

    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }
    

    public void addWeatherCondition(WeatherCondition wc){
        weatherConditions.add(wc);
        
    }

    public List<Update> getUpdate() {
        return update;
    }

    public void setUpdate(List<Update> update) {
        this.update = update;
    }
    
    public void addUpdate(Update u){
        this.update.add(u);
    }
    public boolean equals(Event event) {
        return this.eventId == event.eventId;
    }

    public boolean isBwodb() {
        return bwodb;
    }

    public void setBwodb(boolean bwodb) {
        this.bwodb = bwodb;
    }

    public boolean isBwtdb() {
        return bwtdb;
    }

    public void setBwtdb(boolean bwtdb) {
        this.bwtdb = bwtdb;
    }

    public void setInvited(List<Calendar> invited) {
        this.invited = invited;
    }
    
    public boolean isPublic(){
        return pub;
    }
    
    
}
    
    

