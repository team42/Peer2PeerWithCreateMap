package handleNewTrips;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import model.Peer;

import peer.UDPPeer;
import database.TripOffersDAO;
import config.*;

public class NewTrips {

	Timer timer;
	TripOffersDAO tripOfferDAO = new TripOffersDAO();
	Configuration config = Configuration.getConfiguration();
	
	public NewTrips() {
		timer = new Timer();
		timer.schedule(new addNewTrip(), 5000, 10000);
	}
	
	public void handleTrip(String tripID, String coordinate) {
		
		String[] coords = coordinate.split(",");
		int xCoord = Integer.parseInt(coords[0]);
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		peers = config.getPeers();
		
		String correctCoordinate = coordinateSyntax(coords);
		
		for(int i=0; i<peers.size(); i++) {
			if(xCoord >= ((i*2000)/peers.size()) && xCoord < (((i+1)*2000)/peers.size())) {
				String query = "HANTR" + tripID + correctCoordinate;
				
				System.out.println(peers.get(i).getIp());
				
				try {
					UDPPeer.sendMessages(InetAddress.getByName(peers.get(i).getIp()), query);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String coordinateSyntax(String[] coords) {
		
		for(int i=0; i<coords.length; i++) {
			while(coords[i].length() < 4) {
				coords[i] = "0" + coords[i];
			}
		}
		
		String coordinate = coords[0] + "," + coords[1]; 
		
		return coordinate;
	}
	
	class addNewTrip extends TimerTask {
		public void run() {
			String[] customer = tripOfferDAO.getCustomer();
			
			if(!customer[0].equals("none")) {
				handleTrip(customer[0], customer[1]); 
			}
		}
	}
}
