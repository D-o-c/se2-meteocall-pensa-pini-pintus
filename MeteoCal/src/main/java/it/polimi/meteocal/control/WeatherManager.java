package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.WeatherCondition;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.primarykeys.WeatherConditionPK;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


//@Lock(LockType.WRITE) // not allows timers to execute in parallel
@Singleton
public class WeatherManager {
    
    //Constants
    private static final int five_days = 432000000;
    private static final String yahoo_url_part1 =
            "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22";
    private static final String yahoo_url_part2 =
            "%2C%20ak%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    
    @PersistenceContext
    EntityManager em;
    
    //Control
    @Inject
    UpdateManager um;
    
    List<Event> eventstList;
    Date today;
    
    @Schedule(second="*", minute="*", hour="*/12")
    public void sunSearchWeather(){
        this.searchWeather();
    }
    
    /**
     * Calls
     * Method performed every * minutes * hours
     */
    @Asynchronous
    public void searchWeather(){
        
        try{
            today = new Date();
            eventstList = em.createNamedQuery(Event.findAll, Event.class).getResultList();

            for (Event event : eventstList) {
                
                Boolean cont = true;
                
                while(cont){
                    try{
                        em.lock(event, LockModeType.PESSIMISTIC_WRITE);
                        cont=false;
                    }
                    catch(Exception e){
                    }
                }
                //SEARCH WEATHER IF
                //event is outdoor
                //event.begin <= today + 5 (event begins in the next five days)
                //(Yahoo cannot retrieve weather of day > today + 5)
                //!event.end < today (event is not ended yet)
                if (event.getBeginTime().getTime() <= today.getTime() + five_days &&
                        event.isOutdoor() &&
                        !event.getEndTime().before(today)) {

                    int start = event.getLocation().indexOf(",")+1;
                    int end = event.getLocation().lastIndexOf(",");

                    String city = event.getLocation().substring(start, end).replaceAll(" ", "+");

                    //generation of xml document
                    Document document = generateXML(city);

                    //save weather conditions
                    saveWeatherConditions(document, event);

                }//endif
                
                em.lock(event, LockModeType.NONE);
            }//endfor
            um.sendNotifies();
        }
        catch(Exception e){}
    }
    
    /**
     * Calls parse(inputStream)
     * @param city
     * @return xml document of weather conditions from yahoo meteo
     * @throws IOException 
     */
    public static Document generateXML(String city) throws IOException {

        URL xmlUrl = new URL(yahoo_url_part1+city+yahoo_url_part2);
        URLConnection conn = xmlUrl.openConnection();
        conn.setConnectTimeout(2000);
        
        InputStream in = conn.getInputStream();

        // parsing the XmlUrl
        Document document = parse(in);

        return document;
    }
    
    /**
     * Parsing of inputStream
     * @param is : InputStream
     * @return document, result of input stream parsing
     */
    public static Document parse(InputStream is) {
        Document doc = null;
        DocumentBuilderFactory domFactory;
        DocumentBuilder builder;

        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            domFactory.setNamespaceAware(false);
            builder = domFactory.newDocumentBuilder();

            doc = builder.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {}
        
        return doc;
    }
    
    /**
     * 
     * @param document
     * @param event 
     */
    public void saveWeatherConditions(Document document, Event event){
       
        //Calculate the number of days of the event
        int numDaysEvent = calculateNumDays(event);
        
        try {
            document.getDocumentElement().normalize();
            
            NodeList queryList = document.getElementsByTagName("query");
            for (int q = 0; q < queryList.getLength(); q++) {
                
                Node query = queryList.item(q);
                if (query.getNodeType() == Node.ELEMENT_NODE) {
                    
                    Element eElement = (Element) query;
                    
                    /*NodeList locationList = eElement.getElementsByTagName("yweather:location");
                    for (int l = 0; l < locationList.getLength(); l++) {
                        Node location = locationList.item(l);
                        if (query.getNodeType() == Node.ELEMENT_NODE) {
                            Element e = (Element) location;
                        }
                    }*/ 

                    NodeList forecasts = eElement.getElementsByTagName("yweather:forecast");
                    for(int j = 0; j < numDaysEvent; j++) {
                        
                        for (int f = 0; f < forecasts.getLength(); f++) {
                            
                            Node n5 = forecasts.item(f);
                            if(query.getNodeType() == Node.ELEMENT_NODE) {
                                
                                Element forecast = (Element) n5;
                                Date day =
                                        new Date(   event.getBeginTime().getYear(),
                                                    event.getBeginTime().getMonth(),
                                                    event.getBeginTime().getDate() + j);
                                int theDay = f + today.getDate();
                                if(day.getDate() == theDay ){

                                    WeatherCondition weather = new WeatherCondition(
                                                                    day,
                                                                    event,
                                                                    forecast.getAttribute("text"),
                                                                    Integer.parseInt(forecast.getAttribute("code")));
                                    
                                    int temperature = (Integer.parseInt(forecast.getAttribute("high"))-32)*5/9;
                                    weather.setTemp(temperature);
                                    boolean weatherExists = false;
                                    for(int k = 0; k < event.getWeatherConditions().size(); k++){
                                        
                                        if(event.getWeatherConditions().get(k).getTime().getDate()== theDay){
                                        
                                            int tempCode = event.getWeatherConditions().get(k).getCode();
                                            event.getWeatherConditions().remove(k);
                                            WeatherConditionPK pk = new WeatherConditionPK(event.getEventId(), day);
                                            WeatherCondition w = em.find(WeatherCondition.class, pk);
                                            em.remove(w);
                                            em.merge(event);
                                            em.flush();
                                            weather.setOldCode(tempCode);
                                            em.persist(weather);
                                            event.addWeatherCondition(weather);
                                            em.merge(event);
                                            weatherExists = true;
                                            k--;
                                        }
                                    }    

                                    if(!weatherExists){
                                        em.persist(weather);
                                        event.addWeatherCondition(weather);
                                        em.merge(event);
                                    }

                                    em.flush();

                                }//endif
                            
                            }//endif
                        
                        }//endfor
                    
                    }//endfor
                
                }//endif
            
            }//endfor
            
        } catch (Exception e) {}
    }
    
    /**
     * @param event
     * @return numDaysEvent : number of days of an event
     */
    private int calculateNumDays(Event event){
        int numDaysEvent = event.getEndTime().getDate() - event.getBeginTime().getDate();
        if (event.getBeginTime().getDate() < event.getEndTime().getDate()){
            return numDaysEvent + 1;
        }
        return numDaysEvent + 32 ;
    }
    
    

    

    
}
