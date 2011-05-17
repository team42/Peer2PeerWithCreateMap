package peer;

import handleNewTrips.NewTrips;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Peer;
import config.Configuration;
import command.CommandController;
import taxicomm.TaxiComm;

/**
 * UDP Peer implementation.
 * 
 * Code taken from IDIST3 lecture notes (supplied by bhc) and modified to suit
 * this project.
 * 
 * @author Lasse
 * 
 */
public class UDPPeer {

	static UDPListenThread L;
	static InetAddress IPAddress;
	static CommandController cmdControl = new CommandController();
	static Configuration config = Configuration.getConfiguration();

	static int serverPort = 4342;
	static int clientPort = 4341;
	static DatagramSocket peerSocket;

	static byte[] queryRaw = new byte[1024];
	static byte[] replyRaw = new byte[1024];

	static String companyID = "";

	/**
	 * Starts a client and server which enables communication with other
	 * discovered peers in the network.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws IOException {

		while (companyID.length() != 2) {
			companyID = JOptionPane
					.showInputDialog("Insert Company ID\nMust be 2 characters!");
		}

		config.setCompanyID(companyID);

		try {
			peerSocket = new DatagramSocket(clientPort);
			IPAddress = getAlivePeer();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Start the server
		L = new UDPListenThread(serverPort);
		L.setDaemon(true);
		L.start();

		if (IPAddress == null) {
			System.out.println("No peers found");
		} else {

			String query = "HELLO";
			sendMessages(IPAddress, query);
		}

		@SuppressWarnings("unused")
		NewTrips newTrips = new NewTrips();
		@SuppressWarnings("unused")
		TaxiComm taxiComm = new TaxiComm();

		while (true) {
			// Receive packet
			DatagramPacket receivePacket = new DatagramPacket(replyRaw,
					replyRaw.length);
			peerSocket.receive(receivePacket);
			String reply = new String(receivePacket.getData(), 0,
					receivePacket.getLength());
			cmdControl.processRequest(reply, peerSocket, receivePacket);
			System.out.println("Reply from: " + receivePacket.getAddress()
					+ "\nData: " + reply);
		}
	}

	/**
	 * Sends a UDP message to the supplied IP address with the given data.
	 * 
	 * @param ip
	 *            IP address to send UDP packet to.
	 * @param query
	 *            Data to send.
	 * 
	 **/
	public static void sendMessages(InetAddress ip, String query)
			throws IOException {
		System.out.println(ip + " " + query);
		queryRaw = query.getBytes();

		// Send packet
		DatagramPacket sendPacket = new DatagramPacket(queryRaw,
				queryRaw.length, ip, serverPort);
		peerSocket.send(sendPacket);
		System.out.println("Send to: " + ip + "\nData: " + query + "\n");
	}

	/**
	 * Opens "peers" text file and pings the IP address. If it responds we
	 * return the IP to the calling method.
	 * 
	 * Cleans up the peer/peers text file, so that it only contains alive peers.
	 * 
	 * @throws Exception
	 * @returns
	 **/
	private static InetAddress getAlivePeer() throws Exception {
		PeerList peerList = new PeerList();
		peerList.openFile(0);
		ArrayList<Peer> peers = peerList.readPeerList();

		/*
		 * Check if peers are alive. Removes ones that don't respond to ping.
		 */
		for (int i = 0; i < peers.size(); i++) {
			String ip = peers.get(i).getIp().replaceAll(",+[ 0|1]+$", ""); // trim
																			// to
																			// ip
																			// part
			InetAddress peer = InetAddress.getByName(ip);
			if (peer.isReachable(20)) {
				System.out.println(peer + " is alive!");
			} else {
				peers.remove(i);
				System.out.println(peer + " is dead...");
			}
		}
		peerList.closeInputFile();

		// Lets write the new list of alive peers
		peerList.openFile(1);
		peerList.writePeerList(peers);
		peerList.closeOutputFile();

		config.setPeers(peers); // Load peers into configuration.

		// return the first alive peer in the list
		return InetAddress.getByName(peers.get(0).getIp()
				.replaceAll(",+[ 0|1]+$", ""));
	}
}
