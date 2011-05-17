package commonProtocol;

public class Node {

	public int regionID = 0;
	public long unixTimeStamp = 0;
	public String ip;
	public int tcpPort = 0;
	public int udpPort = 0;
	public long aliveTimeStamp = 0;

	public Node(String ip, int regionID, long unixTimeStamp, int tcpPort,
			int udpPort) {
		this.ip = ip;
		this.regionID = regionID;
		this.unixTimeStamp = unixTimeStamp;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.aliveTimeStamp = System.currentTimeMillis();
	}

	public Node(String ip, int port) {
		this.ip = ip;
		this.udpPort = port;
		// auto fill not known values;
		this.regionID = 0;
		this.unixTimeStamp = 0;
		this.tcpPort = 0;
		this.aliveTimeStamp = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "Node [regionID=" + regionID + ", unixTimeStamp="
				+ unixTimeStamp + ", ip=" + ip + ", tcpPort=" + tcpPort
				+ ", udpPort=" + udpPort + ", aliveTimeStamp=" + aliveTimeStamp
				+ "]";
	}

	
	@Override
	public boolean equals(Object obj) {
		
		if(this.ip.equals(((Node) obj).ip) && this.udpPort == ((Node) obj).udpPort)  {
			return true;
		}
		return false;
	}

	
	
	
	

}
