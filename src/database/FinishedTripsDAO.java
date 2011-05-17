package database;

import java.sql.*;

/**
 * 
 * Handles trips, which are finished.
 * 
 * @author Nicolai
 *
 */

public class FinishedTripsDAO {
	
	// Variables
	private Connection con;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	/**
	 * Add a trip to the database, which indicates that the trip has been handled.
	 * 
	 * @param tripID
	 * @param taxiID
	 * @return true if succesful, else false
	 */
	public boolean addTrip(String tripID, String taxiID) {
	      String cardsQuery = "INSERT INTO finished_trips (trip_id, taxi_id) VALUES (?, ?)";

	      int rowCount = 0;
	      con = null;

	      try {
	         con = PostgresqlConnectionFactory.createConnection();
	         preparedStatement = con.prepareStatement(cardsQuery);
	         preparedStatement.setString(1, tripID);
	         preparedStatement.setString(2, taxiID);

	         rowCount = preparedStatement.executeUpdate();
	         preparedStatement.close();

	      } catch (SQLException e) {
	         e.printStackTrace();
	      } finally {
	         if (con != null) {
	            try { con.close(); }
	            catch (SQLException e1) { System.out.println("Failed Closing of Database!"); }
	         }
	      }
	      // We want to return false if INSERT was unsuccesfull, else return true
	      if (rowCount == 0) { return false; }
	      else { return true; }

	   }
	
	/**
	 * Figure out if a trip is finished.
	 * 
	 * @param tripID
	 * @return true if the trip is finished, else false
	 */
	public boolean isTripFinished(String tripID) {

		String query = "SELECT COUNT(*) FROM finished_trips WHERE trip_id = ?";

		con = null;
		
		boolean finished = false;
		
		try {

			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, tripID);

			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
			   if(resultSet.getInt(1) > 0) {
	            finished = true;
	         } else {
	            finished = false;
	         }
			}
			
			preparedStatement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
					System.out.println("Failed Closing of Database!");
				}
			}
		}
		
		return finished;
	}
}
