package common;

import java.io.Serializable;

public class MapData implements Serializable{
	
	private static final long serialVersionUID = 1795339251867378115L;
	
	public SerializableStation stations[], connections[][];
	
	public void setData(SerializableStation stations[], SerializableStation connections[][]) {
		this.stations = stations;
		this.connections = connections;
	}
	
	public String toString() {
		return "'stations/connections'";
	}
}