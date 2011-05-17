package internals;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import commonProtocol.*;
import commonProtocol.packet.BSPacket;

public class LocalRegionalServer {
	// Settings
	public static int commonProtoTCPPort = 50001;
	public static int commonProtoUDPPort = 50001;
	public static int preAppSSLPort = 50002;
	public static String serverIPAddress = "";
	public static int regionNumber = 1;

	// Static instances
	public static ArrayList<BSData> localBsDataArray = new ArrayList<BSData>();

	// Array of generic type arraylists, workaround for java bug:
	@SuppressWarnings("unchecked")
	public static ArrayList<BSData>[] foreignRegionBSData = new ArrayList[50];

	// timestamp for latest bsdata:
	public static long aliveUnixTimeStamp;

	// If an update is received, the server should be part of the 2p2 network
	public static boolean updateReceived = false;

	public static CommonProtoTCP commonProtoTCP;
	public static CommonProtoUDP commonProtoUDP;
	public static BSPacket bsPacket = new BSPacket();

	public static void main(String[] args) {

		// Singleton objects:
		Database db = Database.getInstance();
		// AStar aStar = new AStar();//.getInstance();
		Log log = Log.getInstance();
		NodeList nodeList = NodeList.getInstance();
		Initiator initiator = Initiator.getInstance();

		// Initialize array with arraylists
		for (int i = 0; i < foreignRegionBSData.length; i++)
			foreignRegionBSData[i] = new ArrayList<BSData>();

		// Setup server with ports and region
		Scanner in = new Scanner(System.in);
		System.out.println("Server setup!\nCommon TCP/UDP port: ");
		int common = in.nextInt();
		commonProtoUDPPort = common;
		commonProtoTCPPort = common;
		System.out.println("SSL port: ");
		preAppSSLPort = in.nextInt();
		System.out.println("Region: ");
		regionNumber = in.nextInt();
		log.printAndLog("**********************************");
		log.printAndLog("* Starting local regional server *");
		log.printAndLog("**********************************");
	
		// initialize database (cache data in memory)
		db.init();

		// Get IP Address
		serverIPAddress = initiator.getIpAddress();
		// serverIPAddress = "127.0.0.1"; // <- for testing

		// start communication:
		try {
			commonProtoTCP = new CommonProtoTCP(commonProtoTCPPort);
			commonProtoUDP = new CommonProtoUDP(commonProtoUDPPort);
			new SSLListener(preAppSSLPort);
		} catch (Exception e) {
			log.printAndLogE("Error occured when trying to initiate connection ports" + e);
		}

		// Get the nodelist from the database
		nodeList.nodeLinkList.addAll(db.getNodelist());

		// Log in to p2p network
		initiator.login2p2p();

		// Start AliveSender thread
		new AliveSender();

		// Generate the bs data
		initiator.generateBSData();

		log.printAndLog("Waiting 10. seconds to get alive data from other regional servers on p2p network");
		Wait.milliSec(0);

		// send bs reguest to nodes that are alive (has tcp port), and dont send to self
		ListIterator<Node> itr = nodeList.getAliveNodes().listIterator();
		while (itr.hasNext()) {
			Node node = itr.next();
			if (node.tcpPort != 0
					&& !((serverIPAddress.equals(node.ip) || serverIPAddress.equals("127.0.0.1")) && node.tcpPort == commonProtoTCPPort))
				commonProtoTCP.sendBSRequest(node);

		}

		// Start Servicemanager
		ServiceManager serviceManager = new ServiceManager();
		serviceManager.setDaemon(true);
		serviceManager.start();

		// Takes care of traversing the nodelist fro old AliveTimeStamp
		new NodeListAliveChecker();

		Wait.forever();
	}
}
