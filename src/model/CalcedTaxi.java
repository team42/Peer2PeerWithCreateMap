package model;

public class CalcedTaxi {

	private String taxiID;
	private String taxiCoord;
	private String company;
	private int shortestPath;
	
	public CalcedTaxi(String taxiID, String taxiCoord, String company, int shortestPath) {
		this.taxiID = taxiID;
		this.taxiCoord = taxiCoord;
		this.company = company;
		this.shortestPath = shortestPath;
	}
	
	public String getTaxiID() {
		return taxiID;
	}
	
	public String getTaxiCoord() {
		return taxiCoord;
	}
	
	public String getCompanyIP() {
		return company;
	}
	
	public int getShortestPath() {
		return shortestPath;
	}
	
	public void setShortestPath(int shortestPath) {
		this.shortestPath = shortestPath;
	}
}
