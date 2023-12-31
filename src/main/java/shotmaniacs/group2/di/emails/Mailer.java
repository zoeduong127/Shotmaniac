package shotmaniacs.group2.di.emails;

import jakarta.activation.MailcapCommandMap;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.dto.Bookingdto;
import shotmaniacs.group2.di.dto.EnrolmentDto;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.Announcement;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.Urgency;
import shotmaniacs.group2.di.security.ServletContextHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Random;

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
//        PATH = new File(System.getProperty("user.dir")) + "\\src\\main\\webapp\\email"; // TODO: Ensure this path is correct before deployment
        PATH = new File(ServletContextHolder.getServletContext().getRealPath("/email")).toString();
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

    public static void sendEmailWithInvite(String[] to, String subject, String HTMLContent, Booking booking, Account account) throws MessagingException {
        // register the text/calendar mime type
        MimetypesFileTypeMap mimetypes = 	(MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        // register the handling of text/calendar mime type
        MailcapCommandMap mailcap = (MailcapCommandMap) 	MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;;x-java-content-handler=com.sun.mail.handlers.text_plain");

        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(EMAIL));

        // Set To: header field of the header.
        InternetAddress[] addressTo = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            addressTo[i] = new InternetAddress(to[i]);
        }
        message.setRecipients(Message.RecipientType.TO, addressTo);

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

        // Add part two, the calendar
        BodyPart calendarPart = buildCalendarPart(new Date(booking.getDate().getTime()), new Date(booking.getDate().getTime()), booking, account);
        multipart.addBodyPart(calendarPart);

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

    public static void sendEnrolmentNotification(int bookingId, int crewMemberId) throws MessagingException {
        System.out.println("Email sent");
        Booking booking = BookingDao.instance.getABooking(bookingId);
        Account account = AccountDao.instance.getAccountById(crewMemberId);

        if (booking == null || account == null) {
            throw new MessagingException();
        }

        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "/enrolmentNotification.html"));

            doc.getElementById("title").html(" You have been enrolled into: <br> " + booking.getName());
            doc.getElementById("greeting").text("Hi, " + account.getUsername());
            doc.getElementById("description").text("Below you will find details about the enrolled booking.");
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + booking.getDescription());
            doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + booking.getClientName());
            doc.getElementById("booking_type").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Booking Type:</strong><br /> " + booking.getBookingType());
            doc.getElementById("event_type").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Event Type:</strong><br /> " + booking.getEventType());
            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + booking.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + booking.getLocation());

            sendEmailWithInvite(new String[]{account.getEmail()}, "New Enrolment", doc.html(), booking, account);
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }
    }

    public static void sendBookingCancellation(int booking_id, int account_id, String reason) throws MessagingException {
        Booking booking = BookingDao.instance.getABooking(booking_id);
        Account account = AccountDao.instance.getAccountById(account_id);
        if (booking == null || account == null) {
            throw new MessagingException();
        }

        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "/deenrolmentNotification.html"));

            doc.getElementById("title").html(" Cancelled Booking: <br> " + booking.getName());
            doc.getElementById("greeting").text("Hi, " + account.getUsername());
            doc.getElementById("description").html("A booking you were enrolled in was cancelled. The reason specified was: <br /> " + reason);
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + booking.getDescription());

            if (booking.getClientName() != null) {
                doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + booking.getClientName());
            } else {
                doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + "unknown");
            }

            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + booking.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + booking.getLocation());

            sendEmail(account.getEmail(), "Booking Was Cancelled", doc.html());
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }    }

    public static void sendBookingDeenrolment(int bookingId, int accountId) throws MessagingException {
        Booking booking = BookingDao.instance.getABooking(bookingId);
        Account account = AccountDao.instance.getAccountById(accountId);

        if (booking == null || account == null) {
            throw new MessagingException();
        }

        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "/deenrolmentNotification.html"));

            doc.getElementById("title").html(" You have been de-enrolled from: <br> " + booking.getName());
            doc.getElementById("greeting").text("Hi, " + account.getUsername());
            doc.getElementById("description").text("You have been de-enrolled from a booking. Below you will find more details: ");
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + booking.getDescription());
            if (booking.getClientName() != null) {
                doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + booking.getClientName());
            } else {
                doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + "unknown");
            }            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + booking.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + booking.getLocation());

            sendEmail(account.getEmail(), "Booking De-enrolment", doc.html());
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }
    }

    public static void sendNewBookingNotification(Bookingdto bookingdto) throws MessagingException {
        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "/newBookingNotification.html"));

            doc.getElementById("title").html(" New Booking Was Submitted: <br> " + bookingdto.getName());
            doc.getElementById("description").text("Below you will find details about the new booking.");
            doc.getElementById("booking_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + bookingdto.getDescription());

            if (bookingdto.getClientName() != null) {
                doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + bookingdto.getClientName());
            } else {
                doc.getElementById("client").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client:</strong><br /> " + "unknown");
            }

            doc.getElementById("booking_type").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Booking Type:</strong><br /> " + bookingdto.getBookingType());
            doc.getElementById("event_type").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Event Type:</strong><br /> " + bookingdto.getEventType());
            doc.getElementById("when").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">When:</strong><br /> " + bookingdto.getDate());
            doc.getElementById("where").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Where:</strong><br /> " + bookingdto.getLocation());

            if (bookingdto.getPhoneNumber() == null) {
                bookingdto.setPhoneNumber("unknown");

            }
            if (bookingdto.getClientEmail() == null) {
                bookingdto.setClientEmail("unknown");
            }
            doc.getElementById("client_contact").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Client contact: </strong><br /> Phone: " + bookingdto.getPhoneNumber() + "<br /> Email: " + bookingdto.getClientEmail());
            sendEmail(NEWBOOKINGEMAIL, "New Booking", doc.html());
        } catch (IOException e) {
            System.out.println("Error parsing email HTML file: " + e.getMessage());
        }
    }

    public static void sendNewAnnouncementNotification(Announcement announcement, String email) throws MessagingException {
        if (announcement == null) {
            throw new MessagingException();
        }
        try {
            Document doc = Jsoup.parse(loadHTMLFile(PATH + "/announcementNotification.html"));

            doc.getElementById("title").html(" New Announcement: <br> " + announcement.getTitle());
            doc.getElementById("announcement_description").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Details: </strong><br /> " + announcement.getBody());
            doc.getElementById("urgency").html("<strong style=\"font-size: 14px; color: #999; line-height: 18px\">Urgency: </strong><br /> " + announcement.getUrgency());
            sendEmail(email, "Announcement - " + announcement.getTitle(), doc.html());
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

    private static BodyPart buildCalendarPart(Date startDateTime, Date endDateTime, Booking booking, Account account) throws MessagingException {
        SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");
        BodyPart calendarPart = new MimeBodyPart();

        String calendarContent = "BEGIN:VCALENDAR\n" + "METHOD:REQUEST\n"
                + "PRODID: Shotmaniacs2\n" + "VERSION:2.0\n"
                + "BEGIN:VEVENT\n" + "DTSTAMP:"
                + iCalendarDateFormat.format(new Date(System.currentTimeMillis()))
                + "\n"
                + "DTSTART:"
                + iCalendarDateFormat.format(startDateTime)
                + "\n"
                + "DTEND:"
                + iCalendarDateFormat.format(endDateTime)
                + "\n"
                + "SUMMARY:"+ booking.getName() +"\n"
                + "UID:"+ generateString(32)+"\n"
                + "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE:MAILTO:"+ account.getEmail() +"\n"
                + "ORGANIZER:MAILTO:"+ EMAIL +"\n"
                + "LOCATION:"+booking.getLocation()+"\n"
                + "DESCRIPTION:"+booking.getDescription()+"\n"
                + "SEQUENCE:0\n"
                + "PRIORITY:5\n"
                + "CLASS:PUBLIC\n"
                + "STATUS:CONFIRMED\n"
                + "TRANSP:OPAQUE\n"
                + "BEGIN:VALARM\n"
                + "ACTION:DISPLAY\n"
                + "DESCRIPTION:REMINDER\n"
                + "TRIGGER;RELATED=START:-PT00H15M00S\n"
                + "END:VALARM\n"
                + "END:VEVENT\n" + "END:VCALENDAR";
        calendarPart.addHeader("Content-Class","urn:content-classes:calendarmessage");
        calendarPart.setContent(calendarContent, "text/calendar;method=CANCEL");
        return calendarPart;
    }

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        try {
            sendNewAnnouncementNotification(new Announcement(-1, "Announcement test",
                    "Announcement test body goes here.", 23,
                    Urgency.MINOR, new Timestamp(System.currentTimeMillis())), "lucafuertes@gmail.com");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
