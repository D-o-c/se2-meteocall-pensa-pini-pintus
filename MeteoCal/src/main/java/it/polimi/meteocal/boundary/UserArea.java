package it.polimi.meteocal.boundary;

import it.polimi.meteocal.control.EventManager;
import it.polimi.meteocal.control.GuestManager;
import it.polimi.meteocal.control.PasswordEncrypter;
import it.polimi.meteocal.control.UserManager;
import it.polimi.meteocal.entity.Contact;
import it.polimi.meteocal.entity.Event;
import it.polimi.meteocal.entity.Update;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * DA COMMENTARE
 */
@Stateless
public class UserArea {
    
    @Inject
    Principal principal;
    
    @Inject
    UserManager um;
    
    @Inject
    EventManager em;
    
    @Inject
    GuestManager gm;
    
    Event selectedEvent; //event to accept or deny
    
    

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
    
    /*
     * OKOK
     */
    public void changeCalendarVisibility() {
        um.changeCalendarVisibility(gm.getLoggedUser());
    }
    
    /*
     * OKOK 
     */
    public boolean changePassword(String inputPassword, String newPassword) {
        
        String enc_old_psw = gm.getLoggedUser().getPassword();
        String enc_new_psw = PasswordEncrypter.encryptPassword(inputPassword);
        
        if (enc_old_psw.equals(enc_new_psw)){
            um.changePassword(gm.getLoggedUser(), newPassword);
            return true;
        }
        return false;
        
    }
    
    /*
     * OKOK
     */
    public ScheduleModel getCalendar(){
        return um.getCalendar(gm.getLoggedUser());
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
        return um.timeConsistency(gm.getLoggedUser(), event);
    }
    
    /*
     * OKOK 
     */
    public void accept(){
        um.acceptInvite(gm.getLoggedUser(), selectedEvent);
        
    }
    
    /*
     * OKOK
     */
    public void deny(){
        um.denyInvite(gm.getLoggedUser(), selectedEvent);
    }

    /*
     * OKOK
     */
    public List<Event> getUserEvent() {
        return um.getUserEvent(gm.getLoggedUser());
        
    }

    public int importXLScalendar(UploadedFile file) {
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
                        tempEvent.add(em.find(Long.parseLong(id)));
                    }
                    catch(Exception e){
                        return -1;
                    }
                }

            }
            
        }
        catch(IOException | BiffException | IndexOutOfBoundsException | NullPointerException e){
            return -1;
        }
        
        return um.importCalendar(gm.getLoggedUser(), tempEvent);
    }

    public int importCSVcalendar(UploadedFile file) {
        HashSet<Event> tempEvent = new HashSet<>();
        BufferedReader br;
        String line;
        
        try{
            br = new BufferedReader(new InputStreamReader(file.getInputstream(), "UTF-8"));
            while ((line = br.readLine()) != null){
                String[] singleLine = line.split(",");
                String id = singleLine[0].substring(1, singleLine[0].length()-1);
                try{
                    tempEvent.add(em.find(Long.parseLong(id)));
                }
                catch(Exception e){
                    return -1;
                }
            }
            
        }
        catch(Exception e){
            return -1;
        }
        return um.importCalendar(gm.getLoggedUser(),tempEvent);
    }

    public int importXMLcalendar(UploadedFile file) {
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
            return -1;
        }
        return um.importCalendar(gm.getLoggedUser(),tempEvent);
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
        return em.find(Long.parseLong(temp)); 
    }
    
    

    public List<Update> getUpdate() {
        return um.getNotifies(gm.getLoggedUser());
    }

    public void setNotifyRead(Update u) {
        um.setNotifyRead(u);
        
    }
    
    public int getNumberOfNotifies(){
        
        return this.getInvites().size() + um.getNumberOfNotReadedNotifies(gm.getLoggedUser());
    }

    public List<Event> getInvites() {
        return um.getInvites(gm.getLoggedUser());
    }

    public String getName() {
        return gm.getLoggedUser().getName();
    }
    
    public boolean isLoggedUserPublic(){
        return gm.getLoggedUser().isPublic();
    }

    public List<Contact> getContact() {
        return um.getContacts(gm.getLoggedUser());
    }

    /**
     * Deletes a contact of the logged user
     * Calls EntityManager.find(Contact.class, Primary Key)
     * Calls EntityManager.remove(contact)
     * @param contact 
     */
    public void deleteContact(String contactEmail) {
        um.deleteContact(gm.getLoggedUser(), contactEmail);
        
        
    }
        
    
    
    
    
}


