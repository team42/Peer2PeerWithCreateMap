package map;

import common.*;

import java.awt.Color;
import java.awt.Graphics;

public class S2 {
	M2 F; // Flip matrix
	M2 S; // Scalerings matrix
	M2 T; // Transformations matrix
	V2 O; // Pos af Origo i pix

	S2(int Sx, int Sy, int Ox, int Oy) {
		F = new M2(1, 0, 0, -1);
		S = new M2(Sx, 0, 0, Sy);
		T = S.mul(F);
		O = new V2(Ox, Oy);
	}

	V2 transformer(V2 p) {
		return T.mul(p).add(O);
	}

	void drawPoint(Graphics g, V2 p, int k) {
		V2 px = transformer(p);
		g.drawOval((int) px.x - (k / 2), (int) px.y - (k / 2), k, k);
	}

	void drawString(Graphics g, String str, V2 position, int offSetX,
			int offSetY) {
		V2 px = transformer(position);
		g.drawString(str, (int) px.x + offSetX, (int) px.y + offSetY);
	}

	void drawString(Graphics g, String str, int X, int Y) {
		V2 position = new V2(X, Y);
		V2 px = transformer(position);
		g.drawString(str, (int) px.x, (int) px.y);
	}

	void drawString(Graphics g, String str, V2 position) {
		V2 px = transformer(position);
		g.drawString(str, (int) px.x, (int) px.y);
	}

	void drawPoint(Graphics g, V2 p) {
		V2 px = transformer(p);
		g.drawOval((int) px.x, (int) px.y, 3, 3);
	}

	void drawStation(Graphics g, String name, V2 p, boolean printName) {
		V2 px = transformer(p);
		g.setColor(Color.BLACK);
		g.drawOval((int) px.x, (int) px.y, 3, 3);
		g.setColor(Color.GRAY);
		drawString(g, (printName ? name : ""), (int) p.x, (int) p.y);
	}
	
	void drawStation(Graphics g, String name, V2 p) {
		drawStation(g, name, p, true);
	}

	void drawStation(Graphics g, String name, int X, int Y) {
		V2 position = new V2(X, Y);
		drawStation(g, name, position);
	}

	void drawStation(Graphics g, SerializableStation station, boolean printName) {
		V2 position = new V2(station.latitude, station.longitude);
		drawStation(g, (printName ? station.stationName : ""), position);
	}
	
	void drawStation(Graphics g, SerializableStation station) {
		drawStation(g, station, true);
	}

	void drawLine(Graphics g, V2 p1, V2 p2) {
		V2 p1x = transformer(p1);
		V2 p2x = transformer(p2);
		g.drawLine((int) p1x.x, (int) p1x.y, (int) p2x.x, (int) p2x.y);
	}

	void drawStationLine(Graphics g, SerializableStation station1,
			SerializableStation station2, Boolean highlight) {
		V2 p1 = new V2(station1.latitude, station1.longitude);
		V2 p2 = new V2(station2.latitude, station2.longitude);
		V2 p1x = transformer(p1);
		V2 p2x = transformer(p2);

		if (!highlight)
			g.setColor(Color.LIGHT_GRAY);
		else
			g.setColor(Color.GREEN);

		g.drawLine((int) p1x.x, (int) p1x.y, (int) p2x.x, (int) p2x.y);
	}

	void drawStationLine(Graphics g, SerializableStation station1, SerializableStation station2) {
		drawStationLine(g, station1, station2, false);
	}

	void drawAxis(Graphics g) {
		drawLine(g, new V2(0, 0), new V2(1, 0));
		drawLine(g, new V2(0, 0), new V2(0, 1));
	}

	public void drawCircle(Graphics g, V2 p, double radius) {
		V2 px = new V2(-radius, radius);
		V2 pc = transformer(p.add(px));
		g.drawOval((int) pc.x, (int) pc.y, (int) (2 * radius * S.a),
				(int) (2 * radius * S.d));
	}
}
