package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dto.Assign_role;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.BookingState;
import shotmaniacs.group2.di.model.BookingType;
import shotmaniacs.group2.di.model.EventType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/crew/{crewid}")

public class crewsresourse {

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
    /**
     * Filter mybooking
     */
    public List<Booking> getBooking(@PathParam("crewid") int crewid ){
        List<Booking> listbooking = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT booking.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id ";
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
    @Path("/mybooking/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter my booking with timestamp
     * Param "ongoing": filter ongoing event
     * Param "past":filter past event
     */
    public List<Booking> getBooking(@PathParam("crewid") int crewid, @QueryParam("filtertime") String ongoing ){
        List<Booking> listbooking = new ArrayList<>();
        try{
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            if(ongoing.equals("ongoing")) {
                query = "SELECT booking.* FROM booking b , enrolment e WHERE e.crew_member_id = ? AND e.booking_id = b.booking_id AND b.date_and_time >= GETDATE()";
            }else{
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


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Get all booking
     */
    public List<Booking> getBooking(){
        List<Booking> listbooking = new ArrayList<>();
        try{
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
    public BookingResource assign_rolebycrew(@PathParam("crewid") int crewid, @PathParam("booking_id") int id) {
        return new BookingResource(uriInfo, request, crewid, id);
    }

}
