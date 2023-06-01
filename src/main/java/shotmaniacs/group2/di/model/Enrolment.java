package shotmaniacs.group2.di.model;

public class Enrolment{
    private int id;
    private int bookingId;
    private int crewMemberId;
    private Role role;
    public Enrolment() {}


    public Enrolment(int id, int bookingId, int crewMemberId, Role role) {
        this.id = id;
        this.bookingId = bookingId;
        this.crewMemberId = crewMemberId;
        this.role = role;
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