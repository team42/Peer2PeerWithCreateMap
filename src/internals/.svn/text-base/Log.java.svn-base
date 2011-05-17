package internals;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * All functionality regarding log
 * @author SG Security Group
 */
public class Log {
	
	//for timestamps in log file
	final static SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
	//for creating the log file for the day:
	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //singleton
    private static final Log instance = new Log();

    /**
     * Default Constructor
     */
    private Log() {
    }

    /**
     * Default singleton method
     * @return an instance of Log class
     */
    public static Log getInstance() {
        return instance;
    }

    /**
     * Prints out string and logs to log file
     * @param string
     */
    public void printAndLog(String string){
    	System.out.println(string);
    	writeToLogFile(string);
    }
    
    
    /**
     * Prints out string and logs to log file
     * @param string
     */
    public void log(String string){
    	writeToLogFile(string);
    }
    
    
    /**
     * Prints out string and logs to log file
     * @param string
     */
    public void LogE(String string){
    	writeToLogFile("ERROR - " + string);
    	writeToLogFile(string);
    }
    
    
    /**
     * Prints out as error, and logs to log file
     * @param string
     */
    public void printAndLogE(String string){
    	System.err.println(string);
    	writeToLogFile("ERROR - " + string);
    }
    
    
    /**
     * Write data to log
     */
    public void writeToLogFile(String string) {

        Calendar cal = Calendar.getInstance();

        try {
            // Create file
            FileWriter filewrite = new FileWriter(
                    "./log/" + sdf.format(cal.getTime()) +
                    ".log", true);

            BufferedWriter output = new BufferedWriter(filewrite);

            output.write("\r\n" + time.format(cal.getTime()) + ": "
                  +string);

            //Close the output stream
            output.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}