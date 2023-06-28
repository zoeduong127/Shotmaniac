package shotmaniacs.group2.di.resources;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.AccountType;
import shotmaniacs.group2.di.model.RootElementWrapper;

import java.nio.charset.StandardCharsets;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

@Path("/login")
public class LoginResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response loginCheck(LoginInfor account) {
        String salt = getSaltByEmail(account.getEmail());
        if (salt == null) {
            return Response.serverError().build();
        }

        account.setPassword(hash256(account.getPassword() + salt));
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM account WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,account.getEmail());
            preparedStatement.setString(2, account.getPassword());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                System.out.println("Login Successfully");

                RootElementWrapper responseObject = new RootElementWrapper();
                Account responseAccount =  new Account(rs.getInt(1), rs.getString(2),rs.getString(3),
                        rs.getString(4),AccountType.valueOf(rs.getString(5)),rs.getString(7));

                responseObject.addAccount(responseAccount);

                Timestamp expiration = addTime(new Timestamp(System.currentTimeMillis()), 24, Calendar.HOUR);
                String token = generateToken(responseAccount, expiration);
                responseObject.addToken(token);

                // Delete existing tokens associated with the same account (log other devices/browsers out)
                PreparedStatement tokenPS = connection.prepareStatement("DELETE FROM token WHERE token.account_id = ?");
                tokenPS.setInt(1, responseAccount.getId());
                tokenPS.executeUpdate();

                // Add token into the database
                tokenPS = connection.prepareStatement("INSERT INTO token (account_id, token, expiration) VALUES (?, ?, ?)");
                tokenPS.setInt(1, responseAccount.getId());
                tokenPS.setString(2, token);
                tokenPS.setTimestamp(3, expiration);
                int rowsAffected = tokenPS.executeUpdate();

                if (rowsAffected > 0) {
                    // Create a response builder object
                    Response.ResponseBuilder responseBuilder = Response.ok(responseObject, MediaType.APPLICATION_JSON);

                    // Set the Access-Control-Allow-Origin header to *
                    responseBuilder.header("Access-Control-Allow-Origin", "*");

                    // Build the response
                    return responseBuilder.build();
                } else {
                    return Response.serverError().build();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return Response.serverError().build();
    }

    @DELETE
    public Response logOut() {
        return null;
    }


    public String getSaltByEmail(String email) {
        String result = null;
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT * FROM account WHERE account.email = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = rs.getString(6);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return result;
    }

    public String hash256(String input) {
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

    private String generateToken(Account account, Timestamp expiration) {
        // Set the token expiration time
        Date expirationDate = new Date(expiration.getTime());

        // Generate the JWT token
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = Jwts.builder()
                .setSubject(account.getEmail())
                .claim("account_type", account.getAccountType())
                .claim("account_id", account.getId())
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();

        return token;
    }

    public static Timestamp addTime(Timestamp timestamp, int amount, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        cal.add(field, amount);
        return new Timestamp(cal.getTimeInMillis());
    }

    public static void main (String args[]) throws ParseException {
        LoginInfor account = new LoginInfor("duongthuhuyen@student.utwente.nl","meomeo");
        LoginResource login = new LoginResource();
        System.out.println(login.loginCheck(account));
    }
}