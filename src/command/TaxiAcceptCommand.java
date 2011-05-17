package command;

import java.io.IOException;
import java.net.*;
import peer.UDPPeer;
import database.OngoingTripsDAO;
import database.FinishedTripsDAO;

import java.util.*;

/**
 * A taxi of a company have accepted the trip that this peer is responsible for.
 * 
 * @author Nicolai
 *
 */
public class TaxiAcceptCommand extends Command {

	private OngoingTripsDAO dao = new OngoingTripsDAO();
	private FinishedTripsDAO finishedDAO = new FinishedTripsDAO();
	
	/**
	 * The taxi and trip ID is identified
	 * A "got trip" is send to the sender
	 * The rest of the companies are send a "missed trip".
	 * 
	 * @param receivedMessage - The received message
	 * @param peerSocket - The socket to respond at
	 * @param receivePacket - The packet containing IP etc of sender
	 */
	public void execute(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket) {
		String taxiID = receivedMessage.substring(5, 11);
		String tripID = receivedMessage.substring(11);
		
		ArrayList<String> companyIPs = dao.getCompanyIP(tripID);
		dao.deleteOngoingTrip(tripID);
		
		String companyGot = receivePacket.getAddress().getHostAddress();
		String query = "MISTR" + tripID;
		
		for(int i=0; i<companyIPs.size(); i++) {
			if(!companyIPs.get(i).equals(companyGot)) {
				try {
					UDPPeer.sendMessages(InetAddress.getByName(companyIPs.get(i)), query);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		finishedDAO.addTrip(tripID, taxiID);
		
		query = "GOTTR" + tripID + taxiID;
		
		try {
			UDPPeer.sendMessages(receivePacket.getAddress(), query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
