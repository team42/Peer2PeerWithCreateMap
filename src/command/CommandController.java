package command;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

/**
 * <code>CommandController</code> will create a HashMap containing references
 * to the command classes and use resolveCommand to look up the relevant command
 * class based on the command received.
 *
 * This code was originally written by hbe and has been modified to fit the goal
 * of this project.
 *
 * @see UDPPeer
 *
 * @author Nicolai, Lasse
 */
public class CommandController {

    /**
     * Instantiates objects used in methods.
     */
	private Command cmd;
	private Map<String, Command> commands;   // An object that maps keys to values
    
    /**
     * Constructor creates the HashMap containing the commands.
     *
     */
    public CommandController() {
        commands = new HashMap<String, Command>();
        commands.put("HELLO", new HelloCommand());
        commands.put("PEERS", new PeersCommand());
        commands.put("REQTC", new ReqTaxiCommand());
        commands.put("SENTC", new SendTaxiCommand());
        commands.put("TAXOF", new TaxiOfferCommand());
        commands.put("TAXAC", new TaxiAcceptCommand());
        commands.put("GOTTR", new GotTripCommand());
        commands.put("MISTR", new MissTripCommand());
        commands.put("HANTR", new HandleTripCommand());
        commands.put("00", new UnknownCommand());
    }

    /**
     * This method is called by UDPPeer which is every
     * time a packet is received.
     *
     * Is uses <code>resolveCommand</code> to find the relevant command class
     * and run its execute method.
     *
     * @param command
     */
    public void processRequest(String receivedMessage, DatagramSocket peerSocket, DatagramPacket receivePacket) {
        cmd = resolveCommand(receivedMessage.substring(0, 5));
        cmd.execute(receivedMessage, peerSocket, receivePacket);
    }

    /**
     * Forwards a request the HashMap to lookup the command and return the
     * relevant instantiated object.
     *
     * @param request The received command
     * @return The instantiated object of the found command
     */
    public Command resolveCommand(String request) {
        cmd = (Command) commands.get(request);
        if (cmd == null) {
            cmd = (Command) commands.get("00"); // Unknown command
        }
        return cmd;
    }
}