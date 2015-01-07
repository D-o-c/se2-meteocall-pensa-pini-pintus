package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.PasswordEncrypter;
import it.polimi.meteocal.entity.Calendar;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.UploadedFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import jxl.Cell;
import jxl.CellType;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * @author aldo
 */
@Stateless
public class UserArea {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    Event selectedEvent; //event to accept or deny
    
    /**
     * Calls EntityManager.find(User.class, principal.getName())
     * @return the logger user
     */
    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
    
    /**
     * Calls EntityManager.find(User.class, PrimaryKey)
     * Sets the logged user privacy
     */
    public void changeCalendarVisibility() {
        boolean temp = getLoggedUser().isPublic();
        temp ^= true;
        getLoggedUser().setPublic(temp);
    }
    
    /**
     * Controls validity of input password and possibly save that password
     * @param inputPassword
     * @param newPassword
     * @return if change has been made correctly
     */
    public boolean changePassword(String inputPassword, String newPassword) {
        String email = getLoggedUser().getEmail();
        String enc_old_psw = getLoggedUser().getPassword();
        String enc_new_psw = PasswordEncrypter.encryptPassword(inputPassword);
        if(enc_new_psw.equals(enc_old_psw)) {
            //update password
            em.find(User.class, email).setPassword(newPassword);
            return true;
        }
        else {
            return false;
        }
    }
    
    public ScheduleModel getCalendar(){
        ScheduleModel calendar = new DefaultScheduleModel();
        List<Calendar> temp = getLoggedUser().getEvents();
        for (Calendar temp1 : temp) {
            if (temp1.getInviteStatus() == 1) {
                Event evntTemp = temp1.getEvent();
                DefaultScheduleEvent dse = new DefaultScheduleEvent(evntTemp.getName(),
                                                                    evntTemp.getBeginTime(),
                                                                    evntTemp.getEndTime());
                dse.setDescription(Long.toString(evntTemp.getEventId()));
                calendar.addEvent(dse);
                
            }
        }
        
        
        return calendar;
    }
    /**
     * Verify timeConsistency of the @param and between event (with inviteStatus == 1) of logged user and @param
     * @param event
     * @return 
     *      -1: if beginTime is AFTER endTime<br/>
     *      -2: if the logged user has another event at the same time<br/>
     *      0: if there isn't problem
     */
    public int timeConsistency(Event event){
        try{
            if (event.getBeginTime().after(event.getEndTime())){ //beginTime is AFTER endTime
                return -1;
            }

            List<Calendar> c = this.getLoggedUser().getEvents();
            for (Calendar c1 : c) {
                if (event.getBeginTime().before(c1.getEvent().getEndTime()) &&
                        event.getEndTime().after(c1.getEvent().getBeginTime()) &&
                        event.getEventId() != c1.getEventId() &&
                        c1.getInviteStatus()==1){
                    return -2;
                }
            }
        }
        catch(NullPointerException e){
                
        }
        return 0;
    }
    
