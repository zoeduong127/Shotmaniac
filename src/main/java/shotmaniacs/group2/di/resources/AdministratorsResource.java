package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.AccountType;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

@Path("/admin")
public class AdministratorsResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addAccount(Account account) {

        // Performing input validation on the given account credentials.
        if (accountExists(account)) {
            return  Response.status(422)
                    .entity("Username or email is already in use.")
                    .build();
        } else if (account.getUsername() == null || account.getUsername().length() <= 3) {
            return  Response.status(422)
                    .entity("Username is invalid or is less than 4 characters long.")
                    .build();
        } else if (account.getEmail() == null || !isEmailValid(account.getEmail())) {
            return  Response.status(422)
                    .entity("Email is in an invalid format.")
                    .build();
        } else if (account.getPasswordHash() == null || account.getPasswordHash().length() <= 6) {
            return  Response.status(422)
                    .entity("Password is invalid or is less than 7 characters long.")
                    .build();
        } else if (!accountTypeContains(valueOf(account.getAccountType()))) {
            return  Response.status(422)
                    .entity("Account type is invalid.")
                    .build();
        }

        String salt = generateSalt();
        account.setPasswordHash(hash256(account.getPasswordHash() + salt));
        account.setSalt(salt);

        int rowsAffected = AccountDao.instance.addAccount(account);

        if (rowsAffected > 0) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
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

    public static boolean isEmailValid(String email) {
        // Define the email pattern using regular expression
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(emailPattern);

        // Match the input email against the pattern
        Matcher matcher = pattern.matcher(email);

        // Check if the email matches the pattern
        return matcher.matches();
    }

    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltBytes = new byte[16];
        secureRandom.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static boolean accountTypeContains(String value) {
        for (AccountType type : AccountType.values()) {
            if (valueOf(type).equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given an account object containing a username and email, it checks if an account with the same username or email already exists.
     * @param account The account to test
     * @return True of the account already exists, false otherwise.
     */
    public boolean accountExists(Account account) {
        String sql = "SELECT * FROM account WHERE account.username = ? OR account.email = ?";
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
