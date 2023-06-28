package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.dto.Bookingdto;
import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.BookingState;
import shotmaniacs.group2.di.model.BookingType;
import shotmaniacs.group2.di.model.EventType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public enum  BookingDao {
    instance;

    // TODO: Hook this class up to the database
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";



    public boolean addBooking(Booking booking) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO booking VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,booking.getName());
            preparedStatement.setString(2,booking.getDescription());
            preparedStatement.setString(3, String.valueOf(booking.getEventType()));
            preparedStatement.setTimestamp(4, booking.getDate());
            preparedStatement.setString(5,booking.getLocation());
            preparedStatement.setInt(6,booking.getDuration());
            preparedStatement.setString(7,booking.getClientName());
            preparedStatement.setString(8,booking.getClientEmail());
            preparedStatement.setString(9,booking.getPhoneNumber());
            preparedStatement.setString(10, String.valueOf(booking.getBookingType()));
            preparedStatement.setString(11, String.valueOf(BookingState.PENDING));
            preparedStatement.setInt(12, booking.getSlots());
            int rowsInserted = preparedStatement.executeUpdate();
            if(rowsInserted > 0) {
                System.out.println("Successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        System.out.println("Unsuccessfully");
        return false;
    }


    public Booking getABooking(int id) {
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM booking WHERE booking_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return null;
    }

    public List<Booking> getAllBookings() {
        List<Booking> listbooking = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT b.* FROM booking b";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)), rs.getInt(13), rs.getInt(14));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }
}