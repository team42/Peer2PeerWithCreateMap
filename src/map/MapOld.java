package map;

import javax.swing.*;

import pathCalculation.Station;
import common.*;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


	

public class MapOld extends JFrame {

	ArrayList<SerializableStation> stationArray = new ArrayList<SerializableStation>();
	ArrayList<SerializableStation[]> connectionArray;
	private int[] connsWanted;
	Sqldb sqlDb = new Sqldb();

	/**
	 * Normal constructor
	 * @param myStations
	 * @param connections
	 */
	public MapOld(SerializableStation[] myStations, ArrayList<SerializableStation[]> connections) {
		loadStations(myStations);
		connectionArray = connections;
		add(new PaintPanel());
	}

	/**
	 * Constructor that takes stations instead of {@link SerializableStation}.
	 * @param myStations
	 * @param connections
	 */
	public MapOld(Station[] myStations, ArrayList<Station[]> connections, int[] connsWanted) {
		
		this.connsWanted = connsWanted;
		
		SerializableStation[] mySerStations = new SerializableStation[myStations.length+1];	
		
		for (int i = 0; i < myStations.length; i++) {
			mySerStations[i] = myStations[i].serStation;
			
		}
		
		ArrayList<SerializableStation[]> connectionsTemp = new ArrayList<SerializableStation[]>();
		
		for (Station[] stations : connections) {
			SerializableStation[] thisPair = {stations[0].serStation ,stations[1].serStation};
			connectionsTemp.add(thisPair);
			SerializableStation[] thisPair1 = {stations[1].serStation ,stations[0].serStation};
			connectionsTemp.add(thisPair1);
		}
		
		loadStations(mySerStations);
		connectionArray = connectionsTemp;
		add(new PaintPanel());
	}

	public void loadStations(SerializableStation[] myStations) {
		for (int i = 0; i < myStations.length - 1; i++) {
			stationArray.add(myStations[i]);
		}
	}

	@SuppressWarnings("serial")
	class PaintPanel extends JPanel {
		S2 S2 = new S2(1, 1, 10, 1000);

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			S2.drawAxis(g);

			S2.drawLine(g, new V2(0, 0), new V2(0, 1000));
			S2.drawLine(g, new V2(0, 1000), new V2(1000, 1000));
			S2.drawLine(g, new V2(1000, 1000), new V2(1000, 0));
			S2.drawLine(g, new V2(1000, 0), new V2(0, 0));

			g.setColor(Color.BLACK);
			// i indexes each element successively
			for (int i = 0; i < stationArray.size(); i++) {
				S2.drawStation(g, stationArray.get(i), (stationArray.get(i).stationid > 999000 ? true : false));
//				System.out.println("stationArray[" + i +"] - ID: " + stationArray.get(i).stationid);				
//				System.out.println("connsWanted[" + i +"]: " + connsWanted[i]);				
			}
			
			// draw connections
			for (int i = 0; i < connectionArray.size(); i++) {
				//System.out.println("For i = "+i);
				S2.drawStationLine(g, connectionArray.get(i)[0], connectionArray.get(i)[1], true);
				//System.out.println("connectionArray[0] - stationid (fra): "+connectionArray.get(i)[0].stationid);
//				System.out.println("connectionArray[0] - latitude: "+connectionArray.get(i)[0].latitude);
//				System.out.println("connectionArray[0] - longitude: "+connectionArray.get(i)[0].longitude);
				//System.out.println("connectionArray[1] - stationid (til): "+connectionArray.get(i)[1].stationid);
				//System.out.println("-------------------------------------------");
//				System.out.println("connectionArray[1] - latitude: "+connectionArray.get(i)[1].latitude);
//				System.out.println("connectionArray[1] - longitude: "+connectionArray.get(i)[1].longitude);
				
			}
			sqlDb.write(stationArray, connectionArray, connsWanted);
		}

		
	}
}