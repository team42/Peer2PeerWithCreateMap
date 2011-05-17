package commonProtocol;

import internals.BSData;
import internals.ClientSessionHandler;
import internals.LocalRegionalServer;
import internals.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import commonProtocol.packet.BSPacket;
import commonProtocol.packet.BSRequestPacket;
import commonProtocol.packet.StationRespondPacket;

/**
 * @param args
 */
public class CommonProtoTCP extends Thread {
	// init everything
	ServerSocket serverSocket;
	Log log = Log.getInstance();

	public CommonProtoTCP(int port) {
		try {
			serverSocket = new ServerSocket(port);
			log.printAndLog("Regional Server TCP initiated at port: " + port);
			this.setDaemon(true);
			this.start();
		} catch (IOException e) {
			log.printAndLogE("Exception when initaiting TCP on port: " + port + " port might be in use - " + e);
		}
	}

	/**
	 * Sends bs request to alive node (alive packet)
	 * 
	 * @param aliveNode
	 */
	public void sendBSRequest(Node aliveNode) {
		log.printAndLog("First time alive data from node, sending bs request to: " + aliveNode);
		Socket socket;
		try {
			socket = new Socket(aliveNode.ip, aliveNode.tcpPort);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(new BSRequestPacket().generateCPPacket());
			new CommonProtoTCPReceiver(socket);

		} catch (Exception e) {
			log.printAndLogE("An Exception occured when trying to connect to " + aliveNode.ip + ":" + aliveNode.tcpPort
					+ " to send BS Request/get BS Data - " + e);
		}
	}

	/**
	 * send a response
	 * 
	 * @param senderRegionID
	 * @param responsPacket
	 */
	public void sendStationResponse(int senderRegionID, StationRespondPacket responsePacket) {
		Socket socket;
		Node node = NodeList.getInstance().getRegionNode(senderRegionID);
		try {
			socket = new Socket(node.ip, node.tcpPort);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(responsePacket.generateCPPacket());
			new CommonProtoTCPConn(socket);
		} catch (Exception e) {
			log.printAndLogE("An Exception occured when trying to connect to " + node.ip + ":" + node.tcpPort
					+ " to send station response - " + e);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				CommonProtoTCPConn conThread = new CommonProtoTCPConn(serverSocket.accept());
				conThread.setDaemon(true);
				conThread.start();
				log.printAndLog("TCP Connection established: " + serverSocket);
			} catch (Exception e) {
				log.printAndLogE("Exception occured when running CommonProtoTCPThread: " + e);
			}
		}
	}

	// enums used for tcp data commands
	public enum Command {
		bsrequest, bsdata, response
	};

	/**
	 * Private connection Class This is instantiated when we want to receive or send a packet on the TCP protocol
	 */
	private class CommonProtoTCPConn extends Thread {
		private Socket socketConnection;

		boolean threadActive = true;
		String inputString = "";

		public CommonProtoTCPConn(Socket socketConnection) {
			this.socketConnection = socketConnection;
		}

		@Override
		public void run() {
			Command command = null;
			while (threadActive)
				try {
					// Get input stream
					BufferedReader bufferIn = new BufferedReader(new InputStreamReader(
							socketConnection.getInputStream()));
					// Get output stream
					DataOutputStream bufOut = new DataOutputStream(socketConnection.getOutputStream());

					PrintWriter out = new PrintWriter(socketConnection.getOutputStream(), true);

					// Get input as string
					inputString = bufferIn.readLine();
					if(inputString == null)
						break;
					// get the command and use switch on it
					command = Command.valueOf(inputString.split("\\|")[0]);

					log.printAndLog("Received on TCP " + inputString);
					switch (command) {

					// INCOMMING REQUEST - They want our border station data.
					//
					case bsrequest:
						log.printAndLog("station request received: " + inputString);
						try {
							log.printAndLog("Sending bs data to: " + socketConnection.toString());
							out.println(LocalRegionalServer.bsPacket.packetAsString);
							new CommonProtoTCPReceiver(socketConnection);
						} catch (Exception e) {
							log.printAndLogE("Exception occured when handling BS Request: " + e);
						}
						break;
					// We received a Response to station request:
					case response:
						log.printAndLog("station response received: " + inputString);
						try{
							// get the Client session Handler
							ClientSessionHandler sessionHandler = ClientSessionHandler.getInstance();

							// Format the input
							StationRespondPacket thisRespondPacket = new StationRespondPacket(inputString);

							// We got a reply via CP. Test if it is a complete route
							// (regional), or a fragment (interregional)
							if (thisRespondPacket.regional) { // Regional
								// The route is complete. Place it in the
								// ClientSessionHandler so the sleeping thread can
								// pick it up
								sessionHandler.setSessionData(thisRespondPacket.sessionID, thisRespondPacket.routes.get(0));
							} else {
								// InterRegional - the route is a fragment to a borderStation.
								// Pass it along to "Dijkstra" before returning it to the sleeping thread

								// Get variables
								int sessionID = thisRespondPacket.sessionID;
								String toStation = sessionHandler.getToStation(sessionID);
								String fromStation = sessionHandler.getFromStation(sessionID);

								// Find the route
//								Route resultRoute = new Dijkstra().getRoute(thisRespondPacket.routes, fromStation,
//										toStation);

//								// Send it to the Sessionhandler
//								sessionHandler.setSessionData(thisRespondPacket.sessionID, resultRoute);

							}
	
						}catch(Exception e){
							log.printAndLogE("Exception when receiving station response");
						}
						break;

					}

				} catch (Exception e) {
					Log.getInstance().printAndLogE("Exception when receiving packet: " + inputString + " - " + e);
					closeConnection();
				}
		}

		public void closeConnection() {
			try {
				socketConnection.close();
				threadActive = false;
			} catch (Exception e) {
				log.printAndLogE("Exception in closing connection: " + e);
			}
		}

	} // End of CommonProtoTCPConn class

	private class CommonProtoTCPReceiver extends Thread {
		Socket receiverSocket;
		Log log = Log.getInstance();

		public CommonProtoTCPReceiver(Socket receiverSocket) {
			log.log("TCP receiver thread started (waiting for tcp response)");
			this.receiverSocket = receiverSocket;
			this.setDaemon(true);
			this.start();
		}

		@Override
		public void run() {
			try {
				BufferedReader bufferIn = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));
				Command command = null;
				// Get input as string
				String inputString;
				inputString = bufferIn.readLine();
				if (inputString == null)
					return;
				// get the command and use switch on it
				command = Command.valueOf(inputString.split("\\|")[0]);
				switch (command) {
				case bsdata:
					try {
						// Add bs data to foreign bs data array
						BSPacket bsPacket = new BSPacket(inputString);
						log.printAndLog("BS Data received from region: " + bsPacket.regionID);
						LocalRegionalServer.foreignRegionBSData[bsPacket.regionID].clear();
						for (BSData bsData : bsPacket.bsDataArray)
							LocalRegionalServer.foreignRegionBSData[bsPacket.regionID].add(bsData);
						
					} catch (Exception e) {
						log.printAndLogE("An Exception occured while trying to dissemble a bsdata packet:\n" + e
								+ "\nconnection has been closed.");
					} finally {
						receiverSocket.close();
					}
				}
			} catch (IOException e1) {
				log.printAndLogE("Exception in CP Receiver thread: " + e1);
			}
		}
	}
} // End of CommonProtoTCP class
