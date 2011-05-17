package pathCalculation;

import java.util.ArrayList;

import common.Route;

import internals.BSData;
import internals.LocalRegionalServer;

public class BSWorld {

	ArrayList<Station> stations = new ArrayList<Station>();

	public BSWorld() {
		
	}
	
	public void init() {
		// bsdata from the other regions
		ArrayList<BSData>[] foreignRegionBSData = LocalRegionalServer.foreignRegionBSData;

		// debug
		// try {
		// foreignRegionBSData[2] = LocalRegionalServer.localBsDataArray;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// System.out.println("foreignRegionBSData size: " + foreignRegionBSData.length);

		// borderstations
		// for each region
		for (ArrayList<BSData> regionBSData : foreignRegionBSData) {
			// no bsdata is found for this region
			// otherwise bsdata must exist
			if (regionBSData == null)
				continue;

			// System.out.println("regionBSData size: " + regionBSData.size());

			// for each route within the region
			for (BSData routeData : regionBSData) {

				Station fromStation = new Station(routeData.stations.get(0));

				// add to borderstations if not already in it
				if (!stations.contains(fromStation))
					stations.add(fromStation);
			}
		}

		// System.out.println("Borderstations size: " + stations.size());

		// connections
		// for each region
		for (ArrayList<BSData> bsdata : foreignRegionBSData) {

			// no bsdata received for this region
			// otherwise bsdata must exist
			if (bsdata == null)
				continue;

			// for each route within the region
			for (BSData routedata : bsdata) {

				// create real station objects
				Station fromStationTemp = new Station(routedata.stations.get(0));
				Station toStationTemp = new Station(routedata.stations.get(routedata.stations.size() - 1));

				// get station reference or create new station objects, if they do not exist
				Station fromStation = (stations.contains(fromStationTemp)) ? stations.get(stations
						.indexOf(fromStationTemp)) : fromStationTemp;
				Station toStation = (stations.contains(toStationTemp)) ? stations.get(stations.indexOf(toStationTemp))
						: toStationTemp;

				// create neighbor object to add to fromStation
				Neighbor toStationNeighbor = new Neighbor(toStation, routedata.totalDistance, routedata.totalDistance);

				// add neighbor to station if it does not already exist
				if (!fromStation.neighbors.contains(toStationNeighbor))
					fromStation.neighbors.add(toStationNeighbor);
			}
		}
	}

	public ArrayList<Neighbor> getNeighbors(Station station) {
		int index = stations.indexOf(station);
		if (index != -1)
			return stations.get(index).neighbors;
		return null;
	}

	public void addRoute(Route route) {
		Station fromStation = new Station(route.route.get(0));
		Station toStation = new Station(route.route.get(route.route.size() - 1));

		// add to borderstations if not already in it
		int fromStationIndex = stations.indexOf(fromStation);
		int toStationIndex = stations.indexOf(toStation);

		if (fromStationIndex == -1) {
			stations.add(fromStation);
		} else {
			fromStation = stations.get(fromStationIndex);
		}
		if (toStationIndex == -1) {
			stations.add(toStation);
		} else {
			toStation = stations.get(toStationIndex);
		}

		// create neighbor object to add
		// Neighbor fromNeighbor = new Neighbor(fromStation, route.cost, route.price);
		Neighbor toNeighbor = new Neighbor(toStation, route.cost, route.price);

		// add neighbor to station if it does not already exist
		int fromNeighborIndex = fromStation.neighbors.indexOf(toNeighbor);
		// int toNeighborIndex = toStation.neighbors.indexOf(fromNeighbor);

		if (fromNeighborIndex == -1)
			fromStation.neighbors.add(toNeighbor);
		// if (toNeighborIndex == -1)
		// toStation.neighbors.add(toNeighbor);

	}

	public static void debugRunAndPrint() {
		System.out.println("BSWorld running...");
		BSWorld bsworld = new BSWorld();
		bsworld.init();
		// System.out.println("Init ok...");
		// for (Station s : bsworld.borderstations) {
		// System.out.println(s.toString() + ", neighbors: \n\t" + s.neighborString());
		// }

		// neighbors for a random station
		// Station s = bsworld.stations.get(0);
		// System.out.println("Neighbors for station: " + s.stationName);
		// for (Neighbor n : s.neighbors)
		// System.out.println("\t" + n.stationRef.stationName + ": cost=" + n.cost + " price=" + n.price);
	}
}