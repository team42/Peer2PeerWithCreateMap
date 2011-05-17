package map;

import javax.swing.*;

import map.Map.PaintPanel;
import pathCalculation.Station;
import common.*;
import java.awt.*;
import java.util.*;;

public class Map2 extends JPanel {
	
	Map2paint paintPanel = new Map2paint();

	public Map2() {
		System.out.println("Oy!!");
	}
	
	public Map2(ArrayList<Coordinates> a) 
	{
		paintPanel.b.addAll(a);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintPanel.draw(g);
	}

	public void setRoute(int[] route)
	{
		paintPanel.setRoute(route);
		repaint();
	}			
}
