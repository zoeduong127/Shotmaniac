package shotmaniacs.group2.di.dto;

import jakarta.mail.MessagingException;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.emails.Mailer;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.BookingState;
import shotmaniacs.group2.di.model.BookingType;
import shotmaniacs.group2.di.model.EventType;
import shotmaniacs.group2.di.resources.LoginResource;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@XmlRootElement
public class Bookingdto {
    @XmlAnyElement
    private int user_id;
    @XmlAnyElement
    private String name;
    @XmlAnyElement
    private String eventType;
    @XmlAnyElement
    private String date;
    @XmlAnyElement
    private String time;
    @XmlAnyElement
    private String location;
    @XmlAnyElement
    private String bookingType;
    @XmlAnyElement
    private int duration;
    @XmlAnyElement
    private String description;
    @XmlAnyElement
    private String clientName;
    @XmlAnyElement
    private String clientEmail;
    @XmlAnyElement
    private String phoneNumber;
    @XmlAnyElement
    private int slots;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";
    public Bookingdto() {
    }
    public Bookingdto(String name,String eventType, String date, String time,
                      String location,String bookingType, int duration, String description, String clientName, String clientEmail, String phoneNumber) {
        setName(name);
        setDescription(description);
        setEventType(eventType.replace(" ","_").toUpperCase());
        setLocation(location);
        setDuration(duration);
        setClientName(clientName);
        setClientEmail(clientEmail);
        setPhoneNumber(phoneNumber);
        setBookingType(bookingType.replace(" ","_").toUpperCase());
        setDate(date);
        setTime(time);
    }

    public Bookingdto(int user_id,String name,String eventType, String date, String time,
                      String location,String bookingType, int duration, String description) {
        setUser_id(user_id);
        setName(name);
        setDescription(description);
        setEventType(eventType.replace(" ","_").toUpperCase());
        setLocation(location);
        setDuration(duration);
        setBookingType(bookingType.toUpperCase());
        setDate(date);
        setTime(time);
    }

    public Bookingdto(int user_id, String name, String eventType, String date, String time, String location, String bookingType, int duration, String description, String clientName, String clientEmail, String phoneNumber, int slots) {
        this.user_id = user_id;
        this.name = name;
        this.eventType = eventType.replace(" ", " ").toUpperCase();
        this.date = date;
        this.time = time;
        this.location = location;
        this.bookingType = bookingType;
        this.duration = duration;
        this.description = description;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.phoneNumber = phoneNumber;
        this.slots = slots;
    }

    public int getUser_id(){
        return this.user_id;
    }
    public void setUser_id(int id){
        user_id = id;
    }
    public boolean addBooking(Bookingdto booking) {
        try {
            if(booking.checkexists(booking) != -1){
                return false;
            }
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO booking VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,booking.getName());
            preparedStatement.setString(2,booking.getDescription());
            preparedStatement.setString(3, String.valueOf(booking.getEventType().toUpperCase()));
            preparedStatement.setTimestamp(4, getTimestamp(booking.getDate()+" "+booking.getTime()));
            preparedStatement.setString(5,booking.getLocation());
            preparedStatement.setInt(6,booking.getDuration());
            preparedStatement.setString(7,booking.getClientName());
            preparedStatement.setString(8,booking.getClientEmail());
            preparedStatement.setString(9,booking.getPhoneNumber());
            preparedStatement.setString(10, String.valueOf(booking.getBookingType().toUpperCase()));
            preparedStatement.setString(11, String.valueOf(BookingState.PENDING));
            int rowsInserted = preparedStatement.executeUpdate();
            if(rowsInserted > 0) {
                try {
                    Mailer.sendNewBookingNotification(booking);
                } catch (MessagingException e) {
                    System.out.println("Error sending new booking notification: " + e.getMessage());
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        System.out.println("Unsuccessfully");
        return false;
    }

    public boolean addBooking_id(int user_id, Bookingdto booking) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,user_id);
            ResultSet rs = preparedStatement.executeQuery();
            /* Insert a new booking*/
            if(rs.next()) {
                Bookingdto bookingdto = new Bookingdto(booking.getName(),booking.getEventType().toUpperCase(),booking.getDate(),booking.getTime(),booking.getLocation(),booking.getBookingType().toUpperCase(),booking.getDuration(),booking.getDescription(),rs.getString("username"), rs.getString("email"),rs.getString("tel"));
                /*get the id of new Booking*/
                if (addBooking(bookingdto)) {
                    int booking_id = bookingdto.checkexists(bookingdto);
                    if(booking_id != -1 ) {
                        String query3 = "INSERT INTO client VALUES (?,?)";
                        PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                        preparedStatement3.setInt(1, booking_id);
                        preparedStatement3.setInt(2,user_id);
                        int rowsInserted1 = preparedStatement3.executeUpdate();
                        if(rowsInserted1 == 1){
                            System.out.println("Successfully");
                            return true;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        System.out.println("Unsuccessfully");
        return false;
    }
    public int checkexists(Bookingdto booking){
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM booking WHERE title = ? AND description = ? AND event_type = ?  AND date_and_time =? AND location =? AND duration_hours = ? AND client_name = ? AND client_email =? AND booking_type = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, booking.getName());
            preparedStatement.setString(2, booking.getDescription());
            preparedStatement.setString(3, String.valueOf(booking.getEventType().toUpperCase()));
            preparedStatement.setTimestamp(4, getTimestamp(booking.getDate() + " " + booking.getTime()));
            preparedStatement.setString(5, booking.getLocation());
            preparedStatement.setInt(6, booking.getDuration());
            preparedStatement.setString(7, booking.getClientName());
            preparedStatement.setString(8, booking.getClientEmail());
            preparedStatement.setString(9, String.valueOf(booking.getBookingType().toUpperCase()));
            ResultSet rs= preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("booking_id");
            }
        }catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return -1;

    }

    public Timestamp getTimestamp(String time){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date parsedDate = dateFormat.parse(time);
            Timestamp sqlTimestamp = new Timestamp(parsedDate.getTime());
            return sqlTimestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return this.time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType.replace(" ", "_").toUpperCase();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType.toUpperCase();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }
//    public static void main (String args[]) throws ParseException {
//        Bookingdto bookingdto = new Bookingdto( "HELLO","Club Photography",
//              "2023-07-12","12:08","Amsterdam",
//                "Photography",2,"This is one of my most important event in my life",
//                "Duong Huyen","duonghuyen127@gmail.com","0687845896");
//        System.out.println(bookingdto.addBooking_id(11,bookingdto));
//    }

}
