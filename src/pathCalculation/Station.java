package pathCalculation;

import java.util.ArrayList;

import common.SerializableStation;

public class Station {

	public int stationid, dbid = -1;
	public double longitude, latitude;
	public String stationName;
	public SerializableStation serStation;

	public ArrayList<Neighbor> neighbors = new ArrayList<Neighbor>();
	public ArrayList<Integer> region = new ArrayList<Integer>();

	/**
	 * 
	 * @param longitude
	 * @param latitude
	 * @param stationName
	 * @param stationid
	 */
	public Station(double longitude, double latitude, String stationName, int stationid) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.stationName = stationName;
		this.stationid = stationid;
		this.serStation = new SerializableStation(longitude, latitude, stationName, stationid);
	}

	/**
	 * 
	 * @param longitude
	 * @param latitude
	 * @param stationName
	 * @param stationid
	 * @param dbid
	 */
	public Station(double longitude, double latitude, String stationName, int stationid, int dbid) {
		this(longitude, latitude, stationName, stationid);
		this.dbid = dbid;
	}

	/**
	 * Constructor that generates station object from {@link SerializableStation}
	 * 
	 * @param serializable
	 */
	public Station(SerializableStation serializable) {
		this.longitude = serializable.longitude;
		this.longitude = serializable.longitude;
		this.stationName = serializable.stationName;
		this.stationid = serializable.stationid;
	}

	/**
	 * Constructor used for storing stations only by name and number (common protocol)
	 * 
	 * @param stationName
	 * @param stationid
	 */
	public Station(String stationName, int stationid) {
		this.stationName = stationName;
		this.stationid = stationid;
		this.serStation = new SerializableStation(0, 0, stationName, stationid);
	}

	@Override
	public String toString() {
		return stationName + " [longitude=" + longitude + ", latitude=" + latitude + ", stationid=" + stationid
				+ ", stationName=" + stationName + ", neighbors=!]";
		// return stationName + " [neighbors=" + neighborString() + "]";
	}

	public String neighborString() {
		int i = 0;
		String s = "";
		for (Neighbor n : neighbors)
			s += ((i++ > 0) ? ", " : "") + "\t" + n.toString() + "\n";
		return s + "";
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public boolean equals(Object s) {
		if (this.stationid == ((Station) s).stationid) {
			return true;
		}
		return false;
	}

}
