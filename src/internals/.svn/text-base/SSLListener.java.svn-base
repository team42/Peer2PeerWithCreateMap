package internals;

import java.io.*;
import common.*;

import java.net.Socket;
import java.net.ServerSocket;

import pathCalculation.AStar;

import common.CommWrapper;

/**
 * @param args
 */
public class SSLListener extends Thread {
	ServerSocket serverSocket;

	public SSLListener(int port) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Regional Server SSL initiated at port: " + port);
			this.setDaemon(true);
			this.start();
		} catch (IOException e) {
			System.err.println("Error when initaiting SSL on port: " + port + " port might be in use: ");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				SSLConn conThread = new SSLConn(serverSocket.accept());
				conThread.setDaemon(true);
				conThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class SSLConn extends Thread {
		private Socket socketConnection;

		public Object response;

		boolean threadActive = true;
		CommWrapper commWrapper;

		public SSLConn(Socket socketConnection) {
			System.out.println("SSL Connection established: " + serverSocket + "\nCurrent connection threads: "
					+ SSLConn.activeCount());
			this.socketConnection = socketConnection;
		}

		@Override
		public synchronized void run() {

			while (threadActive) {
				try {
					// Get input stream
					ObjectInputStream istream = new ObjectInputStream(socketConnection.getInputStream());
					// Get output stream
					ObjectOutputStream ostream = new ObjectOutputStream(socketConnection.getOutputStream());

					// Get object input
					commWrapper = (CommWrapper) istream.readObject();
					System.out.println(commWrapper.toString());

					Object data = commWrapper.getData();

					if (data instanceof Route) {
						// A*
						System.out.println("Route requested...");

						String fromStation = ((Route) data).fromStation;
						String toStation = ((Route) data).toStation;
						// TODO Fix searchCritera away from static text
						String searchCriteria = "shortest";
						// Check if the stations are in our region.
						// fromIsOurs = true -> The from station is in our
						// region
						boolean fromIsOurs = Database.getInstance().stationExists(fromStation);
						// toIsOurs = true -> The To station is in our region
						boolean toIsOurs = Database.getInstance().stationExists(toStation);

						// bothIsOurs = true -> Both stations is in our region
						boolean bothIsOurs = (fromIsOurs && toIsOurs ? true : false);

						if (bothIsOurs) {
							// both stations is located in our region; calculate
							// regional route
							AStar aStar = new AStar();
							commWrapper = aStar.getRoute(commWrapper);

						} else if (fromIsOurs) {
							// only the from station is located in our region
							// calculate routes from every borderstation to the
							// from station
							int sessionID = ClientSessionHandler.getInstance().createNewSession(this, fromStation,
									toStation);

							// Send a route request to all the other regions
							LocalRegionalServer.commonProtoUDP
									.sendRouteRequestToAllNodes(LocalRegionalServer.regionNumber, sessionID,
											fromStation, toStation, searchCriteria);

							// Wait for a reply
							try {
								wait(2000);
							} catch (InterruptedException e) {
								// Something failed
								System.out.println("Something failed when the SSL listener thread got notified");
								e.printStackTrace();
							}

							// Did we get interrupted?
							// If we did there must be data for us. Get it from
							// the Session Handler
							if (interrupted()) {
								commWrapper.setData(ClientSessionHandler.getInstance().getSessionData(sessionID));
							} else {
								System.out.println("TIMEOUT :(");
								commWrapper.setError("Timeout from regional servers.");
								commWrapper.setData(null);
							}

						} else if (toIsOurs) {
							// only the to station is located in our region
							// calculate routes from every borderstation to the
							// to station

						} else {
							// none of the stations requested is ours
							// ASK CP for all information

						}

						// debug
						// System.out.println("Borderstation Routes: Start!");
						// for (Route r : aStar.getBorderstationRoutes(((Route)
						// commWrapper.getData()).)) {
						// System.out.println(r.toString());
						// }
						// System.out.println("Borderstation Routes: Slut!");

					} else if (data instanceof MapData) {
						// The client requested MapData
						// generate and set it into the comWrapper object
						System.out.println("MapData requested...");

						SerializableStation stations[] = Database.getInstance().getStationMapData();
						SerializableStation connections[][] = Database.getInstance().getConnectionMapData();

						MapData mapData = new MapData();
						mapData.setData(stations, connections);

						commWrapper.setData(mapData);
					}

					// Send the commWrapper object back to the client.
					ostream.writeObject(commWrapper);
					System.out.println("Response sent...");
					closeConnection();
				} catch (Exception e) {
					e.printStackTrace();
					closeConnection();
				}
			}
		}

		public void closeConnection() {
			try {
				socketConnection.close();
				threadActive = false;
			} catch (Exception e) {
				System.out.println("Error when closing connection: " + e);
			}
		}

	} // End of SSLConn

} // End of SSLListener
