package shotmaniacs.group2.di.dao;

import shotmaniacs.group2.di.model.Booking;
import shotmaniacs.group2.di.model.BookingType;
import shotmaniacs.group2.di.model.EventType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

public enum  BookingDao {
    instance;
    // TODO: Hook this class up to the database

    public void addBooking (Booking booking){
        String host = "bronto.ewi.utwente.nl";
        String dbName ="dab_dsgnprj_50";
        String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
        String password = "yummybanana";


        try{
            Connection connection = DriverManager.getConnection(url, dbName, password);
            String query = "INSERT INTO booking VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,booking.getId());
            preparedStatement.setString(2,booking.getName());
            preparedStatement.setString(3,booking.getDescription());
            preparedStatement.setString(4, String.valueOf(booking.getEventType()));
            preparedStatement.setDate(5, booking.getDate());
            preparedStatement.setString(6,booking.getLocation());
            preparedStatement.setInt(7,booking.getDuration());
            preparedStatement.setString(8,booking.getClientname());
            preparedStatement.setString(9,booking.getClientEmail());
            preparedStatement.setString(10,booking.getPhoneNumber());
            preparedStatement.setString(11, String.valueOf(booking.getBookingType()));
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
    public static void main (String args[]) throws ParseException {
        long millis=System.currentTimeMillis();
        java.sql.Date sqldate=new java.sql.Date(millis);
        Booking booking = new Booking(3, "F", "Blabla", EventType.COMPETITION, sqldate, "Enschede", 3, "hello@gmail.com","hello","034892749", BookingType.FILM );
        BookingDao.instance.addBooking(booking);
    }
}

