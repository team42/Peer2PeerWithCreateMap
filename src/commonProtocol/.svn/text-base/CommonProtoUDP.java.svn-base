package commonProtocol;

import internals.Database;
import internals.LocalRegionalServer;
import internals.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import pathCalculation.AStar;

import common.Route;
import commonProtocol.packet.AlivePacket;
import commonProtocol.packet.LoginPacket;
import commonProtocol.packet.LogoutPacket;
import commonProtocol.packet.StationRequestPacket;
import commonProtocol.packet.StationRespondPacket;
import commonProtocol.packet.UpdatePacket;

/**
 * @param args
 */
public class CommonProtoUDP extends Thread {

	Database db = Database.getInstance();
	NodeList nodeList = NodeList.getInstance();
	Log log = Log.getInstance();

	// Size of recv buffer
	byte[] recvBuffer = new byte[65535];
	// initiate everything
	DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);
	DatagramSocket datagramSocket;

	public CommonProtoUDP(int port) {
		try {
			datagramSocket = new DatagramSocket(port);
			log.printAndLog("Regional Server UDP initiated at port: " + port);
			this.setDaemon(true);
			this.start();
		} catch (SocketException e) {
			log.printAndLogE("Exception when trying to set up DatagramSocket " + e);
		}
	}

	/**
	 * Method to send a datagram in form of text string to specific IP:port
	 * 
	 * @param message
	 * @param ipAddress
	 * @param port
	 */
	public void sendDatagram(String message, String ipAddress, int port) {
		try {
			InetAddress dest = InetAddress.getByName(ipAddress);
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket datagrampacket = new DatagramPacket(message.getBytes(), message.length(), dest, port);
			socket.send(datagrampacket);
		} catch (Exception e) {
			log.printAndLogE("Exception when trying to send datagram " + e);
		}
	}

	// Send logout packet to all Nodes on nodeList<Node>
	public void sendLogoutToAllNodes(String message) {
		int i = 0;
		for (Node node : nodeList.getNodes()) {
			try {
				InetAddress dest = InetAddress.getByName(node.ip);
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket datagrampacket = new DatagramPacket(message.getBytes(), message.length(), dest,
						node.udpPort);
				socket.send(datagrampacket);
				log.printAndLog("logout test, send to all on nodeList, IP: " + node.ip + ",Port: " + node.udpPort
						+ ", message: " + message);
				nodeList.removeNode(i);
				i++;
			} catch (Exception e) {
				log.printAndLogE("Exception when trying to send logout to all nodes " + e);
			}
		}
	}

	/**
	 * Send login UDP Datagram to node and wait for answer
	 */
	public void sendLogin(Node node) {
		try {
			InetAddress dest = InetAddress.getByName(node.ip);
			// Dont send login to self
			if (dest.getHostAddress().equals(LocalRegionalServer.serverIPAddress)
					&& node.udpPort == LocalRegionalServer.commonProtoUDPPort) {
			} else {
				String loginMsg = new LoginPacket(1, LocalRegionalServer.commonProtoUDPPort,
						LocalRegionalServer.serverIPAddress).generateCPPacket();
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket datagrampacket = new DatagramPacket(loginMsg.getBytes(), loginMsg.length(), dest,
						node.udpPort);
				socket.send(datagrampacket);
			}
		} catch (Exception e) {
			log.printAndLogE("Exception when trying to send Login datagram to " + node.ip + ":" + node.udpPort + " " + e);
		}
	}

	/**
	 * Send update packet to all Nodes on nodeList<Node>
	 * 
	 * @param message
	 */
	public void sendUpdateToAllNodes() {

		String message = new UpdatePacket(nodeList.getNodes()).generateCPPacket();
		for (Node node : nodeList.getNodes()) {
			try {
				InetAddress dest = InetAddress.getByName(node.ip);
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket datagrampacket = new DatagramPacket(message.getBytes(), message.length(), dest,
						node.udpPort);
				socket.send(datagrampacket);
				log.log("Sending update on UDP to " + dest + ":" + node.udpPort
						+ ", message: " + message);
			} catch (Exception e) {
				log.printAndLogE("Exception when trying to send update to all nodes " + e);
			}
		}
	}

	public void sendAliveToAllNodes() {

		String message = new AlivePacket().generateCPPacket();
		
		for (Node node : nodeList.getNodes()) {
			try {
				// Dont send to self
				if (!(node.ip.equals(LocalRegionalServer.serverIPAddress) && node.udpPort == LocalRegionalServer.commonProtoUDPPort)) {
					InetAddress dest = InetAddress.getByName(node.ip);
					DatagramSocket socket = new DatagramSocket();
					DatagramPacket datagrampacket = new DatagramPacket(message.getBytes(), message.length(), dest,
							node.udpPort);
					socket.send(datagrampacket);
					log.log("alive msg send: " + node.ip + ":" + node.udpPort + " message: " + message);
				}
			} catch (Exception e) {
				log.printAndLogE("Exception when trying to send alive to all nodes " + e);
			}
		}
	}

	public void sendUpdateToNode(Node node) {
		try {
			String message = new UpdatePacket(nodeList.getNodes()).generateCPPacket();
			InetAddress dest = InetAddress.getByName(node.ip);
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket datagrampacket = new DatagramPacket(message.getBytes(), message.length(), dest, node.udpPort);
			socket.send(datagrampacket);
			log.log("update test, send update to: " + dest + ":" + node.udpPort
					+ ", message: " + message);
		} catch (Exception e) {
			log.printAndLogE("Exception when trying to send update to node " + e);
		}
	}

	/**
	 * Sends a routeRequest to all nodes on the nodelist
	 * 
	 * @param regionID
	 *            Our regionID
	 * @param sessionID
	 *            The client sessionID
	 * @param fromStationName
	 *            From station Name
	 * @param toStationName
	 *            To Station Name
	 * @param searchCriteria
	 *            shortest or cheapest
	 */
	public void sendRouteRequestToAllNodes(int regionID, int sessionID, String fromStationName, String toStationName,
			String searchCriteria) {

		String message = new StationRequestPacket(regionID, sessionID, fromStationName, toStationName, searchCriteria)
				.generateCPPacket();
		for (Node node : nodeList.getNodes()) {
			try {
				InetAddress dest = InetAddress.getByName(node.ip);
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket datagrampacket = new DatagramPacket(message.getBytes(), message.length(), dest,
						node.udpPort);
				socket.send(datagrampacket);
				log.printAndLog("Route request sent to, IP: " + node.ip + ":" + node.udpPort + ", message: "
						+ message);
			} catch (Exception e) {
				log.printAndLogE("Exception when trying to send route request to all nodes " + e);
			}
		}
	}

	public enum Command {
		login, update, logout, alive, request
	}

	@Override
	public void run() {
		try {
			while (true) {

				datagramSocket.receive(packet);
				String senderIpAddress = packet.getAddress().getHostAddress();
				String msg = new String(recvBuffer, 0, packet.getLength());

				Command command = Command.valueOf(msg.split("\\|")[0]);
				switch (command) {
				case login:
					log.printAndLog("Login datagram received from " + senderIpAddress + "data: " + msg);
					try {
						LoginPacket loginPacket = new LoginPacket(msg, senderIpAddress);
						nodeList.updateLoginPacket(loginPacket);
					} catch (Exception e) {
						log.printAndLogE("Exception when trying to handle login packet: ");
					}
					break;
				case update:
					log.printAndLog("update datagram received from " + senderIpAddress + "data: " + msg);

					// TODO Update the NodeList with the new nodes received
					UpdatePacket updatePacket = new UpdatePacket(msg);
					nodeList.updateNodes(updatePacket);
					// When update is received we are part of p2p net:
					LocalRegionalServer.updateReceived = true;
					break;
				// If logout received, remove Node from nodeList<Node>
				case logout:
					log.printAndLog("logout datagram received from " + senderIpAddress + "data: " + msg);
					try {
						LogoutPacket logoutPacket = new LogoutPacket(msg);
						nodeList.removeNode(senderIpAddress, logoutPacket.port);
					} catch (Exception e) {
						log.printAndLogE("Exception when trying to handle logout " + e);
					}
					break;
				case alive:
					log.log("Alive datagram received from " + senderIpAddress + " data: " + msg);
					// TODO Process alive commands
					try {
						AlivePacket aP = new AlivePacket(msg, senderIpAddress);
						nodeList.updateNode(aP);
					} catch (Exception e) {
						log.printAndLogE("Exception when trying to handle alive " + e);
					}
					break;

				case request:
					log.printAndLog("request datagram received:");
					try {
						// TODO more stuff!
						// Format the input to a stationReuqestPacket
						StationRequestPacket stationRequestPacket = new StationRequestPacket(msg);

						// result
						ArrayList<Route> responseRoutes = new ArrayList<Route>();

						// validate stations; how many of the two is in our
						// region
						// bothIsOurs returns true if both the from- and
						// tostation is located in our region
						boolean fromIsOurs = Database.getInstance().stationExists(stationRequestPacket.fromStationName);
						boolean toIsOurs = Database.getInstance().stationExists(stationRequestPacket.toStationName);

						boolean bothIsOurs = (fromIsOurs && toIsOurs ? true : false);

						// instantiate AStar()
						AStar astar = new AStar();

						int fromStationID, toStationID;

						if (bothIsOurs) {
							// both stations is located in our region; calculate
							// regional route

							// get station id's
							fromStationID = Database.getInstance().getStationID(stationRequestPacket.fromStationName);
							toStationID = Database.getInstance().getStationID(stationRequestPacket.toStationName);

							// calculate route
							responseRoutes.add(astar.getRoute(fromStationID, toStationID));

						} else if (fromIsOurs) {
							// only the from station is located in our region
							// calculate routes from every borderstation to the
							// from station

							// get station id
							fromStationID = Database.getInstance().getStationID(stationRequestPacket.fromStationName);

							// calculate routes
							responseRoutes = astar.getBorderstationRoutes(Database.getInstance().getStationID(
									stationRequestPacket.fromStationName));

						} else if (toIsOurs) {
							// only the to station is located in our region
							// calculate routes from every borderstation to the
							// to station

							// get station id
							toStationID = Database.getInstance().getStationID(stationRequestPacket.toStationName);

							// calculate routes
							responseRoutes = astar.getBorderstationRoutes(Database.getInstance().getStationID(
									stationRequestPacket.toStationName));

						} else {
							// none of the stations requested is ours
							// do nothing
						}

						// Calculate the route with the data
						// If we do not have the stations for this request, the
						// route object will be null

						// TODO Calculate all the routes
						// Route thisRoute = astar.getRouteResponse(
						// stationRequestPacket.fromStationName,
						// stationRequestPacket.toStationName);

						// if the responseRoutes is not empty: send a response
						// to
						// the sender
						if (responseRoutes.size() > 0) {
							StationRespondPacket thisResponsePacket = new StationRespondPacket(
									LocalRegionalServer.regionNumber, stationRequestPacket.sessionID, bothIsOurs,
									responseRoutes);
							LocalRegionalServer.commonProtoTCP.sendStationResponse(stationRequestPacket.regionID,
									thisResponsePacket);
						}

					} catch (Exception e) {
						log.printAndLogE("Exception when trying to handle BS request " + e);
					}
					break;

				} // switch end
			} // While loop

		} catch (IOException e) {
			log.printAndLogE("UDP listener thread crashed :(");
		}
	}
} // End of CommonProtoUDP
