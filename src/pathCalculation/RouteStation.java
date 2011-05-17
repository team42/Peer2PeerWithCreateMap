package pathCalculation;



public class RouteStation {
	private double g = 0, h = 0;// , f;
	private RouteStation cameFrom = null;
	private Station station;

	public RouteStation(Station station) {
		this.station = station;
	}

	// Estimated total distance from start to goal through y
	public double f() {
		// return f = g + h;
		return round(g + h);
	}

	// Distance from start along optimal path
	public double g() {
		return round(g);
	}

	public void g(double g) {
		this.g = g;
	}

	// Heuristic estimate of distance: h(start, goal)
	public void h(double h) {
		this.h = h;
	}

	public double h() {
		return round(h);
	}

	public double lat() {
		return station.latitude;
	}

	public double lon() {
		return station.longitude;
	}

	public RouteStation cameFrom() {
		return cameFrom;
	}

	public void cameFrom(RouteStation cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Station station() {
		return station;
	}

	public double round(double d) {
		return ((double) Math.round(d * 100)) / 100;
	}

	public boolean equals(Object s) {
		if (this.station().equals(((RouteStation) s).station())) {
			return true;
		} else {
			return false;
		}
	}	
	
}