package shotmaniacs.group2.di.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.security.RolesAllowed;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AnnouncementDao;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.emails.Mailer;
import shotmaniacs.group2.di.model.*;

import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @RolesAllowed({"Administrator"})
    @Path("/createnews")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public Response createNew(Announcement announcement) {
        if(AnnouncementDao.instance.addAnnouncement(announcement) == 1){
            return Response.accepted().build();
        }
        return Response.notModified().build();
    }
    @RolesAllowed({"Administrator","Crew"})
    @Path("/mybookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getBookingById(@PathParam("crewid") int crewId) {
        List<Booking> listBooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewId);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4).toUpperCase()),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11).toUpperCase()), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
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
            query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND to_tsvector(b.title) @@ plainto_tsquery(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            preparedStatement.setString(2,searchText);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4).toUpperCase()),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11).toUpperCase()), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
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
                        EventType.valueOf(rs.getString(4).toUpperCase()),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11).toUpperCase()), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
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
    public List<Booking> getBookingWithTimeFilter(@PathParam("crewid") int crewid, @PathParam("filtertime") String filter) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            if(filter.equals("ongoing")) {
                query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND b.date_and_time >= NOW()";
            } else {
                query = "SELECT b.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND b.date_and_time < NOW()";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4).toUpperCase()),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11).toUpperCase()), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
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
            String query = switch (label) {
                case "TODO" ->
                        "SELECT b.* FROM booking b, enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND e.label = 'TODO'";
                case "IN_PROGRESS" ->
                        "SELECT b.* FROM booking b, enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND e.label = 'IN PROGRESS'";
                case "REVIEW" ->
                        "SELECT b.* FROM booking b, enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND e.label = 'REVIEW'";
                default ->  //label.equals("done")
                        "SELECT b.* FROM booking b, enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND e.label = 'DONE'";
            };
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,crewid);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4).toUpperCase()),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11).toUpperCase()), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
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
                        EventType.valueOf(rs.getString(4).toUpperCase()),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11).toUpperCase()), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/on-goingbookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Get all bookings from Bookings site
     */
    public List<Booking> getFutureBookings() {
        List<Booking> listbooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT b.* FROM booking b WHERE b.date_and_time >= NOW() ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4).toUpperCase()),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11).toUpperCase()), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
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

    @RolesAllowed({"Administrator","Crew"})
    @Path("/news")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Get all announcements from News site
     */
    public List<Announcement> getAllAnnouncements(@PathParam("crewid") int id) {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM announcement";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Announcement announcement = new Announcement(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getInt(4), Urgency.valueOf(resultSet.getString(5)), resultSet.getTimestamp(6), AnnouncementDao.instance.getState(resultSet.getInt(1),id));
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
    public List<Announcement> getNewsWithFilter(@QueryParam("status") int status ,@PathParam("crewid") int id) {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query;
            if(status == 0) { //Unread announcement
                query = "SELECT DISTINCT a.* FROM announcement a, mark_announcement ma WHERE a.announcement_id = ma.announcement_id AND ma.read_status = 'UNREAD' AND employee_id ="+id;
            } else {
                query = "SELECT DISTINCT a.* FROM announcement a, mark_announcement ma WHERE a.announcement_id = ma.announcement_id AND ma.read_status = 'READ' AND employee_id ="+id;
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
    /*Mark read_news*/

    @RolesAllowed({"Administrator", "Crew"})
    @Path("/news/{new_id}/mark_read")
    @PUT
    public Response setReadnew(@PathParam("new_id") int newId,@PathParam("crewid") int crew_id) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "UPDATE mark_announcement SET read_status = ? WHERE announcement_id = ? AND employee_id = ? ";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, String.valueOf(AnnouncementState.READ));
            ps.setInt(2, newId);
            ps.setInt(3, crew_id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.out.println("Error while setting booking product manager: " + e.getMessage());
        }
        return Response.serverError().build();
    }
    @RolesAllowed({"Administrator", "Crew"})
    @Path("/mybooking/statistics/hoursworked")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    /**
     * Gets the sum of the hours per booking aggregated by day. Gets all the days with bookings between startDate and endDate.
     * NOTE: Format for input dates is yyyy-MM-dd
     */
    public Response getHoursWorkedPerDay(@PathParam("crewid") int id) {
        List<Booking> bookings = getBookingById(id);
        LocalDateTime currentTime = LocalDateTime.now();
        int currentMonth = currentTime.getMonthValue();
        int currentYear = currentTime.getYear();

        // Filter bookings for the current month
        List<Booking> bookingsThisMonth = new ArrayList<>();
        for (Booking booking : bookings) {
            LocalDateTime bookingDateTime = booking.getDate().toLocalDateTime();
            int bookingMonth = bookingDateTime.getMonthValue();
            int bookingYear = bookingDateTime.getYear();

            if (bookingMonth == currentMonth && bookingYear == currentYear) {
                bookingsThisMonth.add(booking);
            }
        }
        int count = 0;
        // Print the filtered bookings
        for (Booking booking : bookingsThisMonth) {
           count = count + booking.getDuration();
        }

//        try {
//            Connection connection = DriverManager.getConnection(url, dbName, password);
//            String sql = "SELECT DATE_TRUNC('day', b.date_and_time), SUM(b.duration_hours) \n" +
//                    "FROM booking b, enrolment e \n" +
//                    "WHERE e.crew_member_id = ? AND b.booking_id = e.booking_id \n" +
//                    "AND DATE_TRUNC('day', b.date_and_time) >= ?::date\n" +
//                    "AND DATE_TRUNC('day', b.date_and_time) <= ?::date\n" +
//                    "GROUP BY DATE_TRUNC('day', b.date_and_time);";
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.setInt(1, crewid);
//            ps.setString(2,  startDate.toString());
//            ps.setString(3,  endDate.toString());
//
//            ResultSet rs = ps.executeQuery();
//
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            ObjectNode jsonObject = objectMapper.createObjectNode();
//            while (rs.next()) {
//                jsonObject.put(rs.getString(1), rs.getInt(2));
//            }
//
//            return Response.ok(jsonObject.toString()).build();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return Response.ok()
                .entity(String.valueOf(count))
                .build();
    }
    @RolesAllowed({"Administrator", "Crew"})
    @Path("/profile")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Account getprofile(@PathParam("crewid") int id) {
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            query = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return new Account(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4),AccountType.valueOf(rs.getString(5)),null,rs.getString(7),null);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public static void main (String args[]) throws ParseException {
        CrewsResourse crew = new CrewsResourse();
         System.out.println(crew.setReadnew(42,231));
    }
}