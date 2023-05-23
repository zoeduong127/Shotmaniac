package shotmaniacs.group2.di.model;

import java.util.ArrayList;
import java.util.HashMap;

public class CrewMember {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private ArrayList<Enrolment> enrolledBookings;

    public CrewMember(int id, String username, String email, String passwordHash, ArrayList<Enrolment> enrolledBookings) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enrolledBookings = enrolledBookings;
    }

    public CrewMember() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public ArrayList<Enrolment> getEnrolledBookings() {
        return enrolledBookings;
    }

    public void setEnrolledBookings(ArrayList<Enrolment> enrolledBookings) {
        this.enrolledBookings = enrolledBookings;
    }

}
