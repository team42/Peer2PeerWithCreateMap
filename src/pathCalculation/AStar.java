package pathCalculation;

import internals.Database;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import common.CommWrapper;
import common.Route;

public class AStar {
	// singleton instance
	// private static AStar instance = new AStar();

	// define start and goal
	private RouteStation start;
	private RouteStation goal;

	// Load stations
	// ArrayList<Station> stations = Database.getInstance().getStations();

	// pathStations already worked with
	private ArrayList<RouteStation> usedPaths = new ArrayList<RouteStation>();

	// The set of tentative nodes to be evaluated
	private Comparator<RouteStation> comparator = new StationComparator();
	private PriorityQueue<RouteStation> open = new PriorityQueue<RouteStation>(10, comparator);

	// The set of nodes already evaluated
	private ArrayList<RouteStation> closed = new ArrayList<RouteStation>();

	// Final route to return
	private Route route;

	// Does both to and from exist
	private boolean stationsExists = true;
	private boolean fromErr = false;
	private boolean toErr = false;

	/**
	 * empty constructor
	 */
	public AStar() {
	}

	/**
	 * 
	 * @param commWrapper
	 */
	public void findFromToStations(CommWrapper commWrapper) {
		String fromStation = ((Route) commWrapper.getData()).fromStation;
		String toStation = ((Route) commWrapper.getData()).toStation;

		findFromToStations(fromStation, toStation);
	}

	/**
	 * parse stationnames to check, if the exists
	 * 
	 * @param fromStation
	 *            String containing the name of the "from" station
	 * @param toStation
	 *            String containing the name of the "to" station
	 */
	public void findFromToStations(String fromStation, String toStation) {

		int fromStationID = Database.getInstance().getStationID(fromStation);
		int toStationID = Database.getInstance().getStationID(toStation);

		if (fromStationID == -1 || toStationID == -1) {
			// one of the stations could not be found
			fromErr = (fromStationID == -1 ? true : false);
			toErr = (toStationID == -1 ? true : false);
			stationsExists = false;

		} else {
			// both stations found in database

			Station from = Database.getInstance().getStation(fromStationID);
			Station to = Database.getInstance().getStation(toStationID);

			System.out.println("Shortest path: " + from.stationName + " -> " + to.stationName);

			// Instantiate route object to return to client
			route = new Route(from.stationName, to.stationName);

			start = getRouteStation(from);
			goal = getRouteStation(to);
		}
	}

	/**
	 * does the stations exist? if not, write errormsg to console
	 * 
	 * @return
	 */
	public boolean stationsExists() {
		return stationsExists;
	}

	/**
	 * 
	 * @param fromStation
	 * @param toStation
	 * @return CommWrapper object containing the route as a Route object
	 */
	public CommWrapper getRoute(String fromStation, String toStation) {
		Route route = new Route(fromStation, toStation);
		CommWrapper commWrapper = new CommWrapper();
		commWrapper.setData(route);

		return getRoute(commWrapper);
	}

	public CommWrapper getRoute(CommWrapper commWrapper) {
		// check if from and to exists
		findFromToStations(commWrapper);

		// if stations exists, continue
		if (stationsExists()) {
			commWrapper.setData(getRoute());
		} else {
			String errMsg = "";
			if (fromErr)
				errMsg += ((Route) commWrapper.getData()).fromStation;
			if (toErr)
				errMsg += (fromErr ? " and " : "") + ((Route) commWrapper.getData()).toStation;
			errMsg += " could not be found!";
			commWrapper.setError(errMsg);
		}

		return commWrapper;
	}

	/**
	 * get a complete calculated route when given the from- and to-stationID's
	 * 
	 * @param fromStationID
	 * @param toStationID
	 * @return Route object containing the complete calculated route
	 */
	public Route getRoute(int fromStationID, int toStationID) {

		Station from = Database.getInstance().getStation(fromStationID);
		Station to = Database.getInstance().getStation(toStationID);

		// System.out.println("Shortest path: " + from.stationName + " -> "
		// + to.stationName);

		// Instantiate route object to return to client
		route = new Route(from.stationName, to.stationName);

		start = getRouteStation(from);
		goal = getRouteStation(to);

		return getRoute();
	}

	/**
	 * Returns a route from two station names. The method will check if the stations exist before calculating the route.
	 * The method will return NULL if either from or to station doesn't exist in the database
	 * 
	 * @param fromStation
	 *            Name of the from station
	 * @param toStation
	 *            Name of the destination station
	 * @return Returns the calculated Route in a Route object or NULL.
	 */

