package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EnrolmentResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int enrolmentId;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";
    public EnrolmentResource(UriInfo uriInfo, Request request, int enrolmentId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.enrolmentId = enrolmentId;
    }

    @DELETE
    public Response deleteEnrolmentById() {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "DELETE FROM enrolment WHERE enrolment_id;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, enrolmentId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().build();
            } else {
                return Response.ok().entity("No rows were affected.").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }

    @Path("/label")
    @PUT
    public Response updateLabelById(@QueryParam("label") String label) {

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "UPDATE enrolment SET label = ? WHERE enrolment_id = ?;";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, label);
            ps.setInt(2, enrolmentId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return Response.ok().entity("Booking label was updated").build();
            } else {
                return Response.ok().entity("Booking label was not updated").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }

    @Path("/modifyrole")
    @PUT
    public Response updateRoleById(@QueryParam("role") String role) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "UPDATE enrolment SET role = ? WHERE enrolment_id = ?;";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, role);
            ps.setInt(2, enrolmentId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return Response.ok().entity("Booking role was updated").build();
            } else {
                return Response.ok().entity("Booking role was not updated").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }
}
