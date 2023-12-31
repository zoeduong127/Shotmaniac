package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.dto.LoginInfor;
import shotmaniacs.group2.di.model.*;
import shotmaniacs.group2.di.resources.LoginResource;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public enum AnnouncementDao {
    instance;
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";


    public int addAnnouncement(Announcement announcement) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO announcement VALUES (DEFAULT,?,?,?,?,?) RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,announcement.getTitle());
            preparedStatement.setString(2,announcement.getBody());
            preparedStatement.setInt(3, announcement.getPublisher());
            preparedStatement.setString(4, String.valueOf(announcement.getUrgency()));
            preparedStatement.setTimestamp(5,announcement.getDate());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                    String query1 = "INSERT INTO mark_announcement VALUES (?,?,?)";
                    for (int i : getallemployee()) {
                        if (i == announcement.getPublisher()) {
                            continue;
                        }
                        PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                        preparedStatement1.setInt(1, resultSet.getInt(1));
                        preparedStatement1.setString(2, String.valueOf(AnnouncementState.UNREAD));
                        preparedStatement1.setInt(3, i);
                        preparedStatement1.executeUpdate();
                    }
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Unsuccessful");
        return 0;
    }
    public List<Integer> getallemployee(){
        List<Integer> list = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT account_id FROM account WHERE account_type = ? OR account_type = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,String.valueOf(AccountType.Crew));
            preparedStatement.setString(2,String.valueOf(AccountType.Administrator));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return list;
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
            String query = "SELECT * FROM announcement";
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
    public AnnouncementState getState(int announcementId,int id) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT read_status FROM mark_announcement WHERE announcement_id = ? AND employee_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,announcementId);
            preparedStatement.setInt(2,id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                System.out.println("Successful");
                return AnnouncementState.valueOf(rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: " + e);
        }
        return null;
    }
    public static void main (String args[]) throws ParseException {
        Announcement announcement = new Announcement(
                -1,                            // id
                "Important Announcement",     // title
                "This is an important announcement.",  // body
                42,                          // publisher
                Urgency.CRITICAL,                 // urgency (assuming 'Urgency' is an enum with HIGH, MEDIUM, LOW values)
                new Timestamp(System.currentTimeMillis())  // date (current timestamp)
        );
        AnnouncementDao.instance.addAnnouncement(announcement);
    }
}