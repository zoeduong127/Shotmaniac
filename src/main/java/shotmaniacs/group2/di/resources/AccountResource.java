package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.Role;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.String.valueOf;

public class AccountResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    int accountId;

    public AccountResource(UriInfo uriInfo, Request request, int accountId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.accountId = accountId;
    }

    @RolesAllowed({"Administrator"})
    @DELETE
    public Response deleteAccountById() {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "DELETE FROM account WHERE account.id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().entity("Account was deleted.").build();
            } else {
                return Response.notModified().build();
            }

        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return Response.serverError().build();
    }

    @RolesAllowed({"Administrator"})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccountById() {
        return AccountDao.instance.getAccountById(accountId);
    }

    @RolesAllowed({"Administrator"})
    @PUT
    @Path("/role")
    public Response setAccountRoleById(@QueryParam("role") String role) {

        if (!roleIsValid(role)) {
            return Response.notModified().build();
        }

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "UPDATE account SET account.role = ? WHERE account.id = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, role);
            ps.setInt(2, accountId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.out.println("Error while setting account role by id: " + e.getMessage());
        }

        return Response.serverError().build();
    }

    public static boolean roleIsValid(String value) {
        for (Role type : Role.values()) {
            if (valueOf(type).equals(value)) {
                return true;
            }
        }
        return false;
    }

}
