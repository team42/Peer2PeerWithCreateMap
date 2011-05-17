package map;

import javax.swing.*;

import pathCalculation.Station;
import common.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Map extends JFrame {

	ArrayList<Coordinates> b = new ArrayList<Coordinates>();
	int[] drawRoute = new int[0];

	public Map(ArrayList<Coordinates> a) 
	{
		b.addAll(a);
		add(new PaintPanel());		
	}

	public Map() {
		System.out.println("Oy!!");
		add(new PaintPanel());
	}

	public void setRoute(int[] route)
	{
		drawRoute = route;
		for (int i=0; i < drawRoute.length; i++)
		{
			System.out.println("[" + i + "] = " + drawRoute[i]);
		}		
		repaint();
	}
	
	public void rePaint ()
	{
		for (int i=0; i < drawRoute.length; i++)
		{
			System.out.println("[" + i + "] = " + drawRoute[i]);
		}
		System.out.println("Repaint her!");
		repaint();
	}
	

	@SuppressWarnings("serial")
	class PaintPanel extends JPanel 
	{		
		CoordinateSystem S2 = new CoordinateSystem (1, 1, 10, 1000);
		
		public void paintComponent(Graphics g) 
		{
			super.paintComponent(g);

			S2.drawAxis(g);

			S2.drawLine(g, new Vektor(0, 0), new Vektor(0, 1000));
			S2.drawLine(g, new Vektor(0, 1000), new Vektor(1000, 1000));
			S2.drawLine(g, new Vektor(1000, 1000), new Vektor(1000, 0));
			S2.drawLine(g, new Vektor(1000, 0), new Vektor(0, 0));

			g.setColor(Color.BLACK);
		
			for (int i = 0; i < b.size(); i++) 
			{		
				S2.drawStation(g, b.get(i).ownX, b.get(i).ownY);			
			}
		
			for (int t = 0; t < b.size(); t++) 
			{
				
				for(int i = b.get(t).NON; i > 0; i--)
				{
					int ax = b.get(t).ownX;
					int ay = b.get(t).ownY;
					
					int neighbor = b.get(t).getNn(i);
					
					int bx = b.get(neighbor).ownX;
					int by = b.get(neighbor).ownY;
					
					S2.drawStationLine(g, ax, ay, bx, by, false);
				}
											
			}
			
			//Draw all taxis
			for(int i = 0; i<b.size(); i++)
			{
				//System.out.println("Draw taxis her!!");
				if(b.get(i).taxi == true)
				{
					S2.drawTaxi(g, b.get(i).ownX, b.get(i).ownY);
				}
			}
			
			
			System.out.println(drawRoute.length);
			if (drawRoute.length > 0)
			{
				System.out.println("hallihalløj");
				System.out.println("drawRoute her!!!");
				int tempId;
				int i = 0;
				while(i < drawRoute.length-1)
				{
					tempId = drawRoute[i];
					int Ax = b.get(tempId).ownX;
					int Ay = b.get(tempId).ownY;
					i++;
					tempId = drawRoute[i];
					int Bx = b.get(tempId).ownX;
					int By = b.get(tempId).ownY;
					
					S2.drawRouteLine(g, Ax, Ay, Bx, By);
					
				}
			}
			
		}

	}
}
