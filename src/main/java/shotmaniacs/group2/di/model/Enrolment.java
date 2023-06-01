package shotmaniacs.group2.di.model;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Enrolment{
    @XmlAnyElement
    private int id;
    @XmlAnyElement
    private int bookingId;
    @XmlAnyElement
    private int crewMemberId;
    @XmlAnyElement
    private Role role;


    public Enrolment(int id, int bookingId, int crewMemberId, Role role) {
        this.id = id;
        this.bookingId = bookingId;
        this.crewMemberId = crewMemberId;
        this.role = role;
    }

    public Enrolment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCrewMemberId() {
        return crewMemberId;
    }

    public void setCrewMemberId(int crewMemberId) {
        this.crewMemberId = crewMemberId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}