package shotmaniacs.group2.di.model;

import java.util.Date;

public class Booking {
    private int id;
    private String name;
    private String description;
    private EventType eventType;
    private java.sql.Date date;
    private String location;
    private int duration;
    private String clientEmail;
    private String clientName;
    private String phoneNumber;
    private BookingType bookingType;
    private BookingState state;

    //todo: add enrolments


    public Booking(int id, String name, String description, EventType eventType, java.sql.Date date,
                   String location, int duration, String clientEmail, String clientName, String phoneNumber,
                   BookingType bookingType, BookingState state) {
        this.id = id;
        this.name = name;
        this.eventType = eventType;
        this.date = date;
        this.location = location;
        this.duration = duration;
        this.clientEmail = clientEmail;
        this.clientName = clientName;
        this.phoneNumber = phoneNumber;
        this.bookingType = bookingType;
        this.state = state;
    }
    public String getDescription(){return description;};
    public void setDescription(String description){ this.description = description;};

    public String getClientname(){return clientName;}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
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

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public BookingState getState(){return state;}
    public void setState(BookingState state){this.state = state;}
}
