package pathCalculation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import common.Route;
import common.SerializableStation;

public class Dijkstra {

	// ArrayList for stations in the map
	private ArrayList<Station> stations;
	
	public Route getRoute(ArrayList<Station> stationList, String fromStation, String toStation) {
		stations = stationList;
		return calculate(fromStation, toStation);
	}

	public Route getDebugRoute(String fromStation, String toStation) {
		return calculate(fromStation, toStation);
	}
	
	private Route calculate(String fromStation, String toStation) {

		// DEBUG. Will be updated when we receive a BSworld and route instead
		// create debug stations
		if (stations==null){
			System.out.println("DEBUG RUN OF DIJKSTRA.");
			stations = new ArrayList<Station>();
			createDebugMap(stations);
		}

		/**
		 * Check if the stations exists
		 */
		Station startStation = null, endStation = null;

		// Find starting station
		for (Station thisStation : stations) {
			if (thisStation.stationName.equals(fromStation)) {
				System.out.println("Found startStaion in the Database");
				startStation = thisStation;
			}
			if (thisStation.stationName.equals(toStation)) {
				System.out.println("Found endStation in the Database");
				endStation = thisStation;
			}
		}
		// Station not found. Break
		if (startStation == null || endStation == null) {
			System.out.println("Shit! One of the stations could not be found");
			return null;
		}

		/**
		 * Declare the variables for the algorithm
		 */

		// ArrayList for keeping track of stations we have visited.
		ArrayList<Station> visitedStations = new ArrayList<Station>();
		// Array for the DijkstraStations. This type holds the current best cost and best link to it.
		ArrayList<DijkstraStation> dijkstraStations = new ArrayList<DijkstraStation>();

		// The station we are examining neighbors from.
		DijkstraStation currentStation = new DijkstraStation(new Neighbor(startStation, 0, 0), 0, startStation);

		// PriorityQueue for sorting unvisited DijkstraStations.
		NeighBorDijktstraComparetor Dijkcomparator = new NeighBorDijktstraComparetor();
		PriorityQueue<DijkstraStation> unvisitedDijkstraStations;
		unvisitedDijkstraStations = new PriorityQueue<DijkstraStation>(100, Dijkcomparator);

		// Variable to keep track of the goal
		boolean goalFound = false; // did we find the goal yet
		int goalBestCost = Integer.MAX_VALUE; // did we find the goal yet

		// Add the start staion to the queue so that the loop will start with this staiton
		unvisitedDijkstraStations.add(currentStation);
		dijkstraStations.add(currentStation);

		/***************************
		 * The Dijkstra algorithm loop.
		 ***************************/

		while (!unvisitedDijkstraStations.isEmpty()) {
			// Take the next station to examine from the queue and set the next station as the currentStation
			currentStation = unvisitedDijkstraStations.poll();

			System.out.println("\n============= New loop passtrough =============");
			System.out.println("Analysing neighbors of " + currentStation.stationRef.stationName + ". Cost to this station is: " + currentStation.totalCost);

			// Add visited so we dont visit again.
			visitedStations.add(currentStation.stationRef);

			// break the algorithm if the current stations totalcost if bigger than the best cost of the goal
			if (goalBestCost < currentStation.totalCost) {
				System.out.println("Best route to the goal has been found. Best cost is: " + goalBestCost
						+ " (To reach current station is: " + currentStation.totalCost + ")");
				break;
			}
			
			// Check all neighbors from the currentStation to the neighBorQueue so we can examine them
			for (Neighbor thisNeighbor : currentStation.stationRef.neighbors) {

				// Skip stations we have already visited
				if (!visitedStations.contains(thisNeighbor.stationRef)) {
					// If we havent seen this station before
					// Add it to the list of Dijkstrastations and to the PriorityQueue of unvisited stations
					if (getDijkstraStationByID(thisNeighbor.stationRef.stationid, dijkstraStations) == null) {
						DijkstraStation thisDijkstraStation = new DijkstraStation(thisNeighbor, currentStation.totalCost
								+ thisNeighbor.cost, currentStation.stationRef);
						dijkstraStations.add(thisDijkstraStation);
						unvisitedDijkstraStations.offer(thisDijkstraStation);

						if (thisNeighbor.stationRef.equals(endStation)) {
							goalFound = true;
							goalBestCost = (int) thisDijkstraStation.totalCost;
							System.out.println("Goal station found :) Cost to goal is: " + goalBestCost);
						}
						System.out.println("New station seen: " + thisDijkstraStation);

					} else {
						// Station has been seen before.

						// Get the station as a DijkstraStation from the array
						DijkstraStation thisDijkstraStation = getDijkstraStationByID(thisNeighbor.stationRef.stationid,
								dijkstraStations);

						// Check if the connection is better than the one we already know
						if (currentStation.totalCost + thisNeighbor.cost < thisDijkstraStation.totalCost) {

							// New best link found
							System.out.println("New best route found for " + thisNeighbor.stationRef.stationName);
							System.out.println("Old cost:" + thisDijkstraStation.totalCost + ", New cost: "
									+ (currentStation.totalCost + thisNeighbor.cost));

							// update the cost and via station on the DijkstraStation
							thisDijkstraStation.updateBestLink((int) (currentStation.totalCost + thisNeighbor.cost),
									currentStation.stationRef, thisNeighbor);

							// if this is the goal station, update the totalCost
							if (thisNeighbor.stationRef.equals(endStation)) {
								goalBestCost = (int) thisDijkstraStation.totalCost;
								System.out.println("Updated cost for the goal to : " + goalBestCost);
							}
						}
					}
				}
			}

		}// End while

		// Print the World:
//		 System.out.println();
//		 System.out.println("################# Printing the world ###################");
//		 for (DijkstraStation thisDijkstraStation : dijkstraStations) {
//		 System.out.println(thisDijkstraStation.toString());
//		 DijkstraStation tempStation = thisDijkstraStation;
//		 System.out.print("BackTracking route =>");
//		 while (!tempStation.stationRef.equals(startStation)) {
//		 System.out.print(" " + tempStation.cost + " to " + tempStation.viaStation.stationName + " #");
//		
//		 tempStation = getDijkstraStationByID(tempStation.viaStation.stationid, dijkstraStations);
//		 }
//		 System.out.print(" End");
//		 System.out.print("\n\n");
//		 }
//
		if (goalFound) {
			System.out.println("Generating route to goal");
			// create a route object with all the stations.
			Route routeToGoal = new Route(fromStation, toStation);
			// get the goalStation as a DijkstraStation Type
			DijkstraStation goalStation = getDijkstraStationByID(endStation.stationid, dijkstraStations);

			routeToGoal.cost = goalStation.totalCost;
			routeToGoal.price = (int) goalStation.totalCost;

			// Find the route to the goal
			DijkstraStation tempStation = goalStation;

			while (!tempStation.stationRef.equals(startStation)) {
				// Add the station to the route table
				SerializableStation thisStation = new SerializableStation(tempStation.cost, tempStation.cost,
						tempStation.stationRef.stationName, tempStation.stationRef.stationid);
				
				//Fix region string
				String regionString="";
				for (Integer regionID : tempStation.stationRef.region) {
					regionString.concat(regionID+ ",");
				}				
				thisStation.region = regionString.substring(0, regionString.length() - 1);
				
				//Add the station
				routeToGoal.route.add(0, thisStation);
				// Go to the previous station
				tempStation = getDijkstraStationByID(tempStation.viaStation.stationid, dijkstraStations);
			}
			// add the starting station
			SerializableStation thisStation = new SerializableStation(tempStation.stationRef.latitude, tempStation.stationRef.longitude, tempStation.stationRef.stationName,
					tempStation.stationRef.stationid);
			routeToGoal.route.add(0, thisStation);

			// print the route
			//System.out.println("Printing best route to the Goal:\n" + routeToGoal.toString());

			return routeToGoal;
		}

		return null;
	}

