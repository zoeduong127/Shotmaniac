package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.model.Announcement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum AnnouncementDao {
    // TODO: Hook this class up to the databasein
    instance;
    public void addAccount (Announcement announcement ){
        String host = "bronto.ewi.utwente.nl";
        String dbName ="dab_dsgnprj_50";
        String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
        String password = "yummybanana";

        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO annoucement VALUES (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,announcement.getId());
            preparedStatement.setString(2,announcement.gettitle());
            preparedStatement.setString(3,announcement.getBody());
            preparedStatement.setInt(4, announcement.getPublisher());
            preparedStatement.setString(5, String.valueOf(announcement.getUrgency()));
            preparedStatement.setDate(6,announcement.getDate());
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
}