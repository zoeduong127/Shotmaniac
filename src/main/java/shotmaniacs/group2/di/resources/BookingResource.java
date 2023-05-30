package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import jakarta.xml.bind.JAXBElement;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.dto.Assign_role;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.Role;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookingResource {
  @Context
    UriInfo uriInfo;
  @Context
    Request request;

    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

  int account_id;
  int booking_id;
    public BookingResource(UriInfo uriInfo, Request request, int account_id, int id){
        this.uriInfo = uriInfo;
        this.request = request;
        this.account_id = account_id;
        this.booking_id = id;
    }
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    /**
     * Get all details of specific booking
     */
    public Booking getBooking(){
      Booking booking = BookingDao.instance.get_a_booking(booking_id);
      if(booking == null) {
          throw new RuntimeException("Get: booking with " + booking + "not defined");
      }
      return booking;
    }

    /**
     * Assign a role to the booking
     * @param role
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putRole(JAXBElement<Assign_role> role ){
      return putandGetresponse(role.getValue());
    }
    private Response putandGetresponse(Assign_role role){
        Response res = null;
        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO enrolment VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,role.getBooking_id());
            preparedStatement.setInt(2,role.getCrew_member_id());
            preparedStatement.setString(3, String.valueOf(role.getRole()));
            int rowsInseted = preparedStatement.executeUpdate();
            if(rowsInseted > 0){
                res= Response.created(uriInfo.getAbsolutePath()).build();
            }else{
                res = Response.serverError().build();
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return res;
    }

}
