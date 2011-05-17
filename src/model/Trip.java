package model;

import java.util.*;

/**
 * 
 * Trip model which is used for storing trip informations
 * 
 * @author Nicolai
 *
 */
public class Trip {

	private String tripID;
	private String coords;
	private int accepted;
	private Date date;
	
	/**
	 * Constructor
	 * 
	 * trip ID, accepted status and trip coordinate is required to make the model.
	 * Date will be set automatically
	 * 
	 * @param tripID
	 * @param accepted
	 * @param coords
	 */
	public Trip(String tripID, int accepted, String coords) {
		this.tripID = tripID;
		this.accepted = accepted;
		this.coords = coords;
		date = Calendar.getInstance().getTime();
	}
	
	/**
	 * Constructor
	 * 
	 * trip ID, accepted status, trip coordinate and date is required to make the model.
	 * 
	 * @param tripID
	 * @param accepted
	 * @param coords
	 * @param date
	 */
	public Trip(String tripID, int accepted, String coords, Date date) {
		this.tripID = tripID;
		this.accepted = accepted;
		this.coords = coords;
		this.date = date;
	}
	
	/**
	 * Return trip id
	 * 
	 * @return
	 */
	public String getTripID() {
		return tripID;
	}
	
	/**
	 * Return accepted status
	 * 
	 * @return
	 */
	public int getAccepted() {
		return accepted;
	}
	
	/**
	 * Return trip coordinate
	 * 
	 * @return
	 */
	public String getCoords() {
		return coords;
	}
	
	/**
	 * Return the date
	 * 
	 * @return
	 */
	public Date getDate() {
		return date;
	}
}
