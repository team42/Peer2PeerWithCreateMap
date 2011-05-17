package model;

/**
 * Object representation of values read in from "peers" text file.
 * 
 * @author Lasse
 *
 */
public class Peer {
   private String ip;
   private int status;

   public Peer(String ip, int status) {
      this.ip = ip;
      this.status = status;
   }

   public String getIp() {
      return this.ip;
   }
   
   public int getStatus() {
      return this.status;
   }
   
   public String toString() {
      return ip + "," + status;      
   }

}
