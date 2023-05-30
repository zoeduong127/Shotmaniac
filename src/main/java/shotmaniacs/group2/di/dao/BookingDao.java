package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.BookingState;
import shotmaniacs.group2.di.model.BookingType;
import shotmaniacs.group2.di.model.EventType;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public enum  BookingDao {
    instance;
    // TODO: Hook this class up to the database
    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";

    public void addBooking (Booking booking){

        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO booking VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,booking.getId());
            preparedStatement.setString(2,booking.getName());
            preparedStatement.setString(3,booking.getDescription());
            preparedStatement.setString(4, String.valueOf(booking.getEventType()));
            preparedStatement.setTimestamp(5, booking.getDate());
            preparedStatement.setString(6,booking.getLocation());
            preparedStatement.setInt(7,booking.getDuration());
            preparedStatement.setString(8,booking.getClientname());
            preparedStatement.setString(9,booking.getClientEmail());
            preparedStatement.setString(10,booking.getPhoneNumber());
            preparedStatement.setString(11, String.valueOf(booking.getBookingType()));
            preparedStatement.setString(12, String.valueOf(booking.getState()));
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
    public Booking get_a_booking(int id){
        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT * FROM booking WHERE booking_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                System.out.println("Login Successfully");
                return new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)));
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return null;
    }
    public List<Booking> getallBooking(){
        List<Booking> listbooking = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "SELECT booking.* FROM booking b  ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Booking booking = new Booking(rs.getInt(1), rs.getString(2),rs.getString(3),
                        EventType.valueOf(rs.getString(4)),rs.getTimestamp(5),rs.getString(6),
                        rs.getInt(7),rs.getString(8),rs.getString(9),rs.getString(10), BookingType.valueOf(rs.getString(11)), BookingState.valueOf(rs.getString(12)));
                listbooking.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting: "+e);
        }
        return listbooking;
    }
}

