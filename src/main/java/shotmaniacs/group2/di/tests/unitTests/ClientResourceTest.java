package shotmaniacs.group2.di.tests.unitTests;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shotmaniacs.group2.di.dto.Accountdto;
import shotmaniacs.group2.di.dto.Bookingdto;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.EventType;
import shotmaniacs.group2.di.model.RootElementWrapper;
import shotmaniacs.group2.di.resources.AdministratorsResource;
import shotmaniacs.group2.di.resources.ClientsResource;
import shotmaniacs.group2.di.resources.LoginResource;

import java.util.ArrayList;
import java.util.List;

public class ClientResourceTest extends JerseyTest {
    private static final String EMAIL = "testaccountadmin@student.utwente.nl";
    private static final String PASSWORD = "yummybanana";
    private static String authToken;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(ClientsResource.class);
        config.register(AdministratorsResource.class);
        return config;
    }

    @Before
    public static void before() {
        LoginResource loginResource = new LoginResource();
        Response response = loginResource.loginCheck(new LoginInfor(EMAIL, PASSWORD));
        RootElementWrapper wrapper = (RootElementWrapper) response.getEntity();

        authToken = wrapper.getTokens().get(0);
    }
    @Test
    public void bookingCreateSearchAndDeleteTest() {
        Response response = target("/client")
                .request()
                .header("Authorization", authToken)
                .post(Entity.json(new Bookingdto("Booking creation test", "PRODUCT_SHOOT", "2023-03-19",
                        "3:25","Enschede","FILM",
                        31,"Description goes here",
                        "Luca", "clientemail@gmail.com","0928493401")));

        Assertions.assertEquals(200, response.getStatus());

        response = target("/admin/bookings/search")
                .queryParam("searchtext","Booking creation test")
                .request()
                .header("Authorization", authToken)
                .get();

        Assertions.assertEquals(200, response.getStatus());

        List<Booking> bookings = response.readEntity(new GenericType<ArrayList<Booking>>() {});
        Booking booking = bookings.get(0);

        Assertions.assertEquals("Booking creation test", booking.getName());
        Assertions.assertEquals(EventType.PRODUCT_SHOOT, booking.getEventType());
        Assertions.assertEquals(31, booking.getDuration());
        Assertions.assertEquals("Luca", booking.getClientName());

        int bookingId = booking.getId();

        response = target("admin/booking/" + bookingId)
                .request()
                .header("Authorization", authToken)
                .delete();

        Assertions.assertEquals(200, response.getStatus());

        response = target("/admin/bookings/search")
                .queryParam("searchtext","Booking creation test")
                .request()
                .header("Authorization", authToken)
                .get();

        bookings = response.readEntity(new GenericType<ArrayList<Booking>>() {});

        Assertions.assertEquals(0, bookings.size());
    }

    @Test
    public void createAndDeleteClientAccount() {
        Response response = target("/client/new_account")
                .request()
                .header("Authorization", authToken)
                .post(Entity.json(new Accountdto("testclientemail@gmail.com","createClientTest","createClientTestPassword", "0928493401")));

        Assertions.assertEquals(200, response.getStatus());

        response = target("/admin/accounts")
                .queryParam("username", "createClientTest")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
        List<Account> accounts = response.readEntity(new GenericType<ArrayList<Account>>() {});
        Assertions.assertEquals(1, accounts.size());
        Account account = accounts.get(0);

        Assertions.assertEquals("testclientemail@gmail.com", account.getEmail());
        Assertions.assertNotEquals("createClientTestPassword", account.getPasswordHash());

        response = target("/admin/accounts")
                .queryParam("username", "createClientTest")
                .request()
                .header("Authorization", authToken)
                .delete();
        Assertions.assertEquals(200, response.getStatus());

        response = target("/admin/accounts")
                .queryParam("username", "createClientTest")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());

        accounts = response.readEntity(new GenericType<ArrayList<Account>>() {});
        Assertions.assertEquals(0, accounts.size());
    }

}