	public Route getRouteResponse(String fromStation, String toStation) {

		Route thisRoute = null;
		// Checks if the stations exist and fills variables in the class before
		// the search
		findFromToStations(fromStation, toStation);
		if (stationsExists()) {
			// if stations exists, perform the search.
			thisRoute = getRoute();
		}

		return thisRoute;
	}

	/**
	 * routes from borderstations to station inside our region
	 * 
	 * @param stationName
	 *            where to
	 * @return ArrayList containing Route objects with routes from every borderstation inside the region to the goal
	 *         station
	 */
	public ArrayList<Route> getBorderstationRoutes(int stationID) {

		// TODO for each borderstation -> new AStar();
		
		// Station toStation;

		// error if station does not exist; return false
		// int stationID = Database.getInstance().getStationID(stationName);
		// if (stationID == -1) {
		// return null;
		// } else {
		Station toStation = Database.getInstance().getStation(stationID);
		// }

		ArrayList<Station> borderstations = Database.getInstance().getBorderStations();
		ArrayList<Route> borderRoutes = new ArrayList<Route>();

		for (Station bs : borderstations) {
			// set start/goal
			start = getRouteStation(bs);
			goal = getRouteStation(toStation);

			// calculate route
			route = new Route(bs.stationName, toStation.stationName);

			// calculate route
			getRoute();

			// add to borderRoutes
			borderRoutes.add(route);
		}

		return borderRoutes;
	}

	/**
	 * get a complete calculated route when given the from- and to-station objects's
	 * 
	 * @param from
	 * @param to
	 * @return Route object containing the complete calculated route
	 */
	public Route getRoute(Station from, Station to) {

		System.out.println("Shortest path: " + from.stationName + " -> " + to.stationName);

		// Instantiate route object to return to client
		route = new Route(from.stationName, to.stationName);

		start = getRouteStation(from);
		goal = getRouteStation(to);

		// System.out.println("astar: start: " + start.station().stationName + ", goal: " + goal.station().stationName);

		return getRoute();
	}

	/**
	 * calculate the shortest path
	 * 
	 * @return ArrayList containing the route
	 */
	public ArrayList<RouteStation> calculate() {
		return calculate(start, goal);
	}

	/**
	 * calculate the shortest path
	 * 
	 * @param start
	 *            the start pathStation
	 * @param goal
	 *            the goal pathStation
	 * @return ArrayList containing the route
	 */
	public ArrayList<RouteStation> calculate(RouteStation start, RouteStation goal) {
		// Temporary gScore
		double gScoreTmp = 0;

		// Is the new path better
		boolean newIsBetter;

		// add start to open set
		open.add(start);

		// for calculaions
		RouteStation current;

		// as long as there is unvisited nodes in the open set
		while (open.size() > 0) {
			current = open.poll();

			closed.add(current);

			/*
			 * System.out.println("current: " + current.station().stationName + ": " + current.station().stationName +
			 * ", goal: " + goal.station().stationName + ": " + goal.station());
			 */

			if (current.equals(goal)) {
				// System.out.println("GOAL! " + current.station().stationName);
				return backtrackPath();
			}

			// System.out.println("Debug: " + current.station().stationName);

			// for each of the currents stations neighbors
			for (RouteStation neighbor : getNeighbors(current)) {

				// if neighbor has already been visited
				if (closed.contains(neighbor)) {
					/*
					 * System.out.println("Closed contains neighbor: " + neighbor.station().stationName + " (" +
					 * neighbor + ")\n");
					 */
					continue;
				}

				// calculate a temporary g score for comparing to the old
				gScoreTmp = current.g() + cost(current, neighbor);

				// if the neighbor is not in the open set or if the newly
				// calculated g score is better than the previous, then the new
				// path is better
				if (!open.contains(neighbor) || gScoreTmp < neighbor.g()) {
					/*
					 * System.out.println("Closed does NOT contain neighbor: " + neighbor.station().stationName + " (" +
					 * neighbor + ") or \ngScoreTemp > neighbor.g(): " + gScoreTmp + " < " + neighbor.g() + "\n");
					 */
					newIsBetter = true;

				} else {
					// System.out.println("New isn't better\n");
					newIsBetter = false;
				}

				// the path found is better; update the neighbor's data:
				// - set the referrer to current
				// - set g, h
				if (newIsBetter) {
					neighbor.cameFrom(current);
					neighbor.g(gScoreTmp);
					neighbor.h(cost(neighbor, goal));

					/*
					 * System.out.println("Neighbor: " + neighbor.station().stationName + ": g(): " + neighbor.g() +
					 * ", h(): " + neighbor.h() + ", f(): " + neighbor.f());
					 */

					// if the neighbor is not in the open set, add it
					if (!open.contains(neighbor)) {
						open.add(neighbor);
						/*
						 * System.out.println("Neighbor added to open: " + neighbor.station().stationName + "\n");
						 */
					}
				}
			}
		}
		// System.out.println("queue empty; end of closed: " + closed.get(closed.size() - 1).station().stationName);
		return backtrackPath();
	}

