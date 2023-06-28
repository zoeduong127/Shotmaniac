package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AnnouncementDao;
import shotmaniacs.group2.di.model.Announcement;

public class AnnouncementResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    int crewId;
    int announcementId;

    public AnnouncementResource(UriInfo uriInfo, Request request, int crewId, int announcementId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.crewId = crewId;
        this.announcementId = announcementId;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    /**
     * Return the details of a specific announcement (provided with an ID)
     */
    public Announcement getAnnouncement() {
        Announcement announcement = AnnouncementDao.instance.getAnAnnouncement(announcementId);

        if(announcement == null) {
            throw new RuntimeException("GET: announcement with " + announcementId + "is not defined");
        }
        return announcement;
    }
}