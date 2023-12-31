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
    private List<Account> accounts;

    @XmlElement
    private List<Announcement> announcements;

    @XmlElement
    private List<String> tokens;

    public RootElementWrapper() {
        bookings = new ArrayList<>();
        accounts = new ArrayList<>();
        announcements = new ArrayList<>();
        tokens = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }

    public void addToken(String token) {
        tokens.add(token);
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }
}