package shotmaniacs.group2.di.resources;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.model.Account;

import javax.json.Json;
import javax.json.JsonObject;
import java.security.Key;
import java.sql.Date;
import java.util.HashMap;

@Path("/login")
public class LoginResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private HashMap<Integer, String> userTokens = new HashMap<>();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Account credentials) {
        // Validate user credentials
        if (isValidCredentials(credentials)) {
            String token = generateToken(credentials.getUsername());

            // Create a JSON response with the token
            JsonObject responseJson = Json.createObjectBuilder()
                    .add("token", token)
                    .build();

            // Todo: Get the crewMemberId and put it in userTokens with the token.

            // Return the response with HTTP status 200 OK
            return Response.ok(responseJson).build();
        } else {
            // Return an error response with HTTP status 401 Unauthorized
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    // TODO: Add log out api call that destroys the token

    private boolean isValidCredentials(Account credentials) {
        // TODO: Validate the user credentials against the database
        return false;
    }

    private String generateToken(String username) {
        // Generate a unique token for the user
        long expirationTimeInMillis = 24 * 60 * 60 * 1000; // 1 day

        // Set the token expiration time
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeInMillis);

        // Generate the JWT token
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();

        return token;
    }
}
