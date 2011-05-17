package common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Team2
 */
public class Route implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5636853355667389603L;
	public String fromStation, toStation;
	public double cost = 0;
	public int price = 0;
	public long calcTimeNano = 0;

	public ArrayList<SerializableStation> route = new ArrayList<SerializableStation>();

	public Route() {
	}

	public Route(String fromStation, String toStation) {
		this.fromStation = fromStation;
		this.toStation = toStation;
	}

	public ArrayList<SerializableStation> getRoute() {
		return route;
	}

	public String toString() {
		String out = "From: " + fromStation + "\nTo: " + toStation + "\nCost: " + cost + "\nHops: " + route.size()
				+ "\nCalcTimeNano: " + calcTimeNano + "\n------------------\n";
		int i = 1;
		for (SerializableStation s : route) {
			out += i++ + ". " + s.stationName + "\n";
		}
		return out;
	}

}