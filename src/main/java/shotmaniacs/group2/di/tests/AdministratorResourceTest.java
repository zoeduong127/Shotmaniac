package shotmaniacs.group2.di.tests;



import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.AccountType;
import shotmaniacs.group2.di.model.RootElementWrapper;
import shotmaniacs.group2.di.resources.AdministratorsResource;
import shotmaniacs.group2.di.resources.LoginResource;

public class AdministratorResourceTest extends JerseyTest {

    private static final String EMAIL = "testaccountadmin@student.utwente.nl";
    private static final String PASSWORD = "yummybanana";

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(LoginResource.class);
        config.register(AdministratorsResource.class);
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
    public void addAccountAndDeleteAccountByUsernameTest() {
        String authToken = authenticateUser(EMAIL, PASSWORD);

        Response response = target("/admin/accounts")
                .queryParam("username", "addAccountTest")
                .request()
                .header("Authorization", authToken)
                .delete();

        Assertions.assertEquals(200, response.getStatus());

        response = target("/admin/accounts")
                .request()
                .header("Authorization", authToken)
                .put(Entity.json(new Account(0,"addAccountTest", "addAccountTest@student.utwente.nl", "yummybanana", AccountType.Administrator, null)));

        Assertions.assertTrue(response.getStatus() == 200);

        response = target("/admin/accounts")
                .queryParam("username", "addAccountTest")
                .request()
                .header("Authorization", authToken)
                .delete();

        Assertions.assertEquals(200, response.getStatus());
    }
}
