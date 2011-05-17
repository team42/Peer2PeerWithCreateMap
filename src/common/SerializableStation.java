package common;

import java.io.Serializable;

import pathCalculation.Station;

public class SerializableStation implements Serializable {

	private static final long serialVersionUID = 165878638892941890L;

	public int stationid;
	public double longitude, latitude, cost = 0;
	public String stationName, region;

	/**
	 * 
	 * @param longitude
	 * @param latitude
	 * @param stationName
	 * @param stationid
	 */
	public SerializableStation(double longitude, double latitude, String stationName, int stationid) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.stationName = stationName;
		this.stationid = stationid;
	}

	public SerializableStation(Station station) {
		this.longitude = station.longitude;
		this.latitude = station.latitude;
		this.stationName = station.stationName;
		this.stationid = station.stationid;
		if (station.region != null)
			this.region = station.region.get(0).toString();
	}

}