	/**
	 * backtrack from goal to start
	 * 
	 * @return ArrayList containing the final route
	 */
	private ArrayList<RouteStation> backtrackPath() {

		// last element of closed set has to be goal
		RouteStation current = closed.get(closed.size() - 1);

		ArrayList<RouteStation> path = new ArrayList<RouteStation>();
		int i = 1;
		while (true) {
			path.add(current);

			// if current has no cameFrom (reference) -> as to be start station
			if (current.cameFrom() == null)
				break;

			// calculate and set the cost of the specific hop
			double hopCost = current.g() - current.cameFrom().g();
			current.cameFrom().station().serStation.cost = ((double) Math.round(hopCost * 100)) / 100;

			current = current.cameFrom();
			i++;
		}
		path = flip(path);
		return path;
	}

	/**
	 * flip the path to start from beginning and ending in goal
	 * 
	 * @param path
	 *            ArrayList containing the inverse route
	 * @return ArrayList containing the route in correct order
	 */
	public ArrayList<RouteStation> flip(ArrayList<RouteStation> path) {
		ArrayList<RouteStation> pathFlip = new ArrayList<RouteStation>();
		for (int i = path.size() - 1; i >= 0; i--) {

			// add to final flipped path
			pathFlip.add(path.get(i));

			// add each of the serializable station objects to the route
			// arraylist in the route object
			route.route.add(path.get(i).station().serStation);
		}
		return pathFlip;
	}

	/**
	 * print each of the stations on the path
	 * 
	 * @param path
	 *            ArrayList containing pathStation objects
	 */
	public void printPath(ArrayList<RouteStation> path) {
		int i = 1;
		for (RouteStation station : path) {
			System.out.println(i++ + ". " + station.station().stationName);
		}
	}

	/**
	 * get neighbors for at specific station
	 * 
	 * @param station
	 *            pathStation object to get neighbors for
	 * @return ArrayList containing the stations as pathStation objects
	 */
	private ArrayList<RouteStation> getNeighbors(RouteStation station) {

		// List of neighbor nodes
		ArrayList<RouteStation> neighbors = new ArrayList<RouteStation>();

		for (Neighbor neighbor : Database.getInstance().getNeighbors(station.station().stationid)) {
			Station s = neighbor.stationRef;

			neighbors.add(getRouteStation(s)); // new pathStation(s)
		}

		// System.out.println("Neighbors for " + station.station().stationName +
		// ":");
		// printPath(neighbors);

		return neighbors;
	}

	private Route getRoute() {

		long time = System.nanoTime();
		calculate();
		time = System.nanoTime() - time;

		// calculated route
		// System.out.println("astar: from: " + route.route.get(0).stationName + " to: "
		// + route.route.get(route.route.size() - 1).stationName);
		//
		// System.out.println("astar input: from: " + route.fromStation + ", to: " + route.toStation);

		route.calcTimeNano = time;
		route.cost = goal.f();
		return route;
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public RouteStation getRouteStation(Station s) {

		// search usedPaths for station
		// for (int i = 0; i < usedPaths.size(); i++) {
		for (RouteStation r : usedPaths) {
			if (r.station().equals(s)) {
				// System.out.println("usedPaths: " + r.station().stationName);
				return r;
			}
		}

		// station not used before, create new
		RouteStation p = new RouteStation(s);
		usedPaths.add(p);
		return p;
	}

	private double cost(RouteStation s1, RouteStation s2) {
		double lon = s1.lon() - s2.lon();
		double lat = s1.lat() - s2.lat();
		return Math.sqrt(lon * lon + lat * lat);
	}

	private class StationComparator implements Comparator<RouteStation> {

		@Override
		public int compare(RouteStation s1, RouteStation s2) {
			// TODO Assume neither pathStations is null.
			// Real code should probably be more robust.

			if (s1.f() < s2.f()) {
				// first object value is less than the second -> return negative
				return -1;
			} else if (s1.f() > s2.f()) {
				// first object value is greater than the second -> return
				// positive
				return 1;
			} else {
				// equal
				return 0;
			}
		}
	}
}