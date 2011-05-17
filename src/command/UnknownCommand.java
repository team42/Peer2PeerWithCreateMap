package command;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * <code>UnknownCommand</code> is invoked when an unknown command is received
 * from the Card Reader.
 *
 *
 * @see Command
 *
 * @author Nicolai, Lasse
 */
public class UnknownCommand extends Command {

	/**
	 * When an unknown command is received an simple "Unknown Command",
	 * the message and the command is printed.
	 * 
	 * @param receivedMessage - The received message
	 * @param peerSocket - The socket to respond at
	 * @param receivePacket - The packet containing IP etc of sender
	 */
    public void execute(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket) {
        System.out.println("Unkown Command:\n" + receivedMessage);
    }
}