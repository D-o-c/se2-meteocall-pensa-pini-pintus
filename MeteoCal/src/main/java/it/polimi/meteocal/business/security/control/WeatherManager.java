/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.meteocal.business.security.control;

import it.polimi.meteocal.business.security.entity.Event;
import it.polimi.meteocal.business.security.entity.WeatherCondition;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
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
public class WeatherManager {
    
    EntityManager em;
    
    private List<Event> eventList = findAll();
    Date toDate=new Date();
    public void weatherCreation() throws IOException{
    for(int i=0;i<eventList.size();i++){
        if (eventList.get(i).getBeginTime().getYear()==(toDate).getYear()^
                eventList.get(i).getBeginTime().getMonth()==(toDate).getMonth()^
                eventList.get(i).getBeginTime().getDay()==(toDate).getDay()+5){ 
                
                Document doc = generateXML(eventList.get(i).getLocation());
                
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
                            System.out.println("The City Is : " + city);

                        }
                   } 
                                
                    
                    		NodeList n15 =eElement.getElementsByTagName("yweather:forecast");
                            for(int i=0;i<(event.getEndTime().getDay()-event.getBeginTime().getDay());i++){//se segue i giorni e abbiamo un array di weather basta seguire l'arrey
                    		for (int tempr=0;tempr< n15.getLength();tempr++){
                    			Node n5 =n15.item(tempr);
                                        Date day=new Date(event.getBeginTime().getDay()+i,event.getBeginTime().getMonth(),event.getBeginTime().getYear());
                                        
                    			if(nNode.getNodeType() ==Node.ELEMENT_NODE){
                    				Element e5 =(Element) n5;
                    				System.out.println("forecast "+e5.getAttribute("day")+" "+ e5.getAttribute("text"));
                    			WeatherCondition weather = new WeatherCondition();
                                        weather.setEventId(event);
                                       // it creates the WeatherCondition
                                        if(day.getDay()==(Integer.parseInt(e5.getAttribute("date").substring(0,1))) )
                                        weather.setType(e5.getAttribute("text"));
                                        weather.setTime(day);
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
