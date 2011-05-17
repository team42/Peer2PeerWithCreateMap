package map;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;

import model.Taxi;

import database.TaxiDAO;

public class TestSqlDb {

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
int[] route = {233, 732, 361, 556, 512, 193};
		ArrayList<Taxi> q = new ArrayList<Taxi>();
		 //ArrayList<Coordinates> stations = new ArrayList<Coordinates>();
		
		TaxiDAO t = new TaxiDAO();
		t.updateTaxiPosition("000001", "0646,0125");
		t.updateTaxiPosition("000002", "0732,0298");
		
		//Map2 m = new Map2();
		//Map.PaintPanel p;
		q = t.getActiveTaxis();
		Algorithm a = new Algorithm();
		a.Algorithm();
		System.out.println("Active Taxis size: " + t.getActiveTaxis().size());
		int taxiX;
		int taxiY;
		int taxiID;
		
		for(int i = 0; i < q.size(); i++)
		{
		taxiX = Integer.parseInt(q.get(i).getTaxiCoord().substring(0, 4));
		taxiY = Integer.parseInt(q.get(i).getTaxiCoord().substring(6, 9));
		taxiID = Integer.parseInt(q.get(i).getTaxiID());
		System.out.println("taxiX[" + i + "]: " + taxiX);
		System.out.println("taxiY[" + i + "]: " + taxiY);
		System.out.println("taxiID[" + i + "]: " + taxiID);
		a.findClosestPoint(taxiX, taxiY, taxiID);
		}
		
		SqlDb2 v = new SqlDb2();
		TaxiMap frame = new TaxiMap(a.Algorithm()); //v.write()
		frame.setTitle("Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(2020, 1020);
		frame.setBackground(Color.WHITE);
		frame.setLocationRelativeTo(null); // Center the frame
		frame.setVisible(true);
		
		frame.setRoute(route);
		
//		m.setRoute(route);
//		m.rePaint();
//		p.drawRoute(g, route);
	}

}
