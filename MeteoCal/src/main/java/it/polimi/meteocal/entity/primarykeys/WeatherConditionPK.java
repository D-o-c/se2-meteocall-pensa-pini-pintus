package it.polimi.meteocal.entity.primarykeys;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
public class WeatherConditionPK implements Serializable{
    
    private long event;
    private Date time;

    /**
     * Empty Constructor
     */
    public WeatherConditionPK() {}

    /**
     * Constructor
     * @param event
     * @param time 
     */
    public WeatherConditionPK(long event, Date time) {
        this.event = event;
        this.time = time;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof WeatherConditionPK) {
            WeatherConditionPK pk = (WeatherConditionPK)object;
            return event==pk.event && time.equals(pk.time);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
         return (int)(event + time.hashCode());
    }
    
    
}
