package shotmaniacs.group2.di.tests;

import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.message.internal.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.resources.LoginResource;
import shotmaniacs.group2.di.security.TokenManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class LoginResourceTest {

    private static final String email = "testaccount@student.utwente.nl";
    private static final String password = "yummybanana";
    private static LoginResource loginResource;
    private static LoginInfor loginDetails;
    @BeforeAll
    public static void beforeAll() {
        loginResource = new LoginResource();
        loginDetails = new LoginInfor(email, password);
        try {

            String path = System.getProperty("user.dir") + "\\src\\main\\webapp\\security\\tokenKey.key";
            System.out.println(path);

            byte[] keyBytes = Files.readAllBytes(Paths.get(path));

            SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
            TokenManager.setTokenKey(key);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Test
    public void testMethodName() {
        // Arrange: Set up any necessary preconditions or input data

        // Act: Perform the operation or method call being tested

        // Assert: Verify the expected result

        // Optional: Add additional assertions or perform cleanup
    }
    @Test
    public void loginTest() {
        Response response = loginResource.loginCheck(loginDetails);

        int statusCode = response.getStatus();

        System.out.println(statusCode);
        assertTrue(statusCode >= 200 && statusCode < 300);
    }
}