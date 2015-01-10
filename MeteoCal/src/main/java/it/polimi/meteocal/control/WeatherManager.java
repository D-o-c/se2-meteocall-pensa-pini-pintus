/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.control;

import it.polimi.meteocal.entity.WeatherCondition;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.primarykeys.WeatherConditionPK;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Anton
 */

@Singleton
@Lock(LockType.WRITE) // not allows timers to execute in parallel
@Stateless
public class WeatherManager {
    
    @PersistenceContext
    EntityManager em;
    
    
    private List<Event> eventList;
    Date toDate;
    
    @Schedule(minute="*", hour="*")
    public void weatherCreation() throws IOException{
        toDate = new Date();
        eventList = em.createNamedQuery(Event.findAll, Event.class).getResultList();
        for (Event eventList1 : eventList) {
            if (eventList1.getBeginTime().getTime() <= toDate.getTime() + 432000000 && 
                    eventList1.isOutdoor() == true  && eventList1.getEndTime().after(toDate)) {
                int first = eventList1.getLocation().indexOf(",");
                int second = eventList1.getLocation().lastIndexOf(",");
                String city = eventList1.getLocation().substring(first+1, second);
                city = city.replaceAll(" ", "+");
                Document doc = generateXML(city);
                getCondition(doc, eventList1);
            }
        }

    }
    
    public static Document generateXML(String name) throws IOException {

        String url;

        // creating the URL
        //"http://weather.yahooapis.com/forecastrss?w=" 
        url =  "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+name+"%2C%20ak%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        URL xmlUrl = new URL(url);
        InputStream in = xmlUrl.openStream();

        // parsing the XmlUrl
        Document doc = parse(in);

        return doc;

    }

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
        } catch (Exception ex) {
        }
        return doc;
    }

    public void getCondition(Document doc,Event event){
       // String city = null;
        try {

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("query");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    NodeList nl = eElement
                            .getElementsByTagName("yweather:location");

                    for (int tempr = 0; tempr < nl.getLength(); tempr++) {

                        Node n = nl.item(tempr);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element e = (Element) n;
                          //  city = e.getAttribute("city");


                        }
                    } 


                    NodeList n15 =eElement.getElementsByTagName("yweather:forecast");
                    int daysNumber;
                    if (event.getBeginTime().getDate()<event.getEndTime().getDate()){
                        daysNumber=event.getEndTime().getDate()-event.getBeginTime().getDate()+1;
                    }else{
                        daysNumber=event.getEndTime().getDate()+32-event.getBeginTime().getDate();
                    }

                    for(int i=0;i<daysNumber;i++){
                        for (int tempr=0;tempr< n15.getLength();tempr++){
                            Node n5 =n15.item(tempr);

                            if(nNode.getNodeType() ==Node.ELEMENT_NODE){
                                    Element e5 =(Element) n5;

                            Date day=new Date(event.getBeginTime().getYear(),event.getBeginTime().getMonth(),event.getBeginTime().getDate()+i);//giorno dell'evento

                            int giornoGiusto=tempr+toDate.getDate();//oggi+tempr 
                            if(day.getDate()==giornoGiusto ){
                                
                                String prova = e5.getAttribute("temp");

                                WeatherCondition weather = new WeatherCondition(day,
                                                                event,
                                                                e5.getAttribute("text"),
                                                                Integer.parseInt(e5.getAttribute("code")));

                                int yet=0;
                                for(int k=0;k<event.getWeatherConditions().size();k++){
                                    if(event.getWeatherConditions().get(k).getTime().getDate()==giornoGiusto){
                                        /*int tempCode = event.getWeatherConditions().get(k).getCode();
                                        event.getWeatherConditions().set(k,weather);
                                        event.getWeatherConditions().get(k).setOldCode(tempCode);
                                        weather.setOldCode(tempCode);*/
                                        
                                        int tempCode = event.getWeatherConditions().get(k).getCode();
                                        event.getWeatherConditions().remove(k);
                                        WeatherConditionPK pk = new WeatherConditionPK(event.getEventId(), day);
                                        WeatherCondition w = em.find(WeatherCondition.class, pk);
                                        em.remove(w);
                                        em.flush();
                                        weather.setOldCode(tempCode);
                                        em.persist(weather);
                                        event.addWeatherCondition(weather);
                                        em.merge(event);
                                       // em.merge(event);
                                        yet=1;
                                    }
                                }    

                                if(yet==0){
                                    //event.addWeatherCondition(new WeatherCondition(day,event,e5.getAttribute("text")));
                                    em.persist(weather);
                                    event.addWeatherCondition(weather);
                                    em.merge(event);

                                }

                                //em.merge(event);
                                //em.flush();
                                
                                }
                            }
                        }
                    }

                }
            }

        }
        catch (Exception e) {
        }
    }
}
