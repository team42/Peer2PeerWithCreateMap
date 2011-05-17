package peer;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import model.Peer;

import command.CommandController;

import config.Configuration;

/**
* Server thread. Waits for messages and responds to requests.
*
**/
public class UDPListenThread extends Thread {
   
   public int port;
   public InetAddress localIP = null;
   public Configuration config = Configuration.getConfiguration();
   
   private CommandController comCon = new CommandController();
   
   /**
   * Constructor
   **/
   public UDPListenThread(int port) {     
      this.port  = port;
      
      try {
         localIP = InetAddress.getLocalHost();
      } catch (UnknownHostException e) {
         System.out.println(e);
      }
   }
   
   public void run() {
      try {
         DatagramSocket peerSocket = new DatagramSocket(port);
         byte[] queryRaw = new byte[1024];
         
         while(true) {
            // Creates a packet to hold the received data
            DatagramPacket receivePacket = new DatagramPacket(queryRaw, queryRaw.length);
            peerSocket.receive(receivePacket);
            
            String query = new String(receivePacket.getData(), 0, receivePacket.getLength());
            
            comCon.processRequest(query, peerSocket, receivePacket);                             
         }
      } catch(IOException e) {
         e.printStackTrace();         
      }
   }

}
