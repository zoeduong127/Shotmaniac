package shotmaniacs.group2.di.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class RootElementWrapper {
    @XmlElement
    private List<Booking> bookings;

    @XmlElement
    private List<CrewMember> crewMembers;

    @XmlElement
    private List<Announcement> announcements;

    public RootElementWrapper() {
        bookings = new ArrayList<>();
        crewMembers = new ArrayList<>();
        announcements = new ArrayList<>();
    }

    // TODO: More constructors, getters, setters

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void addCrewMember(CrewMember crewMember) {
        crewMembers.add(crewMember);
    }

    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }
}
