package pathCalculation;

import internals.Database;

import java.util.ArrayList;

import common.Route;

public class cmlTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database.getInstance().init();
		
		BSWorld bsworld = new BSWorld();
		
		ArrayList<Route> bsdata1 = new AStar().getBorderstationRoutes(10);
		ArrayList<Route> bsdata2 = new AStar().getBorderstationRoutes(20);

		// add bsdata1
		for (Route route : bsdata1) {
			bsworld.addRoute(route);
		}

		// add bsdata2
		for (Route route : bsdata2) {
			bsworld.addRoute(route);
		}

		Dijkstra myDijkstra = new Dijkstra();
		System.out.println(myDijkstra.getRoute(bsworld.stations, Database.getInstance().getStation(10).stationName, Database
				.getInstance().getStation(20).stationName));
	}

}
