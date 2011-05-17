package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Taxi;

/**
 * 
 * Handles database, which is used while finding a taxi for a trip
 * 
 * @author Nicolai
 *
 */

public class OngoingTripsDAO {

	// Variables
	private Connection con;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	/**
	 * 
	 * Adds an arraylist of taxis, which are linked with a trip ID and a return IP.
	 * 
	 * @param tripID - trip, which the taxis are linked to.
	 * @param taxiList - List of taxis
	 * @param company - return IP.
	 * @return
	 */
	public boolean addAwaitingTaxis(String tripID, ArrayList<Taxi> taxiList,
			String company) {
		String Query = "INSERT INTO ongoing_trips (trip_id, taxi_id, taxi_coordinate, company)"
				+ "VALUES (?, ?, ?, ?) ";

		int rowCount = 0;
		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(Query);

			for (int i = 0; i < taxiList.size(); i++) {
				preparedStatement.setString(1, tripID);
				preparedStatement.setString(2, taxiList.get(i).getTaxiID());
				preparedStatement.setString(3, taxiList.get(i).getTaxiCoord());
				preparedStatement.setString(4, company);

				rowCount = preparedStatement.executeUpdate();
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
		// We want to return false if INSERT was unsuccesfull, else return true
		if (rowCount == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Clears the database for taxis waiting for a specific trip
	 * 
	 * @param tripID
	 * @return true if successful, else false
	 */
	public boolean deleteOngoingTrip(String tripID) {

		String Query = "DELETE FROM ongoing_trips WHERE trip_id = ?";

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
	 * Returns an arraylist of taxis, which are linked to a specific tripID
	 * 
	 * @param tripID
	 * @return true if successful, else false
	 */
	public ArrayList<Taxi> getTaxiByTrip(String tripID) {
		ArrayList<Taxi> taxiList = new ArrayList<Taxi>();

		String taxiID, taxiCoord, company;
		
		String Query = "SELECT * FROM ongoing_trips WHERE trip_id = ?";

		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(Query);
			preparedStatement.setString(1, tripID);

			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				taxiID = resultSet.getString("taxi_id");
				taxiCoord = resultSet.getString("taxi_coordinate");
				company = resultSet.getString("company");
	            taxiList.add(new Taxi(taxiID, taxiCoord, company));
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

		return taxiList;
	}

	/**
	 * 
	 * Returns an arraylist of unique company IPs, which are linked to a specific trip
	 * 
	 * @param tripID
	 * @return true if successful, else false
	 */
	public ArrayList<String> getCompanyIP(String tripID) {

		String companyIP = "";
		ArrayList<String> companyIpList = new ArrayList<String>();

		String Query = "SELECT DISTINCT company FROM ongoing_trips WHERE trip_id = ?";

		con = null;

		try {
			con = PostgresqlConnectionFactory.createConnection();
			preparedStatement = con.prepareStatement(Query);
			preparedStatement.setString(1, tripID);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				companyIP = resultSet.getString("company");
				companyIpList.add(companyIP);
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

		return companyIpList;
	}
}
