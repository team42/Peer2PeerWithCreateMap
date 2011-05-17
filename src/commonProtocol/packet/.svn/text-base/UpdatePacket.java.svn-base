package commonProtocol.packet;

import java.util.LinkedList;
import java.util.List;

import commonProtocol.Node;

/**
 * @author 
 * 
 */
public class UpdatePacket extends CPPacket {

	public List<Node> nodeList = new LinkedList<Node>();

	public UpdatePacket(List<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public UpdatePacket(String msg) {
		String[] rows;
		String command;
		String[] portIP;
		String version;

		// Get commando and version from msg String
		rows = msg.split("\\#");
		command = rows[0].split("\\|")[0];
		version = rows[0].split("\\|")[1];
		for(int i = 1; i< rows.length; i++){
			String[] row = rows[i].split("\\|");
			nodeList.add(new Node(row[1], Integer.parseInt(row[0])));
		}
	}

	public List<Node> getNodes() {
		return nodeList;
	}

	// Create update packet from nodeList<Node>
	@Override
	public String generateCPPacket() {
		String packet = "update|1|";
		for (Node node : nodeList) {
			packet += "#" + Math.abs(node.udpPort) + "|" + node.ip;
		}
		return packet;
	}
}
