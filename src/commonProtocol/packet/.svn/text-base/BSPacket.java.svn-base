package commonProtocol.packet;

import java.util.ArrayList;
import internals.*;
import common.*;

public class BSPacket extends CPPacket {

	public int regionID;
	public ArrayList<BSData> bsDataArray = new ArrayList<BSData>();
	public String packetAsString = "";
	
	/**
	 * Empty constructor
	 */
	public BSPacket(){
	}

	/**
	 * Constructor that takes BSData array as input
	 * @param bsDataArray
	 */
	public BSPacket(ArrayList<BSData> bsDataArray) {
		super();
		this.bsDataArray = bsDataArray;
	}

	/**
	 * Magic constructor that creates BSPacket. BSPacket data is stored in
	 * BSData ArrayList
	 * 
	 * @param input
	 * @throws Exception
	 */
	public BSPacket(String input) throws Exception {
		this.command = "bsdata";
		this.version = 1;
		String[] rowArray = input.split("\\#");
		String[] col = rowArray[0].split("\\|");
		this.regionID = Integer.parseInt(col[2].replace("region", "")); 
		//this.regionID = Integer.parseInt(col[2].substring(6));
		for (int i = 1; i < rowArray.length; i++) {
			// extract row data
			col = rowArray[i].split("\\|");
			int fromRegion = Integer.parseInt(col[0]);
			int toRegion = Integer.parseInt(col[1]);
			String stationID = col[2];
			String stationName = col[3];
			double totalDistance = Integer.parseInt(col[4]);
			double totalPrice = Integer.parseInt(col[5]);
			String[] stationIDs = stationID.split("\\%");
			String[] stationNames = stationName.split("\\%");
			// create stations arraylist
			ArrayList<SerializableStation> stations = new ArrayList<SerializableStation>();
			for (int k = 0; k < stationNames.length; k++)
				stations.add(new SerializableStation(0, 0, stationNames[k], Integer.parseInt(stationIDs[k])));
			bsDataArray.add(new BSData(regionID, fromRegion, toRegion,
					stations, totalDistance, totalPrice));
			// Just for testing:
			LocalRegionalServer.localBsDataArray = bsDataArray;
		}
	}

	/**
	 * Generates packet from BSData array in this object
	 * @return
	 */
	public String generateThisPacket(){
			String result = "bsdata|1|" + "region" + this.regionID + "|";
			for (int i = 0; i < this.bsDataArray.size(); i++) {
				BSData bs = this.bsDataArray.get(i);
				result += "#" + bs.fromRegion + "|" + bs.toRegion + "|"
						+ bs.getStationsIDsCPString() + "|"
						+ bs.getStationNamesCPString() + "|"
						+ (int) bs.totalDistance + "|"
						+ (int) bs.totalPrice + "|";
			}
			packetAsString = result;
			return result;
	}
	
	/**
	 * Generates Packet from BSData array in localRegionalServer
	 */
	@Override
	public String generateCPPacket() {
		String result = "bsdata|1|" + "region" + LocalRegionalServer.regionNumber + "|";
		for (int i = 0; i < LocalRegionalServer.localBsDataArray.size(); i++) {
			BSData bs = LocalRegionalServer.localBsDataArray.get(i);
			result += "#" + bs.fromRegion + "|" + bs.toRegion + "|"
					+ bs.getStationsIDsCPString() + "|"
					+ bs.getStationNamesCPString() + "|"
					+ (int) bs.totalDistance + "|"
					+ (int) bs.totalPrice + "|";
		}
		packetAsString = result;
		return result;
	}
}
