package commonProtocol;

import internals.Database;
import internals.LocalRegionalServer;
import internals.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import commonProtocol.packet.AlivePacket;
import commonProtocol.packet.LoginPacket;
import commonProtocol.packet.UpdatePacket;

/**
 * Contains nodelist for common protocol Can contain
 * 
 * @author
 * 
 */
public class NodeList {

	Database db = Database.getInstance();
	Log log = Log.getInstance();

	// CommonProtoTCP commonProtoTCP = LocalRegionalServer.commonProtoTCP;
	// CommonProtoUDP commonProtoUDP = LocalRegionalServer.commonProtoUDP;

	// holds references to objects for which we know the region
	public Node[] nodeArray = new Node[50];

	// NodeLinkList holds objects for which we do not know the region
	public List<Node> nodeLinkList = Collections.synchronizedList(new LinkedList<Node>());

	// Singleton:
	private static NodeList instance = new NodeList();

	public static NodeList getInstance() {
		return instance;

	}
	
	private NodeList() {

	}

	// synchronized public void updateNode(Node node) {
	// // if region is unknown (update packet)
	//
	// }

	synchronized public void updateLoginPacket(LoginPacket loginPacket) {
		Node loginNode = new Node(loginPacket.ip, loginPacket.port);
		if (traverseLinkList(loginNode) != null) {
			// Node does exist in nodelist, reply with update to sender
			LocalRegionalServer.commonProtoUDP.sendUpdateToNode(loginNode);
		} else {
			// Node does not exist, add it
			nodeLinkList.add(loginNode);
			db.saveNodeList(nodeLinkList);
			LocalRegionalServer.commonProtoUDP.sendUpdateToAllNodes();
		}

	}

	/**
	 * Removes a node if it exists with that IP address UDP port. Could be done with iterator instead, in future
	 * versions
	 * 
	 * @param ip
	 * @param port
	 */
	synchronized public void removeNode(String ip, int port) {
		Node dummyNode = new Node(ip, port);
		ListIterator<Node> itr = nodeLinkList.listIterator();
		while (itr.hasNext()) {
			Node itrNode = itr.next();
			if (itrNode.equals(dummyNode)) {
				log.printAndLog("Removing node: " + itrNode);
				if(itrNode.regionID!=0)
					nodeArray[itrNode.regionID] = null;
				itrNode = null;
				itr.remove();
			}
		}
	}

	/**
	 * Removes a node if it exists with index.
	 * 
	 */
	synchronized public void removeNode(int index) {
		Node linkListNode = nodeLinkList.get(index);
		nodeArray[linkListNode.regionID] = null;
		nodeLinkList.remove(index);
	}

	synchronized public void clear() {
		nodeLinkList.clear();
	}

	synchronized public void updateNode(AlivePacket aP) {
		// Update nodelist with each object from the nodelist
		Node node = new Node(aP.ip, aP.regionID, aP.unixTimeStamp, aP.tcpPort, aP.udpPort);

		// node not found in array:
		if (nodeArray[node.regionID] == null) {
			Node traverseNode = traverseLinkList(node);
			// node not found in linklist:
			if (traverseNode == null) {
				log.printAndLogE("Alive packet received, but node not found in nodelist, node added. This should not happen: "
						+ node);
				LocalRegionalServer.commonProtoTCP.sendBSRequest(node);
				nodeLinkList.add(node);
				nodeArray[node.regionID] = node;
				nodeListUpdated();
			}
			// node found in linklist
			else {// (traverseNode != null)
				LocalRegionalServer.commonProtoTCP.sendBSRequest(node);

				// Update the node in the nodelist
				traverseNode.ip = node.ip;
				traverseNode.regionID = node.regionID;
				traverseNode.tcpPort = node.tcpPort;
				traverseNode.udpPort = node.udpPort;
				traverseNode.unixTimeStamp = node.unixTimeStamp;
				traverseNode.aliveTimeStamp = node.aliveTimeStamp;
				// add reference in array
				nodeArray[traverseNode.regionID] = traverseNode;
			}
		} else { // node was found in array, update it
			if (nodeArray[node.regionID].unixTimeStamp < node.unixTimeStamp) {
				log.log("Newer timestamp received, found in array, send bs request to node :" + node);
				LocalRegionalServer.commonProtoTCP.sendBSRequest(node);
			}
			// Update the node
			nodeArray[node.regionID].ip = node.ip;
			nodeArray[node.regionID].regionID = node.regionID;
			nodeArray[node.regionID].tcpPort = node.tcpPort;
			nodeArray[node.regionID].udpPort = node.udpPort;
			nodeArray[node.regionID].unixTimeStamp = node.unixTimeStamp;
			log.log("Node updated in nodelist (Alive packet): " + node + "Old timestamp: " + nodeArray[node.regionID].aliveTimeStamp + " New :"
					+ node.aliveTimeStamp);
			nodeArray[node.regionID].aliveTimeStamp = node.aliveTimeStamp;
		}
	}

