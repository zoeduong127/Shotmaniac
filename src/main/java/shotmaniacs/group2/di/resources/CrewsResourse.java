package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
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


    @RolesAllowed({"Administrator","Crew"})
    @Path("/mybookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getBooking(@PathParam("crewid") int crewId) {
        List<Booking> listBooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewId);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13));
                listBooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listBooking;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/mybooking/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public List<Booking> searchEnrolledBookings(@PathParam("crewid") int crewid, @QueryParam("searchtext") String searchText) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND to_tsvector(b.title) @@ phraseto_tsquery(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            preparedStatement.setString(2,searchText);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/mybooking/enrolled")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public List<Booking> getEnrolledBookings(@PathParam("crewid") int crewid) {
        List<Booking> listbooking = new ArrayList<>();
        try {

            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/mybooking/timefilter/{filtertime}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter my booking with timestamp
     * Param "ongoing": filter ongoing event
     * Param "past":filter past event
     */
    public List<Booking> getBookingWithTimeFilter(@PathParam("crewid") int crewid, @PathParam("filtertime") String ongoing) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            if(ongoing.equals("ongoing")) {
                query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND b.date_and_time >= NOW()";
            } else {
                query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND b.date_and_time < NOW()";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/mybooking/labelfilter/{label}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter my booking based on label:
     *
     */
    public List<Booking> getBookingWithLabelFilter(@PathParam("crewid") int crewid, @PathParam("label") String label) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query;
            if(label.equals("TODO")) {
                query = "SELECT b.* FROM booking b, enrolment e, label l WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND l.booking_id = b.booking_id AND l.label = 'TODO'";
            } else if (label.equals("IN_PROGRESS")) {
                query = "SELECT b.* FROM booking b, enrolment e, label l WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND l.booking_id = b.booking_id AND l.label = 'IN PROGRESS'";
            } else if (label.equals("REVIEW")) {
                query = "SELECT b.* FROM booking b, enrolment e, label l WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND l.booking_id = b.booking_id AND l.label = 'REVIEW'";
            } else { //label.equals("done")
                query = "SELECT b.* FROM booking b, enrolment e, label l WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND l.booking_id = b.booking_id AND l.label = 'DONE'";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/allbookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Get all bookings from Bookings site
     */
    public List<Booking> getAllBookings() {
        List<Booking> listbooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT b.* FROM booking b";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/booking/{booking_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * Modify specific event with given ID
     */
    public BookingResource modifySpecificBooking(@PathParam("crewid") int crewid, @PathParam("booking_id") int id) {
        return new BookingResource(uriInfo, request, crewid, id);
    }
    //TODO: U - assign a specific role to the booking

    @RolesAllowed({"Administrator","Crew"})
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
            String query = "SELECT * FROM announcement";
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

    @RolesAllowed({"Administrator","Crew"})
    @Path("/news/filter")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter based on read or unread announcements
     */
    public List<Announcement> getNewsWithFilter(@QueryParam("status") int status) {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query;
            if(status == 0) { //Unread announcement
                query = "SELECT a.* FROM announcement a, mark_announcement ma WHERE a.announcement_id = ma.announcement_id AND read_status = 0";
            } else {
                query = "SELECT a.* FROM announcement a, mark_announcement ma WHERE a.announcement_id = ma.announcement_id AND read_status = 1";
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

    @RolesAllowed({"Administrator","Crew"})
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
            String query = "SELECT a.* FROM announcement a, mark_announcement ma WHERE ma.booking_id = ? AND ma.announcement_id = a.announcement_id";
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

    @RolesAllowed({"Administrator","Crew"})
    @Path("/mybooking/{booking_id}/announcements/filter")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns all announcements of a specific booking with a status filter
     */
    public List<Announcement> getAnnouncementOfBookingWithFilter(@PathParam("booking_id") int bookingId, @QueryParam("status") int status) {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query;
            if(status == 0) { //Unread announcement
                query = "SELECT a.* FROM announcement a, mark_announcement ma WHERE ma.booking_id = ? AND a.announcement_id = ma.announcement_id AND read_status = 0";
            } else {
                query = "SELECT a.* FROM announcement a, mark_announcement ma WHERE ma.booking_id = ? AND a.announcement_id = ma.announcement_id AND read_status = 1";
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