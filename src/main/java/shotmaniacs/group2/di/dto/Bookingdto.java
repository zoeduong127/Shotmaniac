package shotmaniacs.group2.di.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.BookingState;
import shotmaniacs.group2.di.model.BookingType;
import shotmaniacs.group2.di.model.EventType;
import shotmaniacs.group2.di.resources.LoginResource;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Bookingdto {

    private String name;

    private String eventType;

    private String date;
    private String time;

    private String location;
    private String bookingType;

    private int duration;
    private String description;

    private String clientName;

    private String clientEmail;

    private String phoneNumber;

    private int slots;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";
    public Bookingdto(){
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
        setBookingType(bookingType.toUpperCase());
        setDate(date);
        setTime(time);
    }
//    public Booking createBooking(){
//        return new Booking(getName(),getDescription(),EventType.valueOf(eventType),getTimestamp(getDate()+" "+getTime()) ,
//                location,getDuration(),getClientName(),getClientEmail(),getPhoneNumber(),BookingType.valueOf(getBookingType()),
//                BookingState.PENDING,10);
//    }
    public boolean addBooking(Bookingdto booking) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO booking VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,booking.getName());
            preparedStatement.setString(2,booking.getDescription());
            preparedStatement.setString(3, String.valueOf(booking.getEventType()));
            preparedStatement.setTimestamp(4, getTimestamp(booking.getDate()+" "+booking.getTime()));
            preparedStatement.setString(5,booking.getLocation());
            preparedStatement.setInt(6,booking.getDuration());
            preparedStatement.setString(7,booking.getClientName());
            preparedStatement.setString(8,booking.getClientEmail());
            preparedStatement.setString(9,booking.getPhoneNumber());
            preparedStatement.setString(10, String.valueOf(booking.getBookingType()));
            preparedStatement.setString(11, String.valueOf(BookingState.PENDING));
            preparedStatement.setInt(12, 0);
            int rowsInserted = preparedStatement.executeUpdate();
            if(rowsInserted > 0) {
                System.out.println("Successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        System.out.println("Unsuccessfully");
        return false;
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
        this.eventType = eventType;
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
        this.bookingType = bookingType;
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
//        Bookingdto bookingdto = new Bookingdto( "Wedding Ceramony","Club Photography",
//              "2023-07-12","12:08","Amsterdam",
//                "Photography",2,"This is one of my most important event in my life",
//                "Duong Huyen","duonghuyen127@gmail.com","0687845896");
//        BookingDao.instance.addBooking(bookingdto.createBooking());
//    }

}