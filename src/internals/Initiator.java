package internals;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import pathCalculation.AStar;
import pathCalculation.Station;

import common.Route;
import commonProtocol.Node;
import commonProtocol.NodeList;

public class Initiator {
	NodeList nodeList = NodeList.getInstance();
	Log log = Log.getInstance();
	Database db = Database.getInstance();
	
	private static Initiator instance = new Initiator();
	
	private Initiator(){
		
	}
	
	public static Initiator getInstance(){
		return instance;
	}
	
	/**
	 * Gets the ip address
	 */
	public String getIpAddress(){
		java.net.InetAddress inetAddress;
		String result = "";
		try {
			inetAddress = java.net.InetAddress.getLocalHost();
			result = inetAddress.getHostAddress();
			log.printAndLog("Server IP adress is: " + result);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.err.println("Error when trying to get server IP Address");
			return null;
		}
		return result;
	}
	
	// TODO Login to P2P network
	// Send login for each node in the nodelist stored in the database, and
	// waits for X seconds for an update from that node, if no update is
	// replied try next node in nodelist.
	
	public void login2p2p(){
		if (nodeList.isEmpty()) {
			System.err.println("Nodelist is empty, please login manually to a known server");
			Wait.milliSec(0);
		} else {
			log.printAndLog("Sending login, to all nodes on the nodeList from Database");
			try {
				for (Node node : nodeList.nodeLinkList) {
					LocalRegionalServer.commonProtoUDP.sendLogin(node);
					log.printAndLog("Send login command to: " + node.ip + ":" + node.udpPort
							+ " awaiting reply (Update)");
					// Waits for ~ 2000 ms
					for (int waitInt = 0; waitInt < 20; waitInt++) {
						System.out.print(".");
						Wait.milliSec(100);
						if (LocalRegionalServer.updateReceived)
							break;
					}
					if (LocalRegionalServer.updateReceived) {
						log.printAndLog("Update received, login to p2p network success");
						break;
					}
					log.printAndLog("No update received, try next node");
				}
			} catch (ConcurrentModificationException cme) {
				cme.printStackTrace();
			}
			if (!LocalRegionalServer.updateReceived)
				log.printAndLogE("No nodes in the nodelist responded to login, please login manually to a known server");
			Wait.milliSec(0);
		}
		
	}
	
	public void generateBSData(){
		// Gets the BS Data for both directions (a to b, and b to a)
		// if the two regions are the same, the data is ignored
		// efficiency is O(n^2) where n is the number of border stations
		// The number of routes outputted should be (n^2)-n
		try {
			log.printAndLog("Generating shortest path for all border stations (BS Data)");
			Wait.milliSec(0); // just for visual purposes
			ArrayList<Station> bsStations = new ArrayList<Station>();
			bsStations = db.getBorderStations();
			for (Station stationX : bsStations) {
				for (Station stationY : bsStations) {
					if (!stationX.equals(stationY)) {
						Route route = new AStar().getRoute(stationX, stationY);
						LocalRegionalServer.localBsDataArray.add(new BSData(LocalRegionalServer.regionNumber, stationX.region.get(0), stationY.region.get(0),
								route.route, route.cost, route.cost));
						// System.out.println("StationX: " + stationX.stationName + ", StationY: " +
						// stationY.stationName);
						// System.out.println("first route: " + route.route.get(0).stationName + ", last route: "
						// + route.route.get(route.route.size() - 1).stationName + "\n");
					}
				}
			}
			log.printAndLog("\nBS Data successfully generated");
			LocalRegionalServer.aliveUnixTimeStamp = System.currentTimeMillis() / 1000;
		} catch (Exception e) {
			System.err.println("An error occured while trying to calculate BS Data:");
			e.printStackTrace();
		}

		// generate bs data string and print it out
		LocalRegionalServer.bsPacket.generateCPPacket();
		log.log("BSPacket string generated:\n" + LocalRegionalServer.bsPacket.packetAsString);

	}
	

}