	/**
	 * Get the station as a known DijktstraStation. It compares by using the station ID
	 * 
	 * @param stationID
	 *            The station ID to find
	 * @param arrayOfDijkstraStations
	 *            The array of DijktstraStations to search
	 * @return Null or the DijktstraStation with the correct ID
	 */
	public DijkstraStation getDijkstraStationByID(int stationID, ArrayList<DijkstraStation> arrayOfDijkstraStations) {
		for (DijkstraStation dijkstraStation : arrayOfDijkstraStations) {
			if (dijkstraStation.stationRef.stationid == stationID)
				return dijkstraStation;
		}
		return null;
	}

	/**
	 * Creates a debug map for use in the Dijkstra algorithm
	 * @param stations
	 */
	public void createDebugMap(ArrayList<Station> stations) {
		Station stationValby = new Station("Valby", 1);
		stations.add(stationValby);

		Station stationFrederiksund = new Station("Frederiksund", 2);
		stations.add(stationFrederiksund);

		Station stationHerlev = new Station("Herlev", 3);
		stations.add(stationHerlev);

		Station stationHvidovre = new Station("Hvidovre", 4);
		stations.add(stationHvidovre);

		Station stationHumsum = new Station("Husum", 5);
		stations.add(stationHumsum);

		Station stationIslev = new Station("Islev", 6);
		stations.add(stationIslev);

		Station stationDanshoj = new Station("Danshoj", 7);
		stations.add(stationDanshoj);

		Station stationSkovlunde = new Station("Skovlunde", 8);
		stations.add(stationSkovlunde);

		// add neighbor connections
		stationValby.neighbors.add(new Neighbor(stationIslev, 4, 4));

		stationIslev.neighbors.add(new Neighbor(stationValby, 4, 4));
		stationIslev.neighbors.add(new Neighbor(stationHumsum, 8, 8));
		stationIslev.neighbors.add(new Neighbor(stationSkovlunde, 3, 3));

		stationHumsum.neighbors.add(new Neighbor(stationHerlev, 3, 3));
		stationHumsum.neighbors.add(new Neighbor(stationFrederiksund, 4, 4));
		stationHumsum.neighbors.add(new Neighbor(stationHvidovre, 2, 2));
		stationHumsum.neighbors.add(new Neighbor(stationDanshoj, 3, 3));
		stationHumsum.neighbors.add(new Neighbor(stationIslev, 8, 8));

		stationHerlev.neighbors.add(new Neighbor(stationHumsum, 3, 3));
		stationHerlev.neighbors.add(new Neighbor(stationFrederiksund, 2, 2));

		stationFrederiksund.neighbors.add(new Neighbor(stationHumsum, 4, 4));
		stationFrederiksund.neighbors.add(new Neighbor(stationHerlev, 2, 2));
		stationFrederiksund.neighbors.add(new Neighbor(stationHvidovre, 5, 5));

		stationHvidovre.neighbors.add(new Neighbor(stationHumsum, 2, 2));
		stationHvidovre.neighbors.add(new Neighbor(stationFrederiksund, 5, 5));
		stationHvidovre.neighbors.add(new Neighbor(stationDanshoj, 4, 4));

		stationDanshoj.neighbors.add(new Neighbor(stationHumsum, 3, 3));
		stationDanshoj.neighbors.add(new Neighbor(stationHvidovre, 5, 5));
		stationDanshoj.neighbors.add(new Neighbor(stationSkovlunde, 10, 10));

		stationSkovlunde.neighbors.add(new Neighbor(stationHumsum, 3, 3));
		stationSkovlunde.neighbors.add(new Neighbor(stationDanshoj, 10, 10));
	}

