package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.dao.AnnouncementDao;
import shotmaniacs.group2.di.emails.Mailer;
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

    @RolesAllowed({"Administrator", "Crew"})
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
        } else if (!accountTypeIsValid(valueOf(account.getAccountType()))) {
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
                            AccountType.valueOf(rs.getString(5)), rs.getString(6), rs.getString(7), rs.getString(8));
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
                            AccountType.valueOf(rs.getString(5)), rs.getString(6), rs.getString(7), rs.getString(8));
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

    @Path("/announcements/search")
    @RolesAllowed({"Administrator", "Crew"})
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Announcement> searchAllAnnouncementsByTitleText(@QueryParam("searchtext") String searchText) {

        List<Announcement> announcementList = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT * FROM announcement a WHERE to_tsvector(a.title) @@ plainto_tsquery(?);";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, searchText);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                announcementList.add(new Announcement(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4), Urgency.valueOf(rs.getString(5)), rs.getTimestamp(6)));
            }
        } catch (SQLException e) {
            System.out.println("Error searching all announcements by title text: " + e.getMessage());
        }
        return announcementList;
    }


    @RolesAllowed({"Administrator", "Crew", "Client"})
    @Path("/booking/{booking_id}/crew")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public List<Account> getEnrolledCrewMembersByBookingId(@PathParam("booking_id") int booking_id) {
        List<Account> accountList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT a.* FROM account a, enrolment e WHERE e.booking_id = ? AND e.crew_member_id = a.account_id;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,booking_id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                accountList.add(new Account(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), AccountType.valueOf(rs.getString(5)), rs.getString(6), rs.getString(7), rs.getString(8)));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return accountList;
    }

    @RolesAllowed({"Administrator"})
    @Path("/bookings/timefilter/{time}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter all bookings with timestamp
     * Param "ongoing": filter ongoing event
     * Param "past":filter past event
     */
    public List<Booking> getAllBookingsWithTimeFilter(@PathParam("time") String filter) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            if(filter.equals("ongoing")) {
                query = "SELECT b.* FROM booking b WHERE b.date_and_time >= NOW()";
            } else {
                query = "SELECT b.* FROM booking b WHERE b.date_and_time < NOW()";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator"})
    @Path("/bookings/statefilter/{state}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Filter all bookings with timestamp
     * Param "approved": approved bookings
     * Param "pending": pending bookings
     */
    public List<Booking> getAllBookingsWithStatusFilter(@PathParam("state") String state) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            if(state.equals("approved")) {
                query = "SELECT b.* FROM booking b WHERE b.state = 'APPROVED'";
            } else if (state.equals("pending")){
                query = "SELECT b.* FROM booking b WHERE b.state = 'PENDING'";
            } else {
                return null;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }

    @RolesAllowed({"Administrator"})
    @Path("/booking/{booking_id}/crew/{crew_id}")
    /**
     * Modify specific event with given ID
     */
    public BookingResource modifySpecificBooking(@PathParam("crew_id") int crewid, @PathParam("booking_id") int id) {
        return new BookingResource(uriInfo, request, crewid, id);
    }

    @RolesAllowed({"Administrator"})
    @Path("/booking/{booking_id}/state")
    @PUT
    public Response setBookingState(@PathParam("booking_id") int bookingId, @QueryParam("state") String state) {

        if (!bookingStateIsValid(state)) {
            return Response.notModified().entity("State is not valid.").build();
        }

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "UPDATE booking SET state = ? WHERE booking_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, state);
            ps.setInt(2, bookingId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }

        } catch (SQLException e) {
            System.out.println("Error while setting booking state: " + e.getMessage());
        }

        return Response.serverError().build();
    }

    @RolesAllowed({"Administrator"})
    @Path("/booking/{booking_id}/productmanager/{product_manager_id}")
    @PUT
    public Response setProductManager(@PathParam("booking_id") int bookingId, @PathParam("product_manager_id") int prod_man_id) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "UPDATE booking SET product_manager_id = ? WHERE booking_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, prod_man_id);
            ps.setInt(2, bookingId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.out.println("Error while setting booking product manager: " + e.getMessage());
        }
        return Response.serverError().build();
    }

    @RolesAllowed({"Administrator", "Crew"})
    @Path("/booking/{booking_id}/productmanager")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Account getProductManagerByBookingId(@PathParam("booking_id") int bookingId) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT a.* FROM account a, booking b WHERE b.product_manager_id = a.account_id AND b.booking_id = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4),
                        AccountType.valueOf(rs.getString(5)), rs.getString(6), rs.getString(7), rs.getString(8));
            } else {
                return null;

            }
        } catch (SQLException e) {
            System.out.println("Error while getting booking product manager: " + e.getMessage());
        }
        return null;
    }

    @RolesAllowed({"Administrator","Crew"})
    @Path("/bookings/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> searchAllBookings(@QueryParam("searchtext") String searchText) {
        List<Booking> listbooking = new ArrayList<>();
        try {
            String query;
            Connection connection = DriverManager.getConnection(url, dbName, password);
            query = "SELECT b.* FROM booking b WHERE to_tsvector(b.title) @@ phraseto_tsquery(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,searchText);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
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

    public static boolean accountTypeIsValid(String value) {
        for (AccountType type : AccountType.values()) {
            if (valueOf(type).equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean bookingStateIsValid(String value) {
        for (BookingState type : BookingState.values()) {
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