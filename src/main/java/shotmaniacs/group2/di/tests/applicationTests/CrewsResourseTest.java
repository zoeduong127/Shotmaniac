package shotmaniacs.group2.di.tests.applicationTests;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.RootElementWrapper;
import shotmaniacs.group2.di.resources.CrewsResourse;
import shotmaniacs.group2.di.resources.LoginResource;

public class CrewsResourseTest extends JerseyTest {

    private static final String EMAIL = "testaccount@student.utwente.nl";
    private static final String PASSWORD = "yummybanana";
    private static final int ACCOUNT_ID = 17;
    private static final int BOOKING_ID = 20;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(CrewsResourse.class);
        config.register(LoginResource.class);

        return config;
    }

    public String authenticateUser(String email, String password) {

        Response response = target("/login")
                .request()
                .post(Entity.json(new LoginInfor(email,password)));

        RootElementWrapper responseObject = response.readEntity(RootElementWrapper.class);
        String authToken = responseObject.getTokens().get(0);

        response.close();

        return authToken;
    }

    @Test
    public void getBookingTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);
        Response response = target("/crew/" + ACCOUNT_ID + "/mybookings")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void searchEnrolledBookingsTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/crew/" + ACCOUNT_ID + "/mybooking/search")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getBookingWithTimeFilterTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/crew/"+ ACCOUNT_ID +"/mybooking/timefilter/ongoing")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());

        response = target("/crew/2/mybooking/timefilter/past")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getBookingWithLabelFilterTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/crew/"+ ACCOUNT_ID +"/mybooking/labelfilter/todo")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());

        response = target("/crew/"+ ACCOUNT_ID +"/mybooking/labelfilter/in_progress")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());

        response = target("/crew/"+ ACCOUNT_ID +"/mybooking/labelfilter/review")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());

        response = target("/crew/"+ ACCOUNT_ID +"/mybooking/labelfilter/done")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAllBookingsTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/crew/"+ ACCOUNT_ID +"/allbookings")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAllAnnouncementsTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/crew/"+ ACCOUNT_ID +"/news")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getNewsWithFilterTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/crew/"+ ACCOUNT_ID +"/news/filter")
                .queryParam("status",0)
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());

        response = target("/crew/"+ ACCOUNT_ID +"/news/filter")
                .queryParam("status",1)
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAnnouncementOfBookingTest() {
        String authToken = authenticateUser(EMAIL,PASSWORD);

        Response response = target("/crew/"+ ACCOUNT_ID +"/mybooking/" + BOOKING_ID + "/announcements")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAnnouncementOfBookingWithFilterTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/crew/"+ ACCOUNT_ID +"/mybooking/" + BOOKING_ID + "/announcements")
                .queryParam("status",0)
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
    }


}
