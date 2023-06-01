package shotmaniacs.group2.di.dto;

import shotmaniacs.group2.di.model.Role;

public class AssignRole {
    private int booking_id;
    private int crew_member_id;
    private Role role;
    public AssignRole(){}

    public AssignRole(int booking_id, int crew_member_id, Role role) {
        this.booking_id = booking_id;
        this.role = role;
        this.crew_member_id = crew_member_id;
    }

    public void setCrewMemberId(int id) {
        this.crew_member_id = id;
    }

    public int getCrewMemberId() {
        return crew_member_id;
    }

    public void setBookingId(int Booking_id) {
        this.booking_id = Booking_id;
    }

    public int getBookingId() {
        return this.booking_id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }
}