package internals;

import java.util.HashMap;

import common.Route;

public class ClientSessionHandler {

	//HashMap used to saved session data.
	//Session ID is used as hashKey
	private HashMap<Integer, Session> activeSessions = new HashMap<Integer, Session>();

	// Singleton:
	private static ClientSessionHandler instance = new ClientSessionHandler();

	/**
	 * Get the singleton
	 * 
	 * @return the Singleton
	 */
	public static ClientSessionHandler getInstance() {
		return instance;
	}

	private ClientSessionHandler() {
	}

	/**
	 * Creates a Session.
	 * 
	 * @param timeOutInMS
	 *            This value determines in how many milliseconds the session
	 *            times out.
	 * 
	 * @return The sessionID of the session that was created. Will return -1 if
	 *         the CommonProtoCol listener is not active.
	 */
	public synchronized int createNewSession(Thread caller, String fromStation,
			String toStation) {
		// Set default value.
		int returnVal = -1;

		// If commonProtocol listener is alive, create a new session and set the
		// SessionID as the return value
		if (LocalRegionalServer.commonProtoUDP.isAlive()) {
			Session newSession = new Session(caller, fromStation, toStation);
			// Add session to activeSessions list
			activeSessions.put(newSession.sessionID, newSession);
			returnVal = newSession.sessionID;
		}
		return returnVal;
	}

	/**
	 * Removes a session from the active sessions
	 * 
	 * @param SessionID
	 *            The sessionID of the session to remove
	 */
	public void removeSession(int sessionID) {

		if (activeSessions.containsKey(sessionID))
			activeSessions.remove(sessionID);
	}

	/**
	 * Saves data for a sessionID so it can be picked up by the thread
	 * 
	 * @param sessionID
	 *            The session ID
	 * @param data
	 *            The data for the session
	 */
	public void setSessionData(int sessionID, Route data) {
		//Find the session and insert the data
		if (activeSessions.containsKey(sessionID)) {
			Session thisSession = activeSessions.get(sessionID);
			thisSession.data = data;
			// Data is ready. Now notify thread
			if (thisSession.refThread != null){
				synchronized (thisSession.refThread) {
					System.out.println("Trying to notify thread");
					thisSession.refThread.notify();
				}				
			}else{
				//thread is null. Response must have come too late.
				//remove the session from the datastore
				removeSession(sessionID);
			}
		}
	}

	/**
	 * Returns the route that matches the sessionID parsed If no data can be
	 * found, it returns null-m
	 * 
	 * @param SessionID
	 *            The Session ID
	 * @return The route for the session
	 */

	public Route getSessionData(int sessionID) {
		Route result = null;
		if (activeSessions.containsKey(sessionID))
			result = activeSessions.get(sessionID).data;
			removeSession(sessionID);
		return result;
	}

	/**
	 * Checks if there is data for a session
	 * 
	 * @param sessionID
	 *            The sessionID
	 * @return Returns true if there is data in the route object
	 */
	public boolean checkForData(int sessionID) {
		boolean result = false;
		if (activeSessions.containsKey(sessionID)) {
			if (activeSessions.get(sessionID).data != null)
				result = true;
		}
		return result;
	}

	/**
	 * Finds the from station name for a session ID. Returns "" if no session is
	 * found
	 * 
	 * @param sessionID
	 *            The sessionID to find
	 * @return The name of the from station for this session
	 */
	public String getFromStation(int sessionID) {
		String result = "";
		if (activeSessions.containsKey(sessionID))
			result = activeSessions.get(sessionID).fromStation;
		return result;
	}

	/**
	 * Finds the to station name for a session ID. Returns "" if no session is
	 * found
	 * 
	 * @param sessionID
	 *            The sessionID to find
	 * @return The name of the to station for this session
	 */
	public String getToStation(int sessionID) {
		String result = "";
		if (activeSessions.containsKey(sessionID))
			result = activeSessions.get(sessionID).toStation;
		return result;
	}

	/**
	 * Internal session class. Holds data about the clients request. Also a
	 * place to store the object that is sent back
	 * 
	 * @author Christian
	 * 
	 */
	private class Session {

		public int sessionID;
		public Thread refThread;
		public Route data = null;
		public String fromStation, toStation;

		public Session(Thread caller, String fromStation, String toStation) {

			this.refThread = caller;
			
			//generate unused sessionID
			while (activeSessions.containsKey(this.sessionID = (int) (10000 + (10000 * Math.random()))))
			System.out.println("Generated session: " + this.sessionID);
			
			this.fromStation = fromStation;
			this.toStation = toStation;
		}

	}

}
