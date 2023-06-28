package shotmaniacs.group2.di.dao;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.AccountType;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.EventType;
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
            String query = "INSERT INTO account VALUES (DEFAULT,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getEmail());
            preparedStatement.setString(3, account.getPasswordHash());
            preparedStatement.setString(4, String.valueOf(account.getAccountType()));
            preparedStatement.setString(5, account.getSalt());
            preparedStatement.setString(6, account.gettel());
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Successfully");
                return rowsInserted;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        System.out.println("Unsuccessfully");
        return 0;
    }



}