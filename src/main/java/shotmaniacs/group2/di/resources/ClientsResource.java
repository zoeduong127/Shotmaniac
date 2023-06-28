package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dto.Accountdto;
import shotmaniacs.group2.di.dto.Bookingdto;
import shotmaniacs.group2.di.dto.Changepass;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/client")
public class ClientsResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";


    /* For client who not has a specific account*/
    @Path("/new_account")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(Accountdto accountdto) {
        AdministratorsResource admin = new AdministratorsResource();
        return admin.addAccount(new Account(-1,accountdto.getUsername(),accountdto.getEmail(),accountdto.getPassword(),AccountType.Client,null,accountdto.gettel(),null));
    }

    /**
     * Create single booking once time
     * @param booking
     * @return
     */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Response createBooking_noid(Bookingdto booking) {
       boolean response = booking.addBooking(booking);
       if(response){
            return Response.ok().build();
       }
        return Response.serverError().build();
    }

    /**
     * Create a list of booking simultaneously
     *
     * simultaneously
     * @param booking
     * @return
     */
    @Path("/list")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Response createBooking_noid(List<Bookingdto> booking) {
        int i = 0;
        for(Bookingdto bookingdto: booking) {
            boolean response = bookingdto.addBooking(bookingdto);
            if(response){
                i++;
            }
        }
        if(i == booking.size()){
            return Response.ok().build();
        }
        return Response.serverError().build();
    }




    /*For clienst who already has their own account*/
    @Path("{client_id}/profile")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Account getprofile(@PathParam("client_id") int id) {
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

    @POST
    @Path("{client_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Response createBooking_noid(@PathParam("client_id")int user_id,Bookingdto booking) {
        boolean response = booking.addBooking_id(user_id,booking);
        if(response){
            return Response.ok().build();
        }
        return Response.serverError().build();
    }

    /**
     * Create a list of booking simultaneously
     *
     * simultaneously
     * @param booking
     * @return
     */
    @Path("{client_id}/list")
    @POST

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Response createBooking_noid(@PathParam("client_id")int user_id,List<Bookingdto> booking) {
        int i = 0;
        for(Bookingdto bookingdto: booking) {
            boolean response = bookingdto.addBooking_id(user_id,bookingdto);
            if(response){
                i++;
            }
        }
        if(i == booking.size()){
            return Response.ok().build();
        }
        return Response.serverError().build();
    }
    /*Filter*/


    @Path("{clientid}/mybooking/timefilter/{filtertime}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter my booking with timestamp
     * Param "ongoing": filter ongoing event
     * Param "past":filter past event
     */
    public List<Booking> getBookingWithTimeFilter(@PathParam("clientid") int id, @PathParam("filtertime") String ongoing) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            if(ongoing.equals("ongoing")) {
                query = "SELECT b.* FROM booking b , client e WHERE e.client_id = ? AND e.booking_id = b.booking_id AND b.date_and_time >= NOW()";
            } else {
                query = "SELECT b.* FROM booking b , client e WHERE e.client_id = ? AND e.booking_id = b.booking_id AND b.date_and_time < NOW()";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13),-1);
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }
    @Path("{clientid}/mybooking/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public List<Booking> searchEnrolledBookings(@PathParam("clientid") int id, @QueryParam("searchtext") String searchText) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            query = "SELECT b.* FROM booking b , client e WHERE e.client_id = ? AND e.booking_id = b.booking_id AND to_tsvector(b.title) @@ phraseto_tsquery(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            preparedStatement.setString(2,searchText);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13),-1);
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }


    @Path("{clientid}/mybookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getBooking(@PathParam("clientid") int id) {
        List<Booking> listBooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT b.* FROM booking b , client e WHERE e.client_id = ? AND e.booking_id = b.booking_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13),-1);
                listBooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listBooking;
    }

    /**
     * Get specific booking'details
     * @param id
     * @return
     */
    @Path("{booking_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Booking getaBooking(@PathParam("booking_id") int id){
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM booking WHERE booking_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13),-1);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return null;
    }
    /*get the list of admins following booking_id*/
    @Path("{booking_id}/admins")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAdmins(@PathParam("booking_id") int id) {
        List<Account> listadmins = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT a.account_id, a.username,a.email, a.tel FROM account a, enrolment e WHERE e.admin_id = ? AND a.account_id = e.admin_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Account account = new Account(rs.getInt(1), rs.getString(2), rs.getString(3),"",null,null,rs.getString(7),null);
                listadmins.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return listadmins;
    }
        /*get the list of admins following booking_id*/
    @Path("{booking_id}/crews")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getCrews(@PathParam("booking_id") int id) {
        List<Account> listcrews = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT a.account_id, a.username,a.email, a.tel FROM account a, enrolment e WHERE e.admin_id = ? AND a.account_id = e.admin_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Account account = new Account(rs.getInt(1), rs.getString(2), rs.getString(3),"",null,null,rs.getString(7),null);
                listcrews.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listcrews;
    }
    /*Delete specific booking*/
    @Path("{booking_id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBooking(@PathParam("booking_id") int id) {
        List<Account> listcrews = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "UPDATE booking SET state = ? WHERE booking_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,String.valueOf(BookingState.CANCELED));
            preparedStatement.setInt(2,id);
            int rowsInserted = preparedStatement.executeUpdate();
            if(rowsInserted > 0){
                return Response.ok().build();
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return Response.serverError().build();
    }


    @Path("{client_id}/change-username")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeusername(@PathParam("client_id") int id, @QueryParam("new") String newname) {
        try {
            if(nameExists(newname)){
                return  Response.status(422)
                        .entity("Username is already in use.")
                        .build();
            }
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "UPDATE account SET username = ? WHERE account_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,newname);
            preparedStatement.setInt(2,id);
            int rowsInserted = preparedStatement.executeUpdate();
            if(rowsInserted > 0){
                return Response.ok().build();
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return Response.serverError().build();
    }

    public boolean nameExists(String name) {
        String sql = "SELECT username FROM account WHERE username = ? ";
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Path("{client_id}/change-username")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response changepassword(@PathParam("client_id") int id, Changepass pass) {
        if(checkpass(id,pass.getOldpass())) {
            try {
                AdministratorsResource admin = new AdministratorsResource();
                String salt = admin.generateSalt();
                Connection connection = DriverManager.getConnection(url, dbName, password);
                String query = "UPDATE account SET password = ? AND salt = ? WHERE account_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, admin.hash256(pass.getNewpass() + salt));
                preparedStatement.setString(2, salt);
                preparedStatement.setInt(3, id);
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    return Response.ok().build();
                }
            } catch (SQLException e) {
                System.err.println("Error connecting: " + e);
            }
        }
        return Response.serverError().build();
    }
    public String getSaltById(int id) {
        String result = null;
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT * FROM account WHERE account.account_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = rs.getString(6);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return result;
    }
    public boolean checkpass(int id, String pass) {
        String salt = getSaltById(id);
        if (salt == null) {
            return false;
        }
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM account WHERE account_id = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, pass);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;

    }
}