package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AnnouncementDao;
import shotmaniacs.group2.di.model.Announcement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AnnouncementResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    int announcementId;

    public AnnouncementResource(UriInfo uriInfo, Request request, int announcementId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.announcementId = announcementId;
    }

    @RolesAllowed({"Administrator","Crew"})
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    /**
     * Return the details of a specific announcement (provided with an ID)
     */
    public Announcement getAnnouncement() {
        Announcement announcement = AnnouncementDao.instance.getAnAnnouncement(announcementId);
        return announcement;
    }

    @RolesAllowed({"Administrator"})
    @DELETE
    public Response deleteAnnouncement() {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);

            String sql = "DELETE FROM announcement WHERE announcement_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, announcementId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().build();
            } else {
                return Response.ok().entity("No announcements with that id were found.").build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }

    @RolesAllowed({"Administrator"})
    @PUT
    @Path("/state")
    public Response setState(@QueryParam("state") String state) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "FROM announcement a SET a.state = ? WHERE a.announcement_id = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, state);
            ps.setInt(2, announcementId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }
}