package command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import model.Peer;
import config.Configuration;

/**
 * This command is used when another Peer announces itself.
 * 
 * @author Nicolai
 *
 */
public class HelloCommand extends Command {

	//
	Configuration config = Configuration.getConfiguration();
	
	/**
	 * The execute method will get it's own PeerList and send it back to the sender.
	 * 
	 * @param receivedMessage - The received message
	 * @param peerSocket - The socket to respond at
	 * @param receivePacket - The packet containing IP etc of sender
	 */
	public void execute(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket) {
		
		ArrayList<Peer> peerList = config.getPeers();
		String strPeerList = "";
		
		for(int i=0; i > peerList.size(); i++) {
			strPeerList += strPeerList + peerList.get(i) + "%";
		}
		
		String reply = "PEERS" + strPeerList;
		
		int peer = receivePacket.getPort();
        
		byte[] replyRaw = new byte[1024];
        replyRaw = reply.getBytes();
        
        DatagramPacket sendPacket = new DatagramPacket(replyRaw, replyRaw.length, receivePacket.getAddress(), peer);
        
        try {
			peerSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
