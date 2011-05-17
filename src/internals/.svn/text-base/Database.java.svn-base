package internals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import pathCalculation.Neighbor;
import pathCalculation.Station;

import common.SerializableStation;
import commonProtocol.Node;

/**
 * @author Team2
 */
public class Database {

	private static final Database instance = new Database();
	Connection conn = null;
	ArrayList<Station> borderStations;
	Station[] stations;
	// Hashtable to get stationID by StationName
	Hashtable<String, Integer> stationNameToID = new Hashtable<String, Integer>();
	Hashtable<Integer, Integer> stationIDToDbID = new Hashtable<Integer, Integer>();

	Log log = Log.getInstance();

	// mapdata for client
	SerializableStation connectionsMapData[][];
	SerializableStation stationsMapData[];

	/**
	 * private constructor; for singleton use creates connection to the database
	 */
	private Database() {

		try {
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException cnfe) {
			log.log("Couldn't find driver class: " + cnfe);
		}

		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost", "ronnie3se10", "ihk090275eit");
			// From home
			// 130.226.195.37:57214
			// conn = DriverManager.getConnection("jdbc:postgresql://localhost",
			// "ronnie3se10", "ihk090275eit");

		} catch (SQLException e) {
			log.printAndLog("Couldn't connect: print out a stack trace and exit. " + e);
			System.exit(1);
		}

	}

	/**
	 * singleton method for getting instance of the database class
	 * 
	 * @return instance of the database class
	 */
	public static Database getInstance() {
		return instance;
	}

	/**
	 * initializes the local memory cached
	 */
	public void init() {
		try {
			// clear old data
			stationNameToID = new Hashtable<String, Integer>();
			stationIDToDbID = new Hashtable<Integer, Integer>();
			createStations();
			createConnections();
			createBorderStations();
			System.out.println("Database init OK!");
		} catch (Exception e) {
			log.printAndLogE("An error occured when initializing the database: " + e);
		}
	}

	/**
	 * get the id of a station
	 * 
	 * @param name
	 *            station searched for
	 * @return the stationID if found, otherwise -1
	 */
	public int getStationID(String name) {
		return (stationNameToID.containsKey(name.toLowerCase()) ? stationNameToID.get(name.toLowerCase()) : -1);
	}

	/**
	 * return true or false depending on whether or not the exists in the database
	 * 
	 * @param stationName
	 * @return true if stations exists, else false
	 */
	public boolean stationExists(String stationName) {
		return (getStationID(stationName) != -1 ? true : false);
	}

	public int getDbID(int stationID) {
		return (stationIDToDbID.containsKey(stationID) ? stationIDToDbID.get(stationID) : -1);
	}

	/**
	 * create cached stations in memory
	 */
	public void createStations() {

		try {

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM station ORDER BY stationid ASC",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = pstmt.executeQuery();
			int i = 0;
			int rc = rowCount(rs);
			stations = new Station[rc];

			// map data
			stationsMapData = new SerializableStation[rc];

			while (rs.next()) {

				stations[i] = new Station(rs.getInt("long"), rs.getInt("lat"), rs.getString("name"),
						rs.getInt("stationid"), i);

				// add to map data
				stationsMapData[i] = stations[i].serStation;

				// add to hashmap for when stationID is needed, but only name
				// known
				stationNameToID.put(rs.getString("name").toLowerCase(), rs.getInt("stationid"));

				// add to id translation hashtable
				stationIDToDbID.put(rs.getInt("stationid"), i);

				i++;
			}
			rs.close();

		} catch (SQLException e) {
			log.printAndLog("Exception " + e);
		}
	}

	/**
	 * create cached connections in memory
	 */
	public void createConnections() {

		try {

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM connection ORDER BY stationid asc",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = pstmt.executeQuery();

			int rc = rowCount(rs);
			int i = 0, n = 0;
			connectionsMapData = new SerializableStation[rc / 2][2];

			while (rs.next()) {

				// System.out.print(rs.getInt("tostationid") + " => ");
				// System.out.println(getStation(rs.getInt("tostationid")));

				int cost = rs.getInt("cost");
				int price = rs.getInt("price");
				Neighbor neighbor = new Neighbor(getStation(rs.getInt("tostationid")), cost, price);

				Station s = getStation(rs.getInt("stationid"));

				s.neighbors.add(neighbor);

				// add to map data
				if (i % 2 == 0) {
					connectionsMapData[n][0] = getStation(rs.getInt("stationid")).serStation;
					connectionsMapData[n][1] = getStation(rs.getInt("tostationid")).serStation;
					n++;
				}

				i++;

			}
			rs.close();

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * create cached borderstations in memory
	 */
	public void createBorderStations() {
		try {

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM borderstation ORDER BY region ASC",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			ResultSet rs = pstmt.executeQuery();
			borderStations = new ArrayList<Station>();

			int i = 0;
			while (rs.next()) {
				int sid = rs.getInt("stationid");
				Station s = getStation(sid);

				// add regions
				int region = rs.getInt("region");
				s.region.add(region);
				// region into serializable station; separate by comma
				s.serStation.region += ((i++ > 0) ? "," : "") + region;

				// if station located at previous position in borderstations
				// array, do not add
				if (!borderStations.contains(s))
					borderStations.add(s);
			}
			rs.close();

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * find the id to add the station at
	 * 
	 * @param stationID
	 * @return
	 */
	// public int addStationAt(int stationID) {
	// return getDbID(stationID);
	// }

	/**
	 * get the neighbors to a sepcific station
	 * 
	 * @param stationID
	 *            the id of a station
	 * @return ArrayList containing the neighbors as Neighbor objects
	 */
	public ArrayList<Neighbor> getNeighbors(int stationID) {
		return stations[getDbID(stationID)].neighbors;
	}

	/**
	 * get station with a specific id
	 * 
	 * @param stationID
	 *            the if of a station
	 * @return the station as a Station object
	 */
	public Station getStation(int stationID) {
		return stations[getDbID(stationID)];
	}

	/**
	 * count the number of returned rows in a resultset
	 * 
	 * @param rs
	 *            the resultset
	 * @return number of rows returned
	 */
	public int rowCount(ResultSet rs) {
		int numRows = 0;
		try {
			rs.last();
			numRows = rs.getRow();
			rs.beforeFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRows;
	}

	/**
	 * get the cached stations
	 * 
	 * @return array of stations
	 */
	public Station[] getStations() {
		return stations;
	}

	/**
	 * get number of borderstations
	 * 
	 * @return number of borderstations
	 */
	public int borderStationsCount() {
		return borderStations.size();
	}

	/**
	 * get cached borderstations
	 * 
	 * @return ArrayList of borderstations
	 */
	public ArrayList<Station> getBorderStations() {
		return borderStations;
	}

	/**
	 * get number of stations
	 * 
	 * @return number of stations
	 */
	public int stationsCount() {
		return stations.length;
	}

	/**
	 * load stations from array to database
	 * 
	 * @param stations
	 *            the array of stations
	 * @return true or false depending of success
	 */
	public boolean loadStations(Station[] stations) {

		// wipe station table
		wipe(1);

		System.out.println("Inserting stations...");

		String q = "", query;
		// int newStationID;
		// int[] stationIDs = { 999001, 999002, 999008, 999009, 999006, 999007 };
		for (int i = 0; i < stations.length; i++) {

			// if borderstation, use the common stationID
			// newStationID = (stations[i].stationid > 1000) ? stationIDs[stations[i].stationid - 1000]
			// : stations[i].stationid;

			// concatenate insertion string separated by comma
			q += (i > 0 ? ", " : "") + "('" + (stations[i].stationid + (stations[i].stationid < 999000 ? 1 : 0))
					+ "', '" + stations[i].stationName + "', '" + stations[i].latitude + "', '" + stations[i].longitude
					+ "')";
			// separate by comma
			// q += (i < stations.length - 1 ? ", " : "");
		}

		// System.out.println("Stations in mapdata: " + stationsMapData.length);

		try {
			query = "INSERT INTO station VALUES " + q;
			// System.out.println("Query string: \n" + query);
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.execute();
			System.out.println("Stations inserted");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * load connections from array to database
	 * 
	 * @param connections
	 *            the array of connections
	 * @return true or false depending of success
	 */
	public boolean loadConnections(ArrayList<Station[]> connections) {

		// wipe connection table
		wipe(2);

		System.out.println("Inserting connections...");
		String q = "", query;
		int sidFrom, sidTo;
		for (int i = 0; i < connections.size(); i++) {
			// concatenate insertion string
			sidFrom = connections.get(i)[0].stationid;
			sidTo = connections.get(i)[1].stationid;
			q += "('" + (sidFrom + (sidFrom < 999000 ? 1 : 0)) + "', '" + (sidTo + (sidTo < 999000 ? 1 : 0)) + "', '"
					+ 1 + "', '" + 1 + "'), ";
			q += "('" + (sidTo + (sidTo < 999000 ? 1 : 0)) + "', '" + (sidFrom + (sidFrom < 999000 ? 1 : 0)) + "', '"
					+ 1 + "', '" + 1 + "')";
			// separate by comma
			q += (i < connections.size() - 1 ? ", " : "");
		}

		// System.out.println("Connections in mapdata: " +
		// connectionsMapData.length);

		try {
			query = "INSERT INTO connection VALUES " + q;
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.execute();
			System.out.println("Connections inserted");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * wipe the table as preparation to insertion of new stations, connections and broderstations
	 * 
	 * @param type
	 *            the type of the table to wipe
	 */
	public void wipe(int type) {
		System.out.println("Wiping table!");

		try {
			PreparedStatement pstmt;
			switch (type) {
			case 1: // station
				pstmt = conn.prepareStatement("TRUNCATE TABLE station");
				pstmt.execute();
				System.out.println("Table wiped: 'station' table");
				break;
			case 2: // connection
				pstmt = conn.prepareStatement("TRUNCATE TABLE connection");
				pstmt.execute();
				System.out.println("Table wiped: 'connection' table");
				break;
			case 3: // borderstation
				pstmt = conn.prepareStatement("TRUNCATE TABLE borderstation");
				pstmt.execute();
				System.out.println("Table wiped: 'borderstation' table");
				break;
			case 4: // nodeList
				pstmt = conn.prepareStatement("TRUNCATE TABLE nodelist");
				pstmt.execute();
				System.out.println("Table wiped: 'nodelist' table");
				break;
			default:
				System.out.println("Could not whipe table: Unknown table!");
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get stations map data for client map
	 * 
	 * @return array containing every station in the map
	 */
	public SerializableStation[] getStationMapData() {
		return stationsMapData;
	}

	/**
	 * get connection map data for client map
	 * 
	 * @return multidimentional array containing every connection in the map
	 */
	public SerializableStation[][] getConnectionMapData() {
		return connectionsMapData;
	}

	public void saveNodeList(List<Node> nodeList) {
		// TODO save the nodeList to the database for use for login to P2P after
		// shutdown
		String q = "";

		for (int i = 0; i < nodeList.size(); i++) {

			// concatenate insertion string
			q += "('" + (nodeList.get(i).ip) + "', '" + (nodeList.get(i).udpPort) + "')";
			// separate by comma
			q += (i < nodeList.size() - 1 ? ", " : "");
		}
		System.out.println("Insert nodeList with String: " + q);
		try {
			String query = "";
			if (q != "") {
				query = "TRUNCATE TABLE nodelist; INSERT INTO nodelist VALUES " + q;
			} else
				query = "TRUNCATE TABLE nodelist";
			System.out.println(query);
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.execute();
			System.out.println("NodeList inserted into DB");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Node> getNodelist() {
		// TODO get nodeList from DB, so login can be done to all on list until
		// login is completed, when receiving Update from the login in node
		// within 2 min.
		LinkedList<Node> nodeList = new LinkedList<Node>();
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM nodelist ORDER BY ip ASC");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out
						.println("test af nodeList from DB:\nIP " + rs.getString("ip") + " Port " + rs.getInt("port"));
				nodeList.add(new Node(rs.getString("ip"), rs.getInt("port")));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nodeList;
	}
}
