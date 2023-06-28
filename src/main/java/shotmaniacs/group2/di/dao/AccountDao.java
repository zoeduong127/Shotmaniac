package shotmaniacs.group2.di.dao;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.*;
import shotmaniacs.group2.di.resources.LoginResource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.text.ParseException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

public enum AccountDao {
    instance;

    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName = "dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" + dbName + "?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    public int addAccount(Account account) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO account VALUES (DEFAULT,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getEmail());
            preparedStatement.setString(3, account.getPasswordHash());
            preparedStatement.setString(4, String.valueOf(account.getAccountType()));
            preparedStatement.setString(5, account.getSalt());
            preparedStatement.setString(6, account.getTelephone());
            preparedStatement.setString(7, String.valueOf(account.getRole()));
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                return rowsInserted;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return 0;
    }

    /**
     * Returns the account object that corresponds to the given account id.
     * @param accountId
     * @return The account if exists. Returns null otherwise.
     */
    public Account getAccountById(int accountId) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String sql = "SELECT * FROM account a WHERE a.account_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, accountId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                try {
                    Account result = new Account(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), AccountType.valueOf(rs.getString(5)), rs.getString(6), rs.getString(7), rs.getString(8));
                    return result;
                } catch (IllegalArgumentException e) {
                    return null;
                }

            }
        } catch (SQLException e) {
            System.out.println("Error getting account by id: " + e.getMessage());
        }
        return null;
    }
}