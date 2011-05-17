package internals;

import java.util.ArrayList;

import common.SerializableStation;

public class BSData {

	public int regionID;
	public int fromRegion;
	public int toRegion;
	public ArrayList<SerializableStation> stations; // the route
	public double totalDistance;
	public double totalPrice;
	public long unixTimeStamp;

	public BSData(int regionID, int fromRegion, int toRegion,
			ArrayList<SerializableStation> stations, double totalDistance,
			double totalPrice) {
		super();
		this.regionID = regionID;
		this.fromRegion = fromRegion;
		this.toRegion = toRegion;
		this.stations = stations;
		this.totalDistance = totalDistance;
		this.totalPrice = totalPrice;
		this.unixTimeStamp = System.currentTimeMillis();
	}

	@Override
	public String toString() {

		int i = 0;
		String stationString = "[";
		for (SerializableStation s : stations)
			stationString += ((i++ > 0) ? ", " : "") + s.stationName;
		stationString += "]";

		return "BSData [regionID=" + regionID + ", fromRegion=" + fromRegion
				+ ", toRegion=" + toRegion + ", stations=" + stationString
				+ ", totalDistance=" + totalDistance + ", totalPrice="
				+ totalPrice + "]";
	}

	public String getStationNamesCPString() {
		String result = "";
		for (int i = 0; i < stations.size(); i++) {
			result += stations.get(i).stationName;
			if (i < stations.size() - 1)
				result += "%";
		}
		return result;
	}

	public String getStationsIDsCPString() {
		String result = "";
		for (int i = 0; i < stations.size(); i++) {
			result += stations.get(i).stationid;
			if (i < stations.size() - 1)
				result += "%";
		}
		return result;
	}
}
