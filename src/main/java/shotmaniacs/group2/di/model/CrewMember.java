package shotmaniacs.group2.di.model;

import java.util.ArrayList;
import java.util.HashMap;

public class CrewMember {
    private int id;
    private UserCredentials credentials;
    private ArrayList<Enrolment> enrolledBookings;

    public CrewMember(int id, UserCredentials credentials, ArrayList<Enrolment> enrolledBookings) {
        this.id = id;
        this.credentials = credentials;
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

    public UserCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(UserCredentials credentials) {
        this.credentials = credentials;
    }

    public ArrayList<Enrolment> getEnrolledBookings() {
        return enrolledBookings;
    }

    public void setEnrolledBookings(ArrayList<Enrolment> enrolledBookings) {
        this.enrolledBookings = enrolledBookings;
    }

}
