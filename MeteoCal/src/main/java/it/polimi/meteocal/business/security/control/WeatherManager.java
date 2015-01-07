/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.control;

import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.business.security.entity.WeatherCondition;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
@Lock(LockType.READ) // allows timers to execute in parallel
@Stateless
public class WeatherManager {
    
    @PersistenceContext
    EntityManager em;
    
    
    private List<Event> eventList;
    Date toDate;
    
    @Schedule(minute="*", hour="*")
    public void weatherCreation() throws IOException{
        toDate=new Date();
    eventList=findAll();
    for(int i=0;i<eventList.size();i++){
        if (eventList.get(i).getBeginTime().getYear()==(toDate).getYear()^
                eventList.get(i).getBeginTime().getMonth()==(toDate).getMonth()^
                eventList.get(i).getBeginTime().getDate()<=(toDate).getDate()+5){ 
                int first=eventList.get(i).getLocation().indexOf(",");
                int second=eventList.get(i).getLocation().lastIndexOf(",");        
                String città =eventList.get(i).getLocation().substring(first+1,second) ;
            
            
                Document doc = generateXML(città);
                
                getCondition(doc,eventList.get(i));
    }
      //  if(eventList.get(i).getBeginTime()!=eventList.get(i).getEndTime()){}
            
        
    }
    }
    
    public List<Event> findAll(){
        return em.createNamedQuery(Event.findAll, Event.class)
                                .getResultList();
    }
    
    public static Document generateXML(String name) throws IOException {

            String url = null;
            String XmlData = null;

            // creating the URL
            //"http://weather.yahooapis.com/forecastrss?w=" 
            url =  "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+name+"%2C%20ak%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
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
                System.err.println("unable to load XML: " + ex);
            }
            return doc;
        }
        
        public void getCondition(Document doc,Event event){
            String city = null;
            String unit = null;
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
                            city = e.getAttribute("city");
                            

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
                                          
                                                    
                                        WeatherCondition weather = new WeatherCondition(day,event,e5.getAttribute("text"));
                              
                                        int yet=0;
                                           for(int k=0;k<event.getWeatherConditions().size();k++){
                                            if(event.getWeatherConditions().get(k).getTime().getDate()==giornoGiusto){
                                                event.getWeatherConditions().get(k).setType(weather.getType());
                                                yet=1;
                                            }
                                        }    
       
                                         if(yet==0){
                                           event.addWeatherCondition(new WeatherCondition(day,event,e5.getAttribute("text")));
                                                    
                                                    }
                                        //em.persist(event.getWeatherConditions().get(event.getWeatherConditions().size()-1));
                                           em.merge(event);
                                        
                                       
                                        //}
                                            
                                       
                                        }
                                        }
                                }
                    		}
                    
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        } 
    
}
