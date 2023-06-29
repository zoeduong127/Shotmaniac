package shotmaniacs.group2.di.tests.unitTests;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.*;
import shotmaniacs.group2.di.resources.AdministratorsResource;
import shotmaniacs.group2.di.resources.AnnouncementResource;
import shotmaniacs.group2.di.resources.LoginResource;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementTest extends JerseyTest {
    private static final String EMAIL = "testaccountadmin@student.utwente.nl";
    private static final String PASSWORD = "yummybanana";
    private static String authToken;


    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(AnnouncementResource.class);
        config.register(AdministratorsResource.class);
        return config;
    }

    @Before
    public static void before() {
        LoginResource loginResource = new LoginResource();
        Response response = loginResource.loginCheck(new LoginInfor(EMAIL,PASSWORD));
        RootElementWrapper wrapper = (RootElementWrapper) response.getEntity();

        authToken = wrapper.getTokens().get(0);
    }

    @Test
    public void announcementCreateSearchAndDeleteTest() {
        Response response = target("/admin/announcement")
                .request()
                .header("Authorization",authToken)
                .put(Entity.json(new Announcement(-1, "Announcement test",
                        "Announcement test body goes here.", 23,
                        Urgency.MINOR, new Timestamp(System.currentTimeMillis()))));

        Assertions.assertTrue(response.getStatus() == 200);

        response = target("admin/announcements/search")
                .queryParam("searchtext", "Announcement test")
                .request()
                .header("Authorization", authToken)
                .get();
        List<Announcement> announcements = response.readEntity(new GenericType<ArrayList<Announcement>>() {});

        Announcement announcement = announcements.get(0);
        int announcementId = announcement.getId();

        Assertions.assertEquals(announcement.getTitle(), "Announcement test");
        Assertions.assertTrue(response.getStatus() == 200);

        response = target("admin/announcement/" + announcementId)
                .request()
                .header("Authorization", authToken)
                .delete();

        Assertions.assertTrue(response.getStatus() == 200);
    }
}
