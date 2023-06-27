package shotmaniacs.group2.di.tests.applicationTests;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.RootElementWrapper;
import shotmaniacs.group2.di.resources.CrewsResourse;
import shotmaniacs.group2.di.resources.LoginResource;
import shotmaniacs.group2.di.security.TokenManager;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class LoginResourceTest extends JerseyTest {

    private static final String EMAIL = "testaccount@student.utwente.nl";
    private static final String PASSWORD = "yummybanana";


    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(LoginResource.class);

        return config;
    }

    @Test
    public void loginAndLogoutTest() {
//        Response response = loginResource.loginCheck(loginDetails);
//        int statusCode = response.getStatus();
//        assertTrue(statusCode >= 200 && statusCode < 300);
//
//        RootElementWrapper responseObject = (RootElementWrapper) response.getEntity();
//        String token = responseObject.getTokens().get(0);
//
//        response = loginResource.logOut(token);
//        statusCode = response.getStatus();
//        assertTrue(statusCode >= 200 && statusCode < 300);

        Response response = target("/login")
                .request()
                .post(Entity.json(new LoginInfor(EMAIL,PASSWORD)));

        RootElementWrapper responseObject = response.readEntity(RootElementWrapper.class);
        String authToken = responseObject.getTokens().get(0);
        Assertions.assertTrue(authToken != null && !authToken.equals(""));
        Assertions.assertTrue(response.getStatus() == 200);
    }
}
