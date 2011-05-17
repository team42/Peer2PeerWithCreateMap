package internals;

import java.util.ArrayList;
import java.util.Scanner;
import pathCalculation.BSWorld;
import commonProtocol.*;
import commonProtocol.packet.LoginPacket;
import commonProtocol.packet.LogoutPacket;

public class ServiceManager extends Thread {

	private NodeList nodeList = NodeList.getInstance();
	private Log log = Log.getInstance();

	private int port;
	private String ip;
	private String msg;

	Scanner in = new Scanner(System.in);

	@Override
	public void run() {
		while (true) {
			System.out.println();
			new LocalRegionalServer();
			System.out.println("This Server:\nIP:\t\t" + LocalRegionalServer.serverIPAddress);
			new LocalRegionalServer();
			System.out.println("UDP/TCPPort:\t" + LocalRegionalServer.commonProtoUDPPort);
			new LocalRegionalServer();
			System.out.println("SSL Port:\t" + LocalRegionalServer.preAppSSLPort);
			new LocalRegionalServer();
			System.out.println("Region:\t\t" + LocalRegionalServer.regionNumber);
			System.out.println();
			System.out.println();
			System.out.println("Command:\t\tDescription:");
			System.out.println("1. Login\t\tLogin to P2P network");
			System.out.println("2. Logout\t\tLogout of P2P network");
			System.out.println("3. Update\t\tUpdate internal datastucture");
			System.out.println("4. NodeList\t\tShow NodeList");
			System.out.println("5. BS Data\t\tShow current BS Data");
			System.out.println("6. Quit\t\t\tQuit & Shutdown server");
			System.out.println("7. BSWorld\t\tShow BSWorld output (debug)");
			System.out.println();
			System.out.print("Choose Command: ");
			int command = in.nextInt();
			switch (command) {
			case 1:
				log.printAndLog("Login:");
				System.out.println();
				log.printAndLog("Node List IP: ");
				ip = in.next();
				log.log(ip);
				System.out.print("Node List Port #: ");
				port = in.nextInt();
				log.log(Integer.toString(port));
				// Generate login packet
				msg = new LoginPacket(1, LocalRegionalServer.commonProtoUDPPort, ip).generateCPPacket();
				// Send login packet
				LocalRegionalServer.commonProtoUDP.sendDatagram(msg, ip, port);
				log.printAndLog("Login message/packet send to: " + ip + " port: " + port + "\nmsg: " + msg);
				break;
			case 2:
				// Generate logout packet
				msg = new LogoutPacket(1, LocalRegionalServer.commonProtoUDPPort).generateCPPacket();
				// Send logout packet to all Nodes on nodeList<Node>
				LocalRegionalServer.commonProtoUDP.sendLogoutToAllNodes(msg);
				log.printAndLog("Logout message send to all on nodeList!");
				nodeList.clear();
				break;
			case 3:
				log.printAndLog("Updating.......");
				System.out.println();
				// Update internal datastructure from database
				Database.getInstance().init();
				log.printAndLog("Update of internal datastructure completed!");
				break;
			case 4:
				// Print out p2pNodeList
				System.out.println("P2P NodeList:");
				System.out.println();
				System.out.println("IP:\t\t\tPort:\t\tLast Alive:");
				for (int i = 0; i < nodeList.getNodes().size(); i++)
					System.out.println(nodeList.getNodes().get(i).ip + "\t\t" + nodeList.getNodes().get(i).udpPort
							+ "\t\t" + (System.currentTimeMillis() - nodeList.getNodes().get(i).aliveTimeStamp) / 1000
							+ " sec");
				break;
			case 5:
				// TODO Print out BS Data
				System.out.println("Local BS Data:");
				for (BSData bsData : LocalRegionalServer.localBsDataArray) {
					System.out.println("From region: " + bsData.fromRegion + " to region: " + bsData.toRegion + ":");
				}
				
				System.out.println("BSPacket string:\n" + LocalRegionalServer.bsPacket.packetAsString);
				
				// print out the foreign regional data.
				System.out.println("Foreign BS Data:");
				for (int i = 0; i < LocalRegionalServer.foreignRegionBSData.length; i++) {
					if (!LocalRegionalServer.foreignRegionBSData[i].isEmpty()) {
						System.out.println("BS data registered for region " + i + ":");
						ArrayList<BSData> bsDataArayList = LocalRegionalServer.foreignRegionBSData[i];
						for (BSData bsData : bsDataArayList)
							System.out.println("From region: " + bsData.fromRegion + " to region: " + bsData.toRegion
									+ ":");
					}
				}
				
				break;
			case 6:
				// Quit & Shutdown server
				log.printAndLog("Quitting....");
				System.exit(0);
				break;
			case 7:
				// bsworld debug
				BSWorld.debugRunAndPrint();
			}
		}
	}

}
