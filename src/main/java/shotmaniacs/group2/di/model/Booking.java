package shotmaniacs.group2.di.model;

import java.util.Date;

public class Booking {
    private int id;
    private String name;
    private EventType eventType;
    private Date date;
    private String location;
    private int duration;
    private int clientId;
    private String clientEmail;
    private BookingType bookingType;

    public Booking(int id, String name, EventType eventType, Date date, String location, int duration, int clientId, String clientEmail, BookingType bookingType) {
        this.id = id;
        this.name = name;
        this.eventType = eventType;
        this.date = date;
        this.location = location;
        this.duration = duration;
        this.clientId = clientId;
        this.clientEmail = clientEmail;
        this.bookingType = bookingType;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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
}
