package pt.unl.fct.di.apdc.geo5.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import java.util.Properties;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class Email extends HttpServlet {
    private static final String ADDRESS = "no-reply@apdc-geoproj.appspotmail.com";
    
    private static final Logger LOG = Logger.getLogger(Email.class.getName());

    public static void sendEmail(String recipientEmail, String activationCode) {
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);

        // Used to debug SMTP issues
        session.setDebug(true);
        
        try {
            MimeMessage msg = new MimeMessage(session);
            // Sender's email ID needs to be mentioned
            msg.setFrom(new InternetAddress(ADDRESS));
            // Recipient's email ID needs to be mentioned.
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            // Set Subject: header field
            msg.setSubject("GeoPlaces: Activate your account", "utf-8");
            // Now set the actual message
            msg.setContent("Welcome to GeoPlaces! Your activation code: " + activationCode, "text/html; charset=utf-8");
            Transport.send(msg);
        } catch (MessagingException e) {
            LOG.warning("Error sending email");
        }
    }
}