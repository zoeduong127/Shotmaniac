package shotmaniacs.group2.di.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import shotmaniacs.group2.di.dao.AccountDao;
import shotmaniacs.group2.di.model.Account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

}
