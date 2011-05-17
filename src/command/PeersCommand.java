package command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import model.Peer;


import config.Configuration;

/**
 * 
 * This command is used after the Peer has announced itself to the network, all
 * other Peers will respond with this command to share their peer list.
 * 
 * @author Nicolai
 *
 */
public class PeersCommand extends Command {

	private Configuration config = Configuration.getConfiguration();
	
	/**
	 * This execute will identify the peerlist and split it to a string array.
	 * This string array will be converted to an arraylist of strings.
	 * The peerlist will be added to the old peer list.
	 * 
	 * @param receivedMessage - The received message
	 * @param peerSocket - The socket to respond at
	 * @param receivePacket - The packet containing IP etc of sender
	 */
	public void execute(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket) {
		String newPeerList = receivedMessage.substring(5);
		
		String[] arPeerList = newPeerList.split("%");
		
		ArrayList<Peer> peerList = config.getPeers();
		
		for(int i=0; i < arPeerList.length; i++) {
			for (int j=0; j<peerList.size(); j++) {
				int k = 0;
				if(arPeerList[i].equals(peerList.get(j).getIp())) {
					k = 1;
				}
				if(k == 0) {
					peerList.add(new Peer(arPeerList[i], 1));
				}
			}
		}
		
		try {
			config.setPeers(peerList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
