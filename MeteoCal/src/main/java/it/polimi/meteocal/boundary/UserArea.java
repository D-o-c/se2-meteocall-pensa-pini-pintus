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
 */
@Stateless
public class UserArea {
    
    //Controls
    @Inject
    UserManager userManager;
    @Inject
    EventManager eventManager;
    @Inject
    GuestManager guestManager;
    
    //event to accept or deny
    Event selectedEvent;
    
    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
    
    /**************************************************************************/
    
    /**
     * Calls userManager.changeCalendarVisibility(guestManager.getLoggedUser());
     */
    public void changeCalendarVisibility() {
        userManager.changeCalendarVisibility(guestManager.getLoggedUser());
    }
    
    /**
     * @param inputPassword
     * @param newPassword
     * @return true if change is ok
     */
    public boolean changePassword(String inputPassword, String newPassword) {
        
        String enc_old_psw = guestManager.getLoggedUser().getPassword();
        String enc_new_psw = PasswordEncrypter.encryptPassword(inputPassword);
        
        if (enc_old_psw.equals(enc_new_psw)){
            userManager.changePassword(guestManager.getLoggedUser(), newPassword);
            return true;
        }
        return false;
    }
    
    /**
     * @return userManager.getCalendar(guestManager.getLoggedUser());
     */
    public ScheduleModel getCalendar() {
        return userManager.getCalendar(guestManager.getLoggedUser());
    }
    
    /**
     * -1: if beginTime is AFTER endTime<br/>
     * -2: if the logged user has another event at the same time<br/>
     *  0: no problems
     * @param event
     * @return userManager.timeConsistency(guestManager.getLoggedUser(), event)
     */
    public int timeConsistency(Event event){
        return userManager.timeConsistency(guestManager.getLoggedUser(), event);
    }
    
    /**
     * Calls userManager.acceptInvite(guestManager.getLoggedUser(), selectedEvent);
     */
    public void accept(){
        userManager.answerInvite(guestManager.getLoggedUser(), selectedEvent, 1);
    }
   
    /**
     * Calls userManager.denyInvite(guestManager.getLoggedUser(), selectedEvent);
     */
    public void deny(){
        userManager.answerInvite(guestManager.getLoggedUser(), selectedEvent, -1);
    }

    /**
     * @return userManager.getUserEvent(loggedUser)
     */
    public List<Event> getUserEvent() {
        return userManager.getUserEvent(guestManager.getLoggedUser());
        
    }

    /**
     * @param file
     * @return 0 no problem, -1 file exceptions, -2 time inconsistent
     */
    public int importXLScalendar(UploadedFile file) {
        
        HashSet<Event> events = new HashSet<>();
        Workbook w;
        
        try {
            w = Workbook.getWorkbook(file.getInputstream());
            Sheet sheet = w.getSheet(0);
            
            for (int i = 1; i < sheet.getRows(); i++) {

                String id;
                Cell cell = sheet.getCell(0, i);
                
                if (cell.getType() == CellType.LABEL) {
                    LabelCell lcell = (LabelCell) cell;
                    id = lcell.getString();
                    
                    Event tempEvent = eventManager.find(Long.parseLong(id));
                    if (tempEvent != null){
                        events.add(tempEvent);
                    }
                }//endif
            }//endfor
        } catch(IOException | BiffException | IndexOutOfBoundsException | NullPointerException e) {
            return -1;
        }
        return userManager.importCalendar(guestManager.getLoggedUser(), events);
    }

    /**
     * @param file
     * @return 0 no problem, -1 file exceptions, -2 time inconsistent
     */
    public int importCSVcalendar(UploadedFile file) {
        
        HashSet<Event> events = new HashSet<>();
        BufferedReader br;
        String line;
        
        try {
            
            br = new BufferedReader(new InputStreamReader(file.getInputstream(), "UTF-8"));
            br.readLine();
            while ((line = br.readLine()) != null){
                String[] singleLine = line.split(",");
                String id = singleLine[0].substring(1, singleLine[0].length()-1);
                
                Event tempEvent = eventManager.find(Long.parseLong(id));
                if (tempEvent != null){
                    events.add(tempEvent);
                }
            }//endwhile
        } catch(Exception e){
            return -1;
        }
        return userManager.importCalendar(guestManager.getLoggedUser(),events);
    }

    /**
     * @param file
     * @return 0 no problem, -1 file exceptions, -2 time inconsistent
     */
    public int importXMLcalendar(UploadedFile file) {
        
        HashSet<Event> events = new HashSet<>();
        
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
                    
                    if (e != null){
                        //add it to list
                        events.add(e);
                    }
                }
            }
            
 
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return -1;
        }
        return userManager.importCalendar(guestManager.getLoggedUser(),events);
    }
    
    /**
     * Gets an event from an XML element
     * @param root
     * @return eventManager.find(Long.parseLong(id))
     */
    private Event getEvent(Element root){
        
        String id = "";
        
        NodeList idList = root.getElementsByTagName("id");
        
        if(idList != null && idList.getLength() > 0) {
            Element e = (Element) idList.item(0);
            id = e.getFirstChild().getNodeValue();
        }
        
        return eventManager.find(Long.parseLong(id)); 
    }
    
    /**
     * @return userManager.getNotifies(loggedUser);
     */
    public List<Update> getUpdates() {
        return userManager.getNotifies(guestManager.getLoggedUser());
    }

    /**
     * Calls userManager.setNotifyRead(update)
     * @param update 
     */
    public void setNotifyRead(Update update) {
        userManager.setNotifyRead(update);
    }
    
    /**
     * Calls userManager.setAllNotifyRead()
     */
    public void setAllNotifyRead() {
        userManager.setAllNotifyRead(guestManager.getLoggedUser());
    }
    
    public boolean allRead() {
        return userManager.allRead(guestManager.getLoggedUser());
    }
    
    /**
     * @return getInvites().size() + getNumberOfNotReadNotifies()
     */
    public int getNumberOfNotifies(){
        return getInvites().size() + getNumberOfNotReadNotifies();
    }

    /**
     * @return userManager.getNumberOfNotReadNotifies(guestManager.getLoggedUser());
     */
    private int getNumberOfNotReadNotifies() {
        return userManager.getNumberOfNotReadNotifies(guestManager.getLoggedUser());
    }
    
    /**
     * @return userManager.getInvites(loggedUser)
     */
    public List<Event> getInvites() {
        return userManager.getInvites(guestManager.getLoggedUser());
    }

    /**
     * @return guestManager.getLoggedUser().getName()
     */
    public String getName() {
        return guestManager.getLoggedUser().getName();
    }
    
    /**
     * @return guestManager.getLoggedUser().isPublic()
     */
    public boolean isLoggedUserPublic(){
        return guestManager.getLoggedUser().isPublic();
    }

    /**
     * @return userManager.getContacts(loggedUser);
     */
    public List<Contact> getContact() {
        return userManager.getContacts(guestManager.getLoggedUser());
    }

    /**
     * Calls userManager.deleteContact(loggedUser, contact);
     * @param contact 
     */
    public void deleteContact(Contact contact) {
        userManager.deleteContact(guestManager.getLoggedUser(), contact);
    }
   
}


