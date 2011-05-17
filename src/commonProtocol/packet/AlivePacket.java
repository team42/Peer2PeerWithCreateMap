package commonProtocol.packet;

import commonProtocol.Node;

import internals.LocalRegionalServer;

public class AlivePacket extends CPPacket {

	public int regionID;
	public long unixTimeStamp;
	public String ip;
	public int tcpPort;
	public int udpPort;

	public AlivePacket(String command, int version, int regionID,
			long unixTimeStamp, int tcpPort, int udpPort, String ipAddress) {
		super();
		this.command = command;
		this.version = version;
		this.regionID = regionID;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.unixTimeStamp = unixTimeStamp;
	}

	public Node toNode() {
		return new Node(this.ip, this.regionID, this.unixTimeStamp,
				this.tcpPort, this.udpPort);
	}

	public AlivePacket() {
		this.command = "alive";
		this.version = 1;
		this.regionID = LocalRegionalServer.regionNumber;
		this.tcpPort = LocalRegionalServer.commonProtoTCPPort;
		this.udpPort = LocalRegionalServer.commonProtoUDPPort;
		this.unixTimeStamp = LocalRegionalServer.aliveUnixTimeStamp;
	}

	public AlivePacket(String input, String senderIP) throws Exception {
		String[] content = input.split("\\|");
		this.command = content[0];
		this.version = 1;
		this.regionID = Integer.parseInt(content[2].replace("region", "")); 
		this.unixTimeStamp = Integer.parseInt(content[3]);
		this.tcpPort = Integer.parseInt(content[4]);
		this.udpPort = Integer.parseInt(content[5]);
		this.ip = senderIP;
	}

	@Override
	public String generateCPPacket() {
		String result = command + "|" + version + "|" + "region" + regionID + "|"
				+ unixTimeStamp + "|" + tcpPort + "|" + udpPort;
		return result;

	}
}
