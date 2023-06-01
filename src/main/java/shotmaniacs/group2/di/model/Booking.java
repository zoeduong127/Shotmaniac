package shotmaniacs.group2.di.model;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Booking {
    @XmlAnyElement
    private int id;
    @XmlAnyElement
    private String name;
    @XmlAnyElement
    private String description;
    @XmlAnyElement
    private EventType eventType;
    @XmlAnyElement
    private java.sql.Timestamp date;
    @XmlAnyElement
    private String location;
    @XmlAnyElement
    private int duration;
    @XmlAnyElement
    private String clientEmail;
    @XmlAnyElement
    private String clientName;
    @XmlAnyElement
    private String phoneNumber;
    @XmlAnyElement
    private BookingType bookingType;
    @XmlAnyElement
    private BookingState state;

    //todo: add enrolments


    public Booking(){}

    public Booking(int id, String name, String description, EventType eventType, java.sql.Timestamp date,
                   String location, int duration, String clientEmail, String clientName, String phoneNumber,
                   BookingType bookingType, BookingState state) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public java.sql.Timestamp getDate() {
        return date;
    }

    public void setDate(java.sql.Timestamp date) {
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

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }
}