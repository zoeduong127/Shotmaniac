package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/crew/{crewid}")

public class CrewsResourse {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    @Path("/mybooking")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getBooking(@PathParam("crewid") int crewId) {
        List<Booking> listBooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT booking.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewId);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)));
                listBooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listBooking;
    }

    @Path("/mybooking?filtertime=<On going/Past>")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter my booking with timestamp
     * Param "ongoing": filter ongoing event
     * Param "past":filter past event
     */
    public List<Booking> getBookingWithFilter(@PathParam("crewid") int crewid, @QueryParam("filtertime") String ongoing) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            if(ongoing.equals("On going")) {
                query = "SELECT booking.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND b.date_and_time >= GETDATE()";
            } else {
                query = "SELECT booking.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND b.date_and_time < GETDATE()";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @Path("/booking")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Get all bookings from Bookings site
     */
    public List<Booking> getAllBookings() {
        List<Booking> listbooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT booking.* FROM booking b";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @Path("/booking/{booking_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * Modify specific event with given ID
     */
    public BookingResource assignedRoleByCrew(@PathParam("crewid") int crewid, @PathParam("booking_id") int id) {
        return new BookingResource(uriInfo, request, crewid, id);
    }
    //TODO: U - assign a specific role to the booking

    @Path("/news")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Get all announcements from News site
     */
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT announcement.* FROM announcement";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Announcement announcement = new Announcement(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getInt(4), Urgency.valueOf(resultSet.getString(5)), resultSet.getTimestamp(6));
                announcementList.add(announcement);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return announcementList;
    }

    @Path("/news?filter=<Read/Unread>")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter based on read or unread announcements
     */
    public List<Announcement> getNewsWithFilter(@QueryParam("filter") int status) {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query;
            if(status == 0) { //Unread announcement
                query = "SELECT announcement.* FROM announcement a, mark_announcement ma WHERE a.announcement_id = ma.announcement_id AND read_status = 0";
            } else {
                query = "SELECT announcement.* FROM announcement a, mark_announcement ma WHERE a.announcement_id = ma.announcement_id AND read_status = 1";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Announcement announcement = new Announcement(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getInt(4), Urgency.valueOf(resultSet.getString(5)), resultSet.getTimestamp(6));
                announcementList.add(announcement);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return announcementList;
    }

    @Path("/mybooking/{booking_id}/announcements")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns all announcements of a specific booking
     */
    public List<Announcement> getAnnouncementOfBooking(@PathParam("booking_id") int bookingId) {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT announcement.* FROM announcement a, mark_announcement ma WHERE ma.booking_id = ? AND ma.announcement_id = a.announcement_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Announcement announcement = new Announcement(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getInt(4), Urgency.valueOf(resultSet.getString(5)), resultSet.getTimestamp(6));
                announcementList.add(announcement);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return announcementList;
    }

    @Path("/mybooking/{booking_id}/announcements?filter=<Read/Unread>")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns all announcements of a specific booking
     */
    public List<Announcement> getAnnouncementOfBookingWithFilter(@PathParam("booking_id") int bookingId, @QueryParam("filter") int status) {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query;
            if(status == 0) { //Unread announcement
                query = "SELECT announcement.* FROM announcement a, mark_announcement ma WHERE ma.booking_id = ? AND a.announcement_id = ma.announcement_id AND read_status = 0";
            } else {
                query = "SELECT announcement.* FROM announcement a, mark_announcement ma WHERE ma.booking_id = ? AND a.announcement_id = ma.announcement_id AND read_status = 1";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Announcement announcement = new Announcement(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getInt(4), Urgency.valueOf(resultSet.getString(5)), resultSet.getTimestamp(6));
                announcementList.add(announcement);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return announcementList;
    }
}