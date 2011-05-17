package commonProtocol.packet;

import java.util.ArrayList;
import java.util.StringTokenizer;

import common.*;

public class StationRespondPacket extends CPPacket {

	public int regionID, sessionID;
	public boolean regional = false;
	public ArrayList<Route> routes = new ArrayList<Route>();
	public String packetString;
	
	/**
	 * Constructor that parses the inputstring and fills out the packets fields.
	 * @param inputString The string to Parse
	 * @throws Exception The parser will throw an error if there is problems with the input data.
	 */
	public StationRespondPacket(String inputString) throws Exception {
		
		formatInputString(inputString);
	}

	/**
	 * This constructor will generate a string that can be sent to another
	 * regional server as a response for a route object.
	 * 
	 * @param regionID
	 *            Our regionID
	 * @param sessionID
	 *            The SessionID expected by the other side
	 * @param regional
	 *            True for regional, false for interregional
	 * @param routes
	 *            An arraylist of Routes that is encoded into the string
	 */
	public StationRespondPacket(int regionID, int sessionID, boolean regional,
			ArrayList<Route> routes) {
		super();
		this.regionID = regionID;
		this.sessionID = sessionID;
		this.regional = regional;
		this.routes = routes;
	}

	/**
	 * Generates a string for this command that can be sent to another server.
	 * The variables of the class MUST be filled out before this function can generate the string
	 */
	@Override
	public String generateCPPacket() {
		String tempStr = "response|"; // Command text
		tempStr += "1|"; // Version field
		tempStr += "region"+ regionID +"|"; // Region field		
		tempStr += (regional) ? "regional|" : "interregional|"; // Regional/interRegional
		tempStr += sessionID + "|"; // Session ID

		// Route data loop
		// For each route in the routes array
		String stationNames, stationIDs;
		for (Route thisRoute : routes) {
			// reset strings for each route
			stationIDs = "#";
			stationNames = "";
			// Concatinate each stationname and ID in the current route to a
			// string
			for (SerializableStation thisStation : thisRoute.route) {
				stationIDs += thisStation.stationid + "%";
				stationNames += thisStation.stationName + "%";
			}
			// remove the extra % char
			stationIDs = stationIDs.substring(0, stationIDs.length() - 1);
			stationNames = stationNames.substring(0, stationNames.length() - 1);

			// Add Data to the complete string
			tempStr += stationIDs + "|"; // Add IDs
			tempStr += stationNames + "|"; //add Names
			tempStr += (int) thisRoute.price + "|"; //Add total distance
			tempStr += (int) thisRoute.cost + "|"; //add total price
		}
		tempStr += "\r\n"; // CR LF - Termination

		//return the complete string
		return tempStr;

	}

	/**
	 * This method will format the received string. It is called b y the
	 * constructor
	 * 
	 * @throws Exception
	 *             Any error that occurs when formatting
	 */
	public void formatInputString(String inputString ) throws Exception {
		// Temporary fields.
		String tempString;
		SerializableStation tempStation;

		// Create a token array of the responseString
		StringTokenizer workArray = new StringTokenizer(inputString, "|");

		// First string is the command
		command = workArray.nextToken();

		// Next String is version
		workArray.nextToken();
		// Version not in use though

		// Next String is region
		// Data should be "regionx" remove region string and parse x as integer
		regionID = Integer
				.parseInt(workArray.nextToken().toLowerCase().replace("region", ""));

		// Next String is regional flag
		if (workArray.nextToken().toLowerCase().equals("regional"))
			regional = true;// workArray.toString()

		// Next String is session ID
		sessionID = Integer.parseInt(workArray.nextToken());

		// ROUTE DATA LOOP BEGINS HERE
		// Each loop in the data will contain 4 tokens. So if there are more
		// than 4 tokens left we have another data loop
		while (workArray.countTokens() > 4) {

			// make a new route object that will be filled with data
			Route thisRoute = new Route();

			// Next token holds the station IDs
			// If the data is formatted correctly it should start with a #.
			tempString = workArray.nextToken();
			if (!tempString.startsWith("#")) {
				throw (Exception) new Exception(
						"Response data recieved, but illegal startchar in the data loop");
			}

			// Remove special char
			tempString = tempString.replace("#", "");

			// Save the station IDs to a token array
			StringTokenizer stationIDs = new StringTokenizer(tempString, "%");

			// Next token holds a list of station names
			// split the station names
			StringTokenizer stationNames = new StringTokenizer(
					workArray.nextToken(), "%");

			// check if the two arrays are the same size
			if (stationIDs.countTokens() != stationNames.countTokens()) {
				throw (Exception) new Exception(
						"Error formatting the response data. StationID count differs from StationName count");
			}

			// Save each token as a station object in the route
			while (stationNames.hasMoreTokens()) {
				tempStation = new SerializableStation(0, 0,
						stationNames.nextToken(), Integer.parseInt(stationIDs
								.nextToken()));
				tempStation.region = "" + this.regionID;
				thisRoute.route.add(tempStation);
			}
			// Get first station, save it in route Header
			thisRoute.fromStation = thisRoute.route.get(0).stationName;
			// Get the last station, save it in route Header
			thisRoute.toStation = thisRoute.route
					.get(thisRoute.route.size() - 1).stationName;

			// Next token the distance
			thisRoute.price = Integer.parseInt(workArray.nextToken());

			// Next token is the price
			thisRoute.cost = Double.parseDouble(workArray.nextToken());

			routes.add(thisRoute);
			// Debug
			// System.out.println(thisRoute.toString());
		}

		// the last token is \CR\LF skip it
		workArray.nextToken();

	}
}
