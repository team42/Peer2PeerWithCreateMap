package pathCalculation;

import internals.Database;
import common.CommWrapper;
import common.Route;

public class AStarTest {
	public static void main(String args[]) {

		// stations
		String s1 = "Holmens Kirke";
		String s2 = "Vodroffsvej";
		
		int sid1 = Database.getInstance().getStationID(s1);
		int sid2 = Database.getInstance().getStationID(s2);
		
		Database.getInstance().init();
		
		System.out.println("sid1: " + sid1 + ", sid2: " + sid2);

		CommWrapper commWrapper = new CommWrapper();
		Route request = new Route(s1, s2);
		commWrapper.setData(request);

		// A*
		AStar aStar = new AStar();//getInstance();
		commWrapper = aStar.getRoute(commWrapper);

		System.out.println("Calculated route: \n" + commWrapper);
	}
}
