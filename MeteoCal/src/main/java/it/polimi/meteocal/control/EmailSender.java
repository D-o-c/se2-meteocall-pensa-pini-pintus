package it.polimi.meteocal.control;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
 
@Singleton
@Asynchronous
public class EmailSender {

    @PersistenceContext
    EntityManager em;
    
    /**
     * Sends an email
     * @param recipient
     * @param subject
     * @param body 
     */
    @Asynchronous
    public void send (String recipient, String subject, String body){
        List<String> temp = new ArrayList<>();
        temp.add(recipient);
        this.send(temp, subject, body);
    }
    
    /**
     * Sends one or more emails
     * @param recipients
     * @param subject
     * @param body 
     */
    @Asynchronous
    public void send(List<String> recipients, String subject, String body){
        try {
            InitialContext ctx = new InitialContext();
            Session session = (Session) ctx.lookup("mail/Gmail");
        

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("MeteoCal"));

            for (String recipient : recipients) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
            }

            message.setSubject(subject + " - MeteoCal");
            message.setText(body);
            
            
            Transport.send(message);
        }
        catch(NamingException | MessagingException e){}
    }
}