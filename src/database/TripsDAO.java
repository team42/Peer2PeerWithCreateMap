package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Trip;

/**
 * 
 * Handles database, which holds information on all trips which are linked to taxis.
 * 
 * @author Nicolai
 *
 */
public class TripsDAO {

	private Connection con;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	/**
	 * 
	 * Taxi confirms trip. Accepted flag is set to 1
	 * 
	 * @param taxiID - Taxi, which accepts
	 * @param tripID - Trip, which is accepted
	 * @return
	 */
	public boolean confirmTrip(String taxiID, String tripID) {

		String cardsQuery1 = "UPDATE trips SET accepted = 1 WHERE taxi_id = ? AND trip_id = ?";

		String cardsQuery2 = "DELETE FROM trips WHERE taxi_id <> ? AND trip_id = ?";

		int rowCount = 0;
		con = null;

		try {

			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(cardsQuery1);
			preparedStatement.setString(1, taxiID);
			preparedStatement.setString(2, tripID);
			rowCount = preparedStatement.executeUpdate();
			preparedStatement.close();

			preparedStatement = con.prepareStatement(cardsQuery2);
			preparedStatement.setString(1, taxiID);
			preparedStatement.setString(2, tripID);
			rowCount = preparedStatement.executeUpdate();
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
		// We want to return false if INSERT was unsuccesfull, else return true
		if (rowCount == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * Removes all rows, which belongs to a specific trip.
	 * 
	 * @param tripID
	 * @return
	 */
	public boolean deleteTrip(String tripID) {

		String Query = "DELETE FROM trips WHERE trip_id = ?";

		int rowCount = 0;
		con = null;

		try {

			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(Query);
			preparedStatement.setString(1, tripID);

			rowCount = preparedStatement.executeUpdate();

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
		// We want to return false if INSERT was unsuccesfull, else return true
		if (rowCount == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * Insert new trip and link to taxi.
	 * Trip coordinate and return IP is noted.
	 * 
	 * @param taxiID
	 * @param tripID
	 * @param tripCoord
	 * @param returnIP
	 * @return
	 */
	public boolean insertTrip(String taxiID, String tripID, String tripCoord, String returnIP) {
		String cardsQuery = "INSERT INTO trips (taxi_id, trip_id, destination, started, return_ip)"
				+ "VALUES (?, ?, ?, Now(), ?) ";

		int rowCount = 0;
		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(cardsQuery);
			preparedStatement.setString(1, taxiID);
			preparedStatement.setString(2, tripID);
			preparedStatement.setString(3, tripCoord);
			preparedStatement.setString(4, returnIP);			

			rowCount = preparedStatement.executeUpdate();
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
		// We want to return false if INSERT was unsuccesfull, else return true
		if (rowCount == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * Deletes a trip for a specific taxi
	 * 
	 * @param taxiID
	 * @param tripID
	 * @return
	 */
	public boolean taxiDeleteTrip(String taxiID, String tripID) {
		String query = "DELETE FROM trips WHERE taxi_id = ? AND trip_id = ?";

		int rowCount = 0;
		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();

			preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, taxiID);
			preparedStatement.setString(2, tripID);

			rowCount = preparedStatement.executeUpdate();

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
		// We want to return false if INSERT was unsuccesfull, else return true
		if (rowCount == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 
	 * Returns the amount of trips a taxi is linked to.
	 * 
	 * @param taxiID
	 * @return amount of trips taxi is linked to.
	 */
	public int taxiTripAmount(String taxiID) {
		int amount = 0;
		
		String Query = "SELECT COUNT(*) FROM trips WHERE taxi_id = ?";

		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(Query);

			preparedStatement.setString(1, taxiID);
			resultSet = preparedStatement.executeQuery();

			if(resultSet.next()) {
			   amount = resultSet.getInt(1);
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
		
		return amount;
	}
	
	/**
	 * 
	 * Get all trips, which are linked to a specific taxi.
	 * 
	 * @param taxiID - Taxi of which trips are requested.
	 * @return ArrayList of trips
	 */
 	public ArrayList<Trip> getTripsByTaxiID(String taxiID) {
		ArrayList<Trip> tripList = new ArrayList<Trip>();

		String tripID, destination;
		int accepted;
		java.sql.Date started;

		String Query = "SELECT * FROM trips WHERE taxi_id = ? ORDER BY accepted DESC";

		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(Query);

			preparedStatement.setString(1, taxiID);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
            tripID = resultSet.getString("trip_id");
            destination = resultSet.getString("destination");
            accepted = resultSet.getInt("accepted");
            started = resultSet.getDate("started");

            java.util.Date date = new java.util.Date(started.getTime());

            tripList.add(new Trip(tripID, accepted, destination, date));
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
		return tripList;
	}

 	/**
 	 * 
 	 * Gets the IP of the Peer, which is handling the specific trip.
 	 * 
 	 * @param tripID
 	 * @return
 	 */
	public String getReturnIP(String tripID) {
		String returnIP = "";

		String Query = "SELECT return_ip FROM trips WHERE trip_id = ?";

		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(Query);
			preparedStatement.setString(1, tripID);

			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
			   returnIP = resultSet.getString("return_ip");
			} else {
			   System.out.println("No records");
			   return "No records";
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
		return returnIP;
	}
	
}
