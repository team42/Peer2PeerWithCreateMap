package peer;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import model.Peer;

/**
 * Text file manipulation of "peers"
 * 
 * @author Lasse
 *
 */
public class PeerList {
   private Scanner input;
   private String filename = "peers.txt";
   private BufferedWriter out;

   public PeerList() {
      File f = new File(filename);
      System.out.println(f + (f.exists()? " is found " : " is missing. Creating file..."));
      try {

         if(!f.exists()) {
            f.createNewFile();

            // Read peers file from jar archive
            InputStream in = getClass().getResourceAsStream("/peer/peers");
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line;

            //output to proper file
            while ((line = br.readLine()) != null) {
               out = new BufferedWriter(new FileWriter(filename,true));               
               out.write(line + "\n");
               out.close();
            }            
         }
      } catch (IOException io) {
         System.out.println("File not found");
      }
   }

   /**
    * Opens file for reading or writing.
    *
    * @param filename
    * @param rw 0 = read, 1 = write
    */
   public boolean openFile(int rw) {
      if(rw == 0) {
         try {
            input = new Scanner(new File(filename));
            return true;
         } catch (FileNotFoundException ex) {
            Logger.getLogger(PeerList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
         }
      } else {
         try {
            out = new BufferedWriter(new FileWriter(filename));
            return true;
         } catch (IOException ex) {
            Logger.getLogger(PeerList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
         }
      }
   }

   /**
    * Creates an arraylist from text file.
    * 
    * @return arraylist containing values from file
    */
   public ArrayList<Peer> readPeerList() {
      ArrayList<Peer> peerList = new ArrayList<Peer>();
      if(!(input==null)) {
         while (input.hasNext()) {
            String ip = input.next();
            int status = 1;//input.nextInt();
            peerList.add(new Peer(ip,status));
         }
         return peerList;
      } else {
         return null;
      }
   }

   public void writePeerList(ArrayList<Peer> peerList) throws IOException {
      if (out != null) {         
         for(int i=0;i<peerList.size();i++) {
            out.append(peerList.get(i).getIp());
         }
      }
   }

   /**
    * Closes the file (duh).
    * 
    */
   public void closeInputFile() {
      if(!(input==null)) {
         input.close();
      }      
   }

   public void closeOutputFile() throws IOException {
      if(!(out==null)) {
         out.close();
      }   
   }
}
