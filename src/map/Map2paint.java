package map;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Map2paint {

	private int[] drawRoute;
	
	ArrayList<Coordinates> b = new ArrayList<Coordinates>();
	CoordinateSystem S2 = new CoordinateSystem (1, 1, 10, 1000);
	
	public Map2paint() {
		drawRoute = new int[0];
	}
	
	public void draw(Graphics g) 
	{
		S2.drawAxis(g);

		S2.drawLine(g, new Vektor(0, 0), new Vektor(0, 1000));
		S2.drawLine(g, new Vektor(0, 1000), new Vektor(1000, 1000));
		S2.drawLine(g, new Vektor(1000, 1000), new Vektor(1000, 0));
		S2.drawLine(g, new Vektor(1000, 0), new Vektor(0, 0));

		g.setColor(Color.BLACK);
		// i indexes each element successively
		System.out.println(b.size());
		for (int i = 0; i < b.size(); i++) 
		{		
			S2.drawStation(g, b.get(i).ownX, b.get(i).ownY);
			System.out.println(i);
		}
		
		System.out.println("HEJ1");
		
		// draw connections
		for (int t = 0; t < b.size(); t++) 
		{
			System.out.println("HEJ2");
			for(int i = b.get(t).NON; i > 0; i--)
			{
				System.out.println("HEJ3");
				int ax = b.get(t).ownX;
				int ay = b.get(t).ownY;
				
				int neighbor = b.get(t).getNn(i);
				
				int bx = b.get(neighbor).ownX;
				int by = b.get(neighbor).ownY;
				
				S2.drawStationLine(g, ax, ay, bx, by, false);
			}
										
		}

		System.out.println("HEJ4");
		
		for (int i=0; i < drawRoute.length; i++)
		{
			System.out.println("[" + i + "] = " + drawRoute[i]);
		}
		
		if (this.drawRoute != null)
		{
			System.out.println("drawRoute her!!!");

			g.setColor(Color.RED);				

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
	
	public void setRoute(int[] route) {
		this.drawRoute = route;
	}
	
}
