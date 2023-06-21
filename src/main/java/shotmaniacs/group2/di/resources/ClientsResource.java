package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.dto.Bookingdto;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.*;

import java.sql.*;
import java.text.ParseException;
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
//    @Path("/account")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
//    public Response createAccount(Account booking) {
//        boolean response = booking.addBooking(booking);
//        if(response){
//            return Response.ok().build();
//        }
//        return Response.serverError().build();
//    }
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

//    @Path("/client/{clientid}")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Booking> getAllBookings(@PathParam("clientid")int id) {
//        List<Booking> listbooking = new ArrayList<>();
//        try {
//            Connection connection = DriverManager.getConnection(url, dbName, password);
//            String query = "SELECT b.* FROM booking b , client c WHERE c.client_id = ? AND c.booking_id = b.booking_id ";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1,id);
//            ResultSet rs = preparedStatement.executeQuery();
//
//            while(rs.next()) {
//                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
//                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
//                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13));
//                listbooking.add(booking);
//            }
//        } catch (SQLException e) {
//            System.err.println("Error connecting: "+e);
//        }
//        return listbooking;
//    }
//
//    @Path("/client/{clientid}")
//    @POST
//    @Produces(MediaType.TEXT_HTML)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void createBooking(@PathParam("clientid")int id ,Booking booking) {
//        booking.setState(BookingState.PENDING);
//        BookingDao.instance.addBooking(booking);
//
//        try {
//            Connection connection = DriverManager.getConnection(url, dbName, password);
//            String query = "INSERT INTO client VALUES (?,?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1,booking.getId());
//            preparedStatement.setInt(2,id);
//            int rowsInseted = preparedStatement.executeUpdate();
//            while(rowsInseted > 0) {
//                System.out.println("Successfully");
//                System.out.println(booking);
//                return;
//            }
//        } catch (SQLException e) {
//            System.err.println("Error connecting: "+e);
//        }
//        System.out.println("Unsuccessfully");
//    }
//    @Path("{clientid}/delete/{booking_id}")
//    @Produces(MediaType.TEXT_HTML)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public BookingResource cancelabooking(@PathParam("clientid") int clientid,@PathParam("booking_id") int id) {
//        return new BookingResource(uriInfo, request, clientid, id);
//    }
//
    public static void main (String args[]) throws ParseException {
        Bookingdto bookingdto = new Bookingdto( "Wedding Ceramony",
        "Club Photography","2023-07-12","12:08",
                "Amsterdam","Photography",2,"This is one of my most important event in my life","Duong Huyen","duonghuyen127@gmail.com","0687845896");
        ClientsResource client = new ClientsResource();
        client.createBooking_noid(bookingdto);
    }

}