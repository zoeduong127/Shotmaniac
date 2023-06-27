package shotmaniacs.group2.di.emails;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.dto.EnrolmentDto;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.resources.BookingResource;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Mailer {

    private static String dbHOST = "bronto.ewi.utwente.nl";
    private static String dbNAME ="dab_dsgnprj_50";
    private static String dbURL = "jdbc:postgresql://" + dbHOST + ":5432/" +dbNAME+"?currentSchema=dab_dsgnprj_50";
    private static String dbPASSWORD = "yummybanana";

    private static final String HOST = "smtp.gmail.com";
    private static final String PASSWORD = "uxcwfkbcqiwbhvmw";
    private static final String EMAIL = "shotmaniacs2mod04@gmail.com";
    private static Session session;
    private static String PATH;
    private static final String NEWBOOKINGEMAIL = "shotmaniacs2mod04@gmail.com";

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
        //PATH = new File(System.getProperty("user.dir")) + "\\src\\main\\webapp\\email"; // TODO: Ensure this path is correct before deployment
        PATH = new File(System.getProperty("user.dir")).getParent() + "\\webapps\\shotmaniacs2\\email";
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

    public static void sendEnrolmentNotification(int enrolmentId) throws MessagingException {
        EnrolmentDto enrolmentDetails = getEnrolmentDetails(enrolmentId);
        if (enrolmentDetails == null) {
            throw new MessagingException();
        }
        Booking booking = BookingDao.instance.getABooking(enrolmentDetails.getBookingId());
        Account account = AccountDao.instance.getAccountById(enrolmentDetails.getCrewMemberId());

        if (booking == null || account == null) {
            throw new MessagingException();
        }

        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "\\enrolmentNotification.html"));

            doc.getElementById("title").html(" You have been enrolled into: <br> " + booking.getName());
            doc.getElementById("greeting").text("Hi, " + account.getUsername());
            doc.getElementById("description").text("Below you will find details about the enrolled booking.");
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + booking.getDescription());
            doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + booking.getClientName());
            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + booking.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + booking.getLocation());

            sendEmail(account.getEmail(), "New Enrolment", doc.html()); //TODO: Change this to send to account's email
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }
    }

    public static void sendBookingCancellation(int enrolmentId, String reason) throws MessagingException {
        EnrolmentDto enrolmentDetails = getEnrolmentDetails(enrolmentId);
        if (enrolmentDetails == null) {
            throw new MessagingException();
        }

        Booking booking = BookingDao.instance.getABooking(enrolmentDetails.getBookingId());
        Account account = AccountDao.instance.getAccountById(enrolmentDetails.getCrewMemberId());
        if (booking == null || account == null) {
            throw new MessagingException();
        }

        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "\\deenrolmentNotification.html"));

            doc.getElementById("title").html(" Cancelled Booking: <br> " + booking.getName());
            doc.getElementById("greeting").text("Hi, " + account.getUsername());
            doc.getElementById("description").html("A booking you were enrolled in was cancelled. The reason specified was: <br /> " + reason);
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + booking.getDescription());
            doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + booking.getClientName());
            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + booking.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + booking.getLocation());

            sendEmail(account.getEmail(), "Booking Was Cancelled", doc.html());
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }    }

    public static void sendBookingDeenrolment(int enrolmentId) throws MessagingException {
        EnrolmentDto enrolmentDetails = getEnrolmentDetails(enrolmentId);
        if (enrolmentDetails == null) {
            throw new MessagingException();
        }

        Booking booking = BookingDao.instance.getABooking(enrolmentDetails.getBookingId());
        Account account = AccountDao.instance.getAccountById(enrolmentDetails.getCrewMemberId());

        if (booking == null || account == null) {
            throw new MessagingException();
        }
        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "\\deenrolmentNotification.html"));

            doc.getElementById("title").html(" You have been de-enrolled from: <br> " + booking.getName());
            doc.getElementById("greeting").text("Hi, " + account.getUsername());
            doc.getElementById("description").text("You have been de-enrolled from a booking. Below you will find more details: ");
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + booking.getDescription());
            doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + booking.getClientName());
            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + booking.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + booking.getLocation());

            sendEmail(account.getEmail(), "Booking De-enrolment", doc.html());
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }
    }

    public static void sendNewBookingNotification(int bookingId) throws MessagingException {
        Booking booking = BookingDao.instance.getABooking(bookingId);
        if (booking == null) {
            throw new MessagingException();
        }

        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "\\newBookingNotification.html"));

            doc.getElementById("title").html(" New Booking Was Submitted: <br> " + booking.getName());
            doc.getElementById("description").text("Below you will find details about the new booking.");
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + booking.getDescription());
            doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + booking.getClientName());
            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + booking.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + booking.getLocation());
            doc.getElementById("client_contact").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client contact: </strong><br /> Phone: " + booking.getPhoneNumber() + "<br /> Email: " + booking.getClientEmail());
            sendEmail(NEWBOOKINGEMAIL, "New Booking", doc.html());
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }
    }

    private static EnrolmentDto getEnrolmentDetails(int enrolmentId) {
        String query = "SELECT e.booking_id, e.crew_member_id FROM enrolment e WHERE e.enrolment_id = ?";

        EnrolmentDto enrolmentDto = new EnrolmentDto();

        try {
            Connection connection = DriverManager.getConnection(dbURL, dbNAME, dbPASSWORD);
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setInt(1, enrolmentId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            enrolmentDto.setBookingId(rs.getInt(1));
            enrolmentDto.setCrewMemberId(rs.getInt(2));
            return enrolmentDto;
        } catch (SQLException e) {
            System.out.println("Error sending enrolment/de-enrolment notification: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
//        try {
//            sendEmail("lucafuertes@gmail.com", "testing", "");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
        try {
            sendNewBookingNotification(22);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }

}