    public void accept(){
        for (int i=0;i<selectedEvent.getInvited().size();i++){
            if (selectedEvent.getInvited().get(i).getUser().getEmail().equals(this.getLoggedUser().getEmail())){
                selectedEvent.getInvited().get(i).setInviteStatus(1);
            }
        }
        for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
            if (this.getLoggedUser().getEvents().get(i).getEventId()==selectedEvent.getEventId()){
                this.getLoggedUser().getEvents().get(i).setInviteStatus(1);
            }
        }
        //e.addInvited(this.getLoggedUser(), 1);
        em.merge(selectedEvent);
        em.merge(this.getLoggedUser());
    }
    public void deny(){
        for (int i=0;i<selectedEvent.getInvited().size();i++){
            if (selectedEvent.getInvited().get(i).getUser().getEmail().equals(this.getLoggedUser().getEmail())){
                selectedEvent.getInvited().get(i).setInviteStatus(-1);
            }
        }
        for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
            if (this.getLoggedUser().getEvents().get(i).getEventId()==selectedEvent.getEventId()){
                this.getLoggedUser().getEvents().get(i).setInviteStatus(-1);
            }
        }
        em.merge(selectedEvent);
        em.merge(this.getLoggedUser());
    }

    /**
     * return a list of event to which the user participates, for export them
     * @return 
     */
    public List<Event> getUserEvent() {
        List<Event> temp = new ArrayList<>();
        for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
            if (this.getLoggedUser().getEvents().get(i).getInviteStatus()==1){
                temp.add(this.getLoggedUser().getEvents().get(i).getEvent());
            }
        }
        return temp;
    }

    public void importXLScalendar(UploadedFile file) {
        HashSet<Event> tempEvent = new HashSet<>();
        Workbook w;
        try{
            w=Workbook.getWorkbook(file.getInputstream());
            Sheet sheet = w.getSheet(0);
            
            for (int i = 1; i < sheet.getRows(); i++) {

                String id;
                Cell cell = sheet.getCell(0, i);
                if (cell.getType() == CellType.LABEL) {
                    LabelCell temp = (LabelCell) cell;
                    id = temp.getString();
                    try{
                        tempEvent.add(em.find(Event.class, Long.parseLong(id)));
                    }
                    catch(Exception e){

                    }
                }

            }
            
        }
        catch(IOException | BiffException | IndexOutOfBoundsException | NullPointerException e){
            
        }
        
        this.importCalendar(tempEvent);
    }

    public void importCSVcalendar(UploadedFile file) {
        HashSet<Event> tempEvent = new HashSet<>();
        BufferedReader br;
        String line;
        
        try{
            br = new BufferedReader(new InputStreamReader(file.getInputstream(), "UTF-8"));
            while ((line = br.readLine()) != null){
                String[] singleLine = line.split(",");
                String id = singleLine[0].substring(1, singleLine[0].length()-1);
                try{
                    tempEvent.add(em.find(Event.class, Long.parseLong(id)));
                }
                catch(Exception e){
                    
                }
            }
            
        }
        catch(Exception e){
            
        }
        this.importCalendar(tempEvent);
    }

    public void importXMLcalendar(UploadedFile file) {
        HashSet<Event> tempEvent = new HashSet<>();
        try {
 
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                                 .newDocumentBuilder();

            Document dom = dBuilder.parse(file.getInputstream());
            
            //get the root element
            Element docEle = dom.getDocumentElement();

            //get a nodelist of elements
            NodeList nl = docEle.getElementsByTagName("event");
            
            if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the event element
				Element el = (Element)nl.item(i);

				//get the Event object
				Event e = getEvent(el);

				//add it to list
				tempEvent.add(e);
			}
		}
            
 
        } catch (ParserConfigurationException | IOException | SAXException e) {
        }
        this.importCalendar(tempEvent);
    }
    
    
    /**
     * Get event from xml Element
     * @param e
     * @return 
     */
    private Event getEvent(Element e){
        long id;
        String temp = null;
        
        NodeList nl = e.getElementsByTagName("id");
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            temp = el.getFirstChild().getNodeValue();
        }
        id = Long.parseLong(temp);
        return em.find(Event.class, id); 
    }
    
    private void importCalendar(HashSet<Event> e){
        for (int i=0;i<this.getLoggedUser().getEvents().size();i++){
            if (!this.getLoggedUser().getEvents().get(i).getEvent().getCreator().getEmail().equals(this.getLoggedUser().getEmail())){
                this.getLoggedUser().getEvents().get(i).setInviteStatus(-1);
            }
        }
        List<Event> events = new ArrayList<>(e);
        //Event[] events = (Event[]) e.toArray();
        for (Event event : events) {
            if (this.timeConsistency(event)==0){
                for (int j = 0; j < event.getInvited().size(); j++) {
                    if (event.getInvited().get(j).getUser().getEmail().equals(this.getLoggedUser().getEmail())) {
                        event.getInvited().get(j).setInviteStatus(1);
                        
                    }
                }

                for (int i = 0; i < this.getLoggedUser().getEvents().size(); i++){
                    if (this.getLoggedUser().getEvents().get(i).getEventId()==event.getEventId()){
                        this.getLoggedUser().getEvents().get(i).setInviteStatus(1);
                    }
                }
                em.merge(event);
                em.merge(this.getLoggedUser());
            }
        }
    }
        
    
    
    
    
}


