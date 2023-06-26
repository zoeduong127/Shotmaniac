package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dao.AnnouncementDao;
import shotmaniacs.group2.di.model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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

    @RolesAllowed({"Administrator"})
    @Path("enrolment/{enrolmentId}")
    public EnrolmentResource modifySpecificEnrolment(@PathParam("enrolmentId") int enrolmentId) {
        return new EnrolmentResource(uriInfo, request, enrolmentId);
    }

    @RolesAllowed({"Administrator"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("account/{accountId}")
    public AccountResource modifySpecificAccount(@PathParam("accountId") int accountId) {
        return new AccountResource(uriInfo, request, accountId);
    }

    @RolesAllowed({"Administrator"})
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/accounts")
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

    @RolesAllowed({"Administrator"})
    @DELETE
    @Path("/accounts")
    public Response deleteAccountByUsername(@QueryParam("username") String username) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "DELETE FROM account WHERE account.username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().entity("Account was deleted.").build();
            } else {
                return Response.ok().entity("Account with that username could not be found.").build();
            }

        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return Response.serverError().build();
    }

    @RolesAllowed({"Administrator"})
    @GET
    @Path("/accounts")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Account> searchAccountByUsername(@QueryParam("username") String username) {

        List<Account> accountList = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT * FROM account a WHERE to_tsvector(a.username) @@ phraseto_tsquery(?);";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                try {
                    Account result = new Account(rs.getInt(1), rs.getString(2),
                            rs.getString(3), rs.getString(4),
                            AccountType.valueOf(rs.getString(5)), rs.getString(6));
                    accountList.add(result);
                } catch (IllegalArgumentException ignored) {
                }
            }

        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return accountList;
    }

    @RolesAllowed({"Administrator"})
    @GET
    @Path("/accounts/{accountType}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Account> searchAccountByUsernameAndType(@QueryParam("username") String username, @PathParam("accountType") String accountType) {
        List<Account> accountList = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT * FROM account a WHERE to_tsvector(a.username) @@ phraseto_tsquery(?) AND a.account_type = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, accountType);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                try {
                    Account result = new Account(rs.getInt(1), rs.getString(2),
                            rs.getString(3), rs.getString(4),
                            AccountType.valueOf(rs.getString(5)), rs.getString(6));
                    accountList.add(result);
                } catch (IllegalArgumentException ignored) {
                }
            }

        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return accountList;
    }



    @Path("/announcement/{announcementid}")
    @RolesAllowed({"Administrator"})
    public AnnouncementResource modifySpecificAnnouncement(@PathParam("announcementid") int announcementId) {
        return new AnnouncementResource(uriInfo, request, announcementId);
    }

    @Path("/announcement")
    @RolesAllowed({"Administrator"})
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public Response createAnnouncement(Announcement announcement) {
        int rowsAffected = AnnouncementDao.instance.addAnnouncement(announcement);

        if (rowsAffected > 0) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @RolesAllowed({"Administrator"})
    @Path("/booking/{booking_id}/crew")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public List<Account> getEnrolledCrewMembersByBookingId(@PathParam("booking_id") int booking_id) {
        List<Account> accountList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT a.* FROM account a , enrolment e WHERE e.booking_id = ? AND e.crew_member_id = a.account_id;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,booking_id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                accountList.add(new Account(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), AccountType.valueOf(rs.getString(5)), rs.getString(6)));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return accountList;
    }

    @RolesAllowed({"Administrator"})
    @Path("/booking/{booking_id}/crew/{crew_id}")
    /**
     * Modify specific event with given ID
     */
    public BookingResource modifySpecificBooking(@PathParam("crew_id") int crewid, @PathParam("booking_id") int id) {
        return new BookingResource(uriInfo, request, crewid, id);
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
            System.out.println("Error while checking if account exists: " + e.getMessage());
        }
        return false;
    }
}
