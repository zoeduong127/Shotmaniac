package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.BookingDao;
import shotmaniacs.group2.di.emails.Mailer;
import shotmaniacs.group2.di.model.AccountType;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.Role;

import java.sql.*;

import static java.lang.String.valueOf;

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
    @Produces({MediaType.APPLICATION_JSON})
    /**
     * Get all details of specific booking
     */
    public Booking getBooking() {
      Booking booking = BookingDao.instance.getABooking(bookingId);
      return booking;
    }

    @Path("/enrol")
    @RolesAllowed({"Administrator","Crew"})
    @PUT
    public Response putEnrolment(@QueryParam("role") String roleToPut){
        if (!roleIsValid(roleToPut)) {
            return Response.notModified().build();
        }
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO enrolment (booking_id,crew_member_id, role,label) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,bookingId);
            preparedStatement.setInt(2,accountId);
            preparedStatement.setString(3, roleToPut);
            preparedStatement.setString(4, "TODO");
            int rowsInserted = preparedStatement.executeUpdate();
            if(rowsInserted > 0) {
                Mailer.sendEnrolmentNotification(bookingId, accountId);
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.err.println("Error putting enrolment: "+e.getMessage());
        } catch (MessagingException e) {
            System.err.println("Error putting enrolment: "+e.getMessage());
            return Response.ok().entity("Enrolment was successful but email failed to send.").build();
        }

        return Response.serverError().build();
    }

    @RolesAllowed({"Administrator"})
    @Path("/enrolment")
    @DELETE
    public Response deleteEnrolmentByBookingIdAndAccountId() {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "DELETE FROM enrolment WHERE booking_id = ? AND crew_member_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ps.setInt(2, accountId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try {
                    Mailer.sendBookingDeenrolment(bookingId, accountId);
                } catch (MessagingException e) {
                    System.out.println("Error while sending booking de-enrolment: " + e.getMessage());
                }
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }

    @RolesAllowed({"Administrator", "Crew"})
    @Path("/label")
    @PUT
    public Response updateLabel(@QueryParam("label") String label) {

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "UPDATE enrolment SET label = ? WHERE booking_id = ? AND crew_member_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, label);
            ps.setInt(2, bookingId);
            ps.setInt(3, accountId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return Response.ok().entity("Booking label was updated").build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return Response.serverError().build();
    }

    @RolesAllowed({"Administrator", "Crew"})
    @Path("/label")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLabelByAccountAndBookingId() {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT enrolment.label FROM enrolment WHERE booking_id = ? AND crew_member_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ps.setInt(2, accountId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Response.ok().entity(rs.getString(1)).build();
            } else {
                return Response.ok().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Response.serverError().build();
    }

    public static boolean roleIsValid(String value) {
        for (Role type : Role.values()) {
            if (valueOf(type).equals(value)) {
                return true;
            }
        }
        return false;
    }

}