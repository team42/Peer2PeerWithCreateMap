package pathCalculation;

public class Neighbor {

	double cost, price;

	Station stationRef;

	public Neighbor(Station stationRef, double cost, double price) {
		this.stationRef = stationRef;
		this.cost = cost;
		this.price = price;
	}

	@Override
	public String toString() {
		return stationRef.stationName + "[cost=" + cost + ", price=" + price + ", stationRef=" + stationRef + "]";
	}

	public boolean equals(Object n) {
		if (this.stationRef.equals(((Neighbor) n).stationRef)) {
			return true;
		}
		return false;
	}
}
