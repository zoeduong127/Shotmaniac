package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.AccountType;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.EventType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;

public enum AccountDao {
    instance;
    private static String host = "bronto.ewi.utwente.nl";
    private static  String dbName ="dab_dsgnprj_50";
    private static  String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";
    // TODO: Hook this class up to the database
    public void addAccount(Account account) {

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO account VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,account.getId());
            preparedStatement.setString(2,account.getUsername());
            preparedStatement.setString(3,account.getEmail());
            preparedStatement.setString(4, account.getPasswordHash());
            preparedStatement.setString(5, String.valueOf(account.getAccountType()));
            int rowsInserted = preparedStatement.executeUpdate();
            while(rowsInserted > 0) {
                System.out.println("Successfully");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        System.out.println("Unsuccessfully");

    }

}