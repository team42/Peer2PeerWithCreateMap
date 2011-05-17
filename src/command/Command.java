package command;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * <code>Command</code> is the abstract class the Command classes must adhere to.
 *
 * It must also implement the <code>execute</code> method as per the Command
 * design pattern.
 *
 * @author Nicolai, Lasse
 */

public abstract class Command {
	
	/**
	 * The execute method is invoked each time an command is received at the P2P network.
	 * 
	 * @param receivedMessage - The received message
	 * @param peerSocket - The socket to respond at
	 * @param receivePacket - The packet containing IP etc of sender
	 */
    public abstract void execute(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket);
}