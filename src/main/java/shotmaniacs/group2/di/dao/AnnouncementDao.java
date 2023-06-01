package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public enum AnnouncementDao {
    // TODO: Hook this class up to the database in
    instance;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";


    public void addAnnouncement(Announcement announcement) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO annoucement VALUES (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,announcement.getId());
            preparedStatement.setString(2,announcement.getTitle());
            preparedStatement.setString(3,announcement.getBody());
            preparedStatement.setInt(4, announcement.getPublisher());
            preparedStatement.setString(5, String.valueOf(announcement.getUrgency()));
            preparedStatement.setTimestamp(6,announcement.getDate());
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

    public Announcement getAnAnnouncement(int announcementId) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM announcement WHERE announcement_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,announcementId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                System.out.println("Successful");
                return new Announcement(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4), Urgency.valueOf(rs.getString(5)), rs.getTimestamp(6));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return null;
    }

    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcementList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT announcement.* FROM announcement";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Announcement announcement = new Announcement(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getInt(4), Urgency.valueOf(resultSet.getString(5)), resultSet.getTimestamp(6));
                announcementList.add(announcement);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return announcementList;
    }
}