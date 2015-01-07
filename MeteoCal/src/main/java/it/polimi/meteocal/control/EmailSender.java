package it.polimi.meteocal.control;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class EmailSender {


    public static void send (String recipient, String subject, String body){
        List<String> temp = new ArrayList<>();
        temp.add(recipient);
        EmailSender.send(temp, subject, body);
    }
    public static void send(List<String> recipients, String subject, String body){
        try{
            final String username = "PPPmeteocal@gmail.com";
            final String password = "meteocalPPP";

            Properties props = new Properties();
            props.put("mail.smtps.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
              new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                    }
              });

            Transport t = session.getTransport("smtps");
            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("MeteoCal"));

                for (String recipient : recipients) {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
                }

                message.setSubject(subject + " - MeteoCal");
                message.setText(body);
                t.connect("smtp.gmail.com", username, password);
                t.sendMessage(message, message.getAllRecipients());

            } finally{
                t.close();
            }
        }
        catch(Exception e){
            
        }
    }
}