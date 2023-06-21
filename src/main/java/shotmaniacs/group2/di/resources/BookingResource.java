package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import jakarta.xml.bind.JAXBElement;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.dto.AssignRole;
import shotmaniacs.group2.di.model.Booking;

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

  int accountId;
  int bookingId;

    public BookingResource(UriInfo uriInfo, Request request, int accountId, int id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.accountId = accountId;
        this.bookingId = id;
    }

    @RolesAllowed({"Administrator","Crew"})
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    /**
     * Get all details of specific booking
     */
    public Booking getBooking() {
      Booking booking = BookingDao.instance.getABooking(bookingId);
      if(booking == null) {
          throw new RuntimeException("Get: booking with " + bookingId + "is not defined");
      }
      return booking;
    }


    /**
     * Assign a role to the booking
     * @param role
     * @return
     */
    @RolesAllowed({"Administrator","Crew"})
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putRole(JAXBElement<AssignRole> role){
      return putAndGetResponse(role.getValue());
    }

    private Response putAndGetResponse(AssignRole role) {
        Response res = null;
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO enrolment VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,role.getBookingId());
            preparedStatement.setInt(2,role.getCrewMemberId());
            preparedStatement.setString(3, String.valueOf(role.getRole()));
            int rowsInseted = preparedStatement.executeUpdate();
            if(rowsInseted > 0) {
                res = Response.created(uriInfo.getAbsolutePath()).build();
            } else {
                res = Response.serverError().build();
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return res;
    }
}