	/**
	 * Comparator for the DijkstraStationqueue
	 * 
	 * @author Christian
	 * 
	 */
	private class NeighBorDijktstraComparetor implements Comparator<DijkstraStation> {
		@Override
		public int compare(DijkstraStation x, DijkstraStation y) {
			if (x.totalCost < y.totalCost) {
				return -1;
			}
			if (x.totalCost > y.totalCost) {
				return 1;
			}
			return 0;
		}
	}

	/**
	 * Internal private class for stations with Dijkstra fields
	 * 
	 * @author Christian
	 * 
	 */
	private class DijkstraStation extends Neighbor {

		double totalCost = 0;
		Station viaStation = null;

		public DijkstraStation(Neighbor neighbor, double initialCost, Station viaStation) {
			super(neighbor.stationRef, neighbor.cost, neighbor.price);
			this.viaStation = viaStation;
			totalCost = initialCost;
		}

		/*
		 * Sets the new best link to reach this station. Also adds the link cost to the totalcost
		 */
		public void updateBestLink(int newbestCost, Station newbestStation, Neighbor thisNeighbor) {
			stationRef = thisNeighbor.stationRef;
			cost = thisNeighbor.cost;
			price = thisNeighbor.price;

			viaStation = newbestStation;
			totalCost = newbestCost;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DijkstraStation) {
				if (this.stationRef.stationid == ((DijkstraStation) obj).stationRef.stationid)
					return true;
			} else if (obj instanceof Neighbor) {
				if (this.stationRef.stationid == ((Neighbor) obj).stationRef.stationid)
					return true;
			}
			return false;

		}

		@Override
		public String toString() {
			return "DijkstraStation= [Station: " + stationRef.stationName + ", TotalCost: " + totalCost
					+ ", viaStation: " + viaStation.stationName + "] Extends Neighbor";

		}

	}
}
