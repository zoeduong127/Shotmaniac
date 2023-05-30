package shotmaniacs.group2.di.dto;

import shotmaniacs.group2.di.model.Role;

public class Assign_role {
    private int booking_id;
    private int crew_member_id;
    private Role role;
    public Assign_role(int booking_id, int crew_member_id,Role role){
        this.booking_id = booking_id;
        this.role = role;
        this.crew_member_id = crew_member_id;
    }
    public void setCrew_member_id(int id){
        this.crew_member_id = id;
    }
    public int getCrew_member_id(){
        return crew_member_id;
    }
    public void setBooking_id(int Booking_id){
        this.booking_id = Booking_id;
    }
    public int getBooking_id(){return this.booking_id;}
    public void setRole(Role role){
        this.role = role;
    }
    public Role getRole(){return this.role;}
}
