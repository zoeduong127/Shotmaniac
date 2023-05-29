package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.model.Account;
import shotmaniacs.group2.di.model.AccountType;

import java.sql.*;
import java.text.ParseException;

public enum AccountDao {
    instance;
    // TODO: Hook this class up to the database
    public void addAccount (Account account ){
        String host = "bronto.ewi.utwente.nl";
        String dbName ="dab_dsgnprj_50";
        String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
        String password = "yummybanana";

        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO account VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,account.getId());
            preparedStatement.setString(2,account.getUsername());
            preparedStatement.setString(3,account.getEmail());
            preparedStatement.setString(4, account.getPasswordHash());
            preparedStatement.setString(5, String.valueOf(account.getAccountType()));
            int rowsInseted = preparedStatement.executeUpdate();
            while(rowsInseted > 0){
                System.out.println("Successfully");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        System.out.println("Unsuccessfully");
    }

    public boolean logincheck (Account account ){
        String host = "bronto.ewi.utwente.nl";
        String dbName ="dab_dsgnprj_50";
        String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
        String password = "yummybanana";

        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM account WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,account.getEmail());
            preparedStatement.setString(2, account.getPasswordHash());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                System.out.println("Login Successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return false;
    }
    //    public static void main (String args[]) throws ParseException {
//        Account account = new Account(2, "Duong Huyen","duongthuhuyen@student.utwente.nl","meomeo", AccountType.Administrator);
//        AccountDao.instance.addAccount(account);
//    }
    public static void main (String args[]) throws ParseException {
        Account account = new Account(2, "Duong Huyen","duongthuhuyen@student.utwente.nl","meomeo", AccountType.Administrator);
        AccountDao.instance.logincheck(account);
    }

}

