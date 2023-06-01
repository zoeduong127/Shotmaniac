package shotmaniacs.group2.di.resources;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.AccountType;

import javax.json.Json;
import javax.json.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;

@Path("/login")
public class LoginResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private HashMap<Integer, String> userTokens = new HashMap<>();

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Response loginCheck(LoginInfor account) {
        String host = "bronto.ewi.utwente.nl";
        String dbName ="dab_dsgnprj_50";
        String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
        String password = "yummybanana";
        account.setPassword(hash256(account.getPassword()));
        try {

            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM account WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,account.getEmail());
            preparedStatement.setString(2, account.getPassword());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                System.out.println("Login Successfully");
                Account back =  new Account(rs.getInt(1), rs.getString(2),rs.getString(3),
                        rs.getString(4),AccountType.valueOf(rs.getString(5)));
                return Response.ok(back).build();
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return Response.noContent().build();
    }

    public static String hash256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

        // TODO: Add log out api call that destroys the token

//    private Account isValidCredentials(LoginInfor credentials) {
//        // TODO: Validate the user credentials against the database
//        return AccountDao.instance.loginCheck(credentials);
//    }
//
//    private String generateToken(Account account) {
//        // Generate a unique token for the user
//        long expirationTimeInMillis = 24 * 60 * 60 * 1000; // 1 day
//
//        // Set the token expiration time
//        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeInMillis);
//
//        // Generate the JWT token
//        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        String token = Jwts.builder()
//                .setSubject(String.valueOf(account))
//                .setExpiration(expirationDate)
//                .signWith(key)
//                .compact();
//
//        return token;
//    }
//public static void main (String args[]) throws ParseException {
//    LoginInfor account = new LoginInfor("duongthuhuyen@student.utwente.nl","meome");
//    LoginResource login = new LoginResource();
//    System.out.println(login.loginCheck(account));
//}
}