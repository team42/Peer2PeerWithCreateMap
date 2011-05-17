package commonProtocol.packet;

import java.util.StringTokenizer;

public class StationRequestPacket extends CPPacket {

	public int version = 1;
	public int regionID;
	public int sessionID;
	public String fromStationName;
	public String toStationName;
	public String searchCriteria;

	/**
	 * Constructor that parses the inputstring and fills out the packets class
	 * fields.
	 * 
	 * @param inputString
	 *            The string to Parse
	 * @throws Exception
	 *             The parser will throw an error if there is problems with the
	 *             input data.
	 */
	public StationRequestPacket(String inputString) throws Exception {
		// Create a token array of the responseString
		StringTokenizer workArray = new StringTokenizer(inputString, "|");

		// The packet must have 7 fields.
		if (workArray.countTokens() != 7) {
			throw (Exception) new Exception(
					"The request packet doesnt have enough fields to be parsed");
		}

		// First string is the command
		command = workArray.nextToken();

		// Next String is version
		workArray.nextToken();
		// Version not in use though

		// Next String is region
		// Data should be "regionx" remove region string and parse x as integer
		regionID = Integer.parseInt(workArray.nextToken().toLowerCase()
				.replace("region", ""));

		// Next String is session ID
		sessionID = Integer.parseInt(workArray.nextToken());

		// The from station
		fromStationName = workArray.nextToken();

		// The to Station
		toStationName = workArray.nextToken();

		// The Search criteria
		searchCriteria = workArray.nextToken();

	}

	/**
	 * This constructor is used to fill all the fields when we want to generate
	 * a CP packet
	 * 
	 * @param regionID
	 *            Our region ID
	 * @param sessionID
	 *            The Session ID
	 * @param fromStationName
	 *            The from station name
	 * @param toStationName
	 *            The destination station name
	 * @param searchCriteria
	 *            The searchCriteria. Must be either "shortest" or "cheapest".
	 */
	public StationRequestPacket(int regionID, int sessionID,
			String fromStationName, String toStationName, String searchCriteria) {
		super();
		this.regionID = regionID;
		this.sessionID = sessionID;
		this.fromStationName = fromStationName;
		this.toStationName = toStationName;
		this.searchCriteria = searchCriteria.toLowerCase();
	}

	/**
	 * Generates a string that can be sent over the Common Protocol.
	 */
	@Override
	public String generateCPPacket() {
		String tempStr = "request|"; // Command text
		tempStr += "1|"; // Version field
		tempStr += "region"+ regionID +"|"; // Region field		
		tempStr += sessionID + "|"; // Session I
		tempStr += fromStationName + "|"; // fromStationName field
		tempStr += toStationName + "|"; // toStationName field		
		tempStr += searchCriteria; // searchCriteria field

		//return the complete string
		return tempStr;

	}
}
