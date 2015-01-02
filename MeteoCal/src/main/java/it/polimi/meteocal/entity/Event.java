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
 * @author Anton
 */


@Entity (name="EVENT")
@NamedQueries({
        @NamedQuery(name = Event.findAll, 
                query = "SELECT e FROM EVENT e")
})
public class Event implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long eventId;
    
    public static final String findAll = "Event.findAll";
    
    
    @OneToMany(mappedBy="event", cascade = CascadeType.PERSIST)
    private List<Calendar> invited;
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "May not be empty")
    @Column(name = "BEGIN_TIME")
    @Future
    private Date beginTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "May not be empty")
    @Column(name = "END_TIME")
    @Future
    private Date endTime;
    
    @NotNull(message = "May not be empty")
    @Column(name = "NAME")
    private String name;
    
    @NotNull(message = "May not be empty")
    @Column(name = "DESCRIPTION")
    private String description;
    
    @NotNull(message = "May not be empty")
    @Column(name = "PUBLIC_")
    private boolean pub=true;
    
    @NotNull(message = "May not be empty")
    @ManyToOne
    @JoinColumn(name = "EMAIL")
    private User creator;
    
    @Pattern(regexp="[\\w*[ ]*]*[,][\\w*[ ]*]*[,][\\w*[ ]*]*",
             message="write \"address, city, state\"")
    @NotNull(message = "May not be empty")
    @Column(name = "LOCATION")
    private String location;
    
    
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

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }

    public User getCreator() {
        return creator;
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
        pub=b;
    }
    
 
    
}