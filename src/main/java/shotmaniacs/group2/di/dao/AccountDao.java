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
    // TODO: Hook this class up to the database
    public void addAccount(Account account) {
        String host = "bronto.ewi.utwente.nl";
        String dbName ="dab_dsgnprj_50";
        String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
        String password = "yummybanana";

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

    public Account loginCheck(LoginInfor account) {
        String host = "bronto.ewi.utwente.nl";
        String dbName ="dab_dsgnprj_50";
        String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
        String password = "yummybanana";

        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM account WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,account.getEmail());
            preparedStatement.setString(2, account.getPassword());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                System.out.println("Login Successfully");
                return new Account(rs.getInt(1), rs.getString(2),rs.getString(3),
                        rs.getString(4),AccountType.valueOf(rs.getString(5)));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return null;
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


    public static void main (String args[]) throws ParseException {
        Account account = new Account(2, "Duong Huyen","duongthuhuyen@student.utwente.nl","meomeo", AccountType.Administrator);
        account.setPasswordHash(hash256(account.getPasswordHash()));
        AccountDao.instance.addAccount(account);
    }
}