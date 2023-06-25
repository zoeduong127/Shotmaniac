package shotmaniacs.group2.di.emails;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Mailer {

    private static final String HOST = "smtp.gmail.com";
    private static final String PASSWORD = "uxcwfkbcqiwbhvmw";
    private static final String EMAIL = "shotmaniacs2mod04@gmail.com";
    private static Session session;
    private static String path;

    private Mailer() {
    }

    static {
//        try {
//            loadHTMLFile(new File(System.getProperty("user.dir")).getParent() + "\\webapps\\shotmaniacs2\\email");
//        } catch (IOException ignored) {
//        }
//        try {
//            loadHTMLFile(new File(System.getProperty("user.dir")) + "\\src\\main\\webapp\\email");
//        } catch (IOException ignored) {
//        }
        path = new File(System.getProperty("user.dir")) + "\\src\\main\\webapp\\email";

        System.out.println();
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        session = Session.getInstance(properties, new jakarta.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(EMAIL, PASSWORD);

            }
        });

        // Used to debug SMTP issues
        session.setDebug(true);

    }

    public static void sendEmail(String to, String subject, String HTMLContent) throws MessagingException {
        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(EMAIL));

        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set Subject: header field
        message.setSubject(subject);

        // Now set the actual message
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(HTMLContent,"text/html");

        // Create the multipart object and add the HTML body part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Set the multipart as the message's content
        message.setContent(multipart);

        System.out.println("sending email notification...");
        // Send message
        Transport.send(message);
        System.out.println("Sent message successfully....");
    }

    private static String loadHTMLFile(String path) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(path));
        String str;
        while ((str = in.readLine()) != null) {
            contentBuilder.append(str);
        }
        in.close();
        return contentBuilder.toString();
    }

    public static void sendEnrolmentNotification(int enrolmentId) {
        // TODO
    }

    public static void sendBookingCancellation() {
        // TODO
    }

    public static void sendBookingUnenrolment(int enrolmentId) {
        // TODO
    }

//    public static void main(String[] args) {
//        try {
//            sendEmail("lucafuertes@gmail.com", "testing", "");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }

}
