package internals;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;

import commonProtocol.packet.LoginPacket;

public class SendUdpForTesting {

	public static void main(String args[]) {
//		int vers = 1;
//
//		System.out.println("Commands:");
//		System.out.println("1. Create login, logout and update");
//		System.out.println("2. Send station request");
//
		InetAddress dest;
		DatagramSocket socket;
		DatagramPacket packet;
//
//		while (true) {
//			try {
//				Scanner in = new Scanner(System.in);
//				int command = in.nextInt();
//				switch (command) {
//				case 1:
//					System.out.print("Type: ");
//					String type = in.next();
//					// System.out.print("IP: ");
//					// String ip = in.next();
//					System.out.print("Port: ");
//					String port = in.next();
//					String msg = type + "|" + vers + "|" + port;
//
//					System.out.println(msg);
//					dest = InetAddress.getByName("127.0.0.1");
//					socket = new DatagramSocket();
//					packet = new DatagramPacket(msg.getBytes(), msg.length(),
//							dest, 50005);
//					socket.send(packet);
//					System.out.println(type + " message sent:     " + msg);
//					break;
//				case 2:
//					System.out.print("Version: ");
//					String version = in.next();
//					System.out.print("Region: ");
//					String region = in.next();
//					System.out.print("SessionID: ");
//					String sessionID = in.next();
//					System.out.print("From station: ");
//					String fromStation = in.next();
//					System.out.print("To station: ");
//					String toStation = in.next();
//					System.out.print("Search criteria: ");
//					String searchCriteria = in.next();
//					String str = "request|" + version + "|" + region + "|"
//							+ sessionID + "|" + fromStation + "|" + toStation
//							+ "|" + searchCriteria;

				
				try{
				
					String str = "alive|1|region1|123456|1234|1231";
					
					
					System.out.println(str);
					dest = InetAddress.getByName("127.0.0.1");
					socket = new DatagramSocket();
					packet = new DatagramPacket(str.getBytes(), str.length(),
							dest, 1231);
					socket.send(packet);
					System.out.println("Request message sent:     " + str);
					
					
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			}
		}