	/**
	 * Updates the nodelist with elements that does not already exist.
	 * 
	 * @param updatePacket
	 */
	synchronized public void updateNodes(UpdatePacket updatePacket) {
		// Update nodelist with each object from the nodelist
		List<Node> updateNodes = updatePacket.nodeList;
		boolean nodeListHasBeenUpdated = false;
		for (Node node : updateNodes) {
			Node traverseNode = traverseLinkList(node);
			if (traverseNode == null) {
				nodeLinkList.add(node);
				nodeListHasBeenUpdated = true;
			}
		}
		if (nodeListHasBeenUpdated)
			Database.getInstance().saveNodeList(getNodes());
	}

	/**
	 * Traverses the linklist and returns the object if ip address and udp port is the same as inputnode. This method is
	 * used to bind p2pnodes with alive nodes (p2p nodes are received from update commands, and alive nodes are received
	 * from alive packets)
	 * 
	 * @param inPutNode
	 * @return
	 */

	public Node traverseLinkList(Node inPutNode) {
		ListIterator<Node> itr = nodeLinkList.listIterator();
		while (itr.hasNext()) {
			Node itrNode = itr.next();
			if (itrNode.equals(inPutNode)) {
				return itrNode;
			}
		}
		return null;
	}


	/**
	 * What to do when nodelist has been updated
	 */
	synchronized private void nodeListUpdated() {
		try {
			LocalRegionalServer.commonProtoUDP.sendUpdateToAllNodes();
			db.saveNodeList(nodeLinkList);
		} catch (Exception e) {
			log.printAndLogE("Error when trying to send update to all nodes");
			e.printStackTrace();
		}
	}

	/**
	 * returns true if no nodes are in the list. If nodeLinkList is empty, node array should also be empty
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return nodeLinkList.isEmpty();
	}

	/**
	 * Returns linklist with Nodes that have alive data (regionID, unixTimeStamp, tcpPort, aliveTimeStamp) (methods
	 * checks for tcpPort != 0)
	 * 
	 * @return
	 */
	public LinkedList<Node> getAliveNodes() {
		LinkedList<Node> aliveNodes = new LinkedList<Node>();
		ListIterator<Node> itr = nodeLinkList.listIterator();
		while (itr.hasNext()) {
			Node currentNode = itr.next();
			if (currentNode.tcpPort != 0)
				aliveNodes.add(currentNode);
		}
		return aliveNodes;
	}

	// public LinkedList<Node> getAliveNodes() {
	//
	// LinkedList<Node> aliveNodes = new LinkedList<Node>();
	// for (Node currentNode : nodeLinkList)
	// if (currentNode.tcpPort != 0)
	// aliveNodes.add(currentNode);
	// return aliveNodes;
	// }

	public List<Node> getNodes() {
		return nodeLinkList;
	}

	/**
	 * Returns linklist with Nodes that have alive data (regionID, unixTimeStamp, tcpPort, aliveTimeStamp)
	 * 
	 * @return
	 */
	public Node getRegionNode(int regionID) {
		if (nodeArray[regionID] != null) {
			return nodeArray[regionID];
		}
		return null;
	}
}
