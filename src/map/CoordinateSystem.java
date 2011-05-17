package map;



import java.awt.*;

import common.SerializableStation;

public class CoordinateSystem {
  Matricer F;
  Matricer S;
  Matricer T;
  Vektor O;
  
 public CoordinateSystem(int sx, int sy, int Ox, int Oy) {
    F=new Matricer(1, 0,
                   0, -1);
    S=new Matricer(sx,  0,
                   0,   sy);
    T=F.mul(S);
    O=new Vektor(Ox,Oy);
  }
 public CoordinateSystem(double sx, double sy, double Ox, double Oy) {
    F=new Matricer(1, 0,
                   0, -1);
    S=new Matricer(sx,  0,
                   0,   sy);
    T=F.mul(S);
    O=new Vektor(Ox,Oy);
  }
  
 public Vektor transform(Vektor p){
    return T.mul(p).add(O);
  }
  
 public void drawPoint(Graphics g, Vektor p){
    Vektor px=transform(p);
    g.drawOval((int)px.x, (int)px.y, 1, 1);
    }

 public void drawPoint(Graphics g, Vektor p, int h){
    Vektor px=transform(p);
    g.drawOval((int)px.x, (int)px.y, h, h);
    }
 
 public void drawPoint(Graphics g, Vektor p, int h, Color c){
    Vektor px=transform(p);
    Color old = g.getColor();
    g.setColor(c);
    g.drawOval((int)px.x, (int)px.y, h, h);
    g.setColor(old);
  }
 public void drawTaxi(Graphics g, int X, int Y)
 {
    int h = 5;
	Vektor position = new Vektor(X, Y);
	Vektor px = transform(position);
	g.drawOval((int)px.x, (int)px.y, h, h);
 }
public void drawLine(Graphics g, Vektor p1, Vektor p2){
    Vektor px1=transform(p1);
    Vektor px2=transform(p2);  
    g.drawLine((int)px1.x, (int)px1.y, (int)px2.x, (int)px2.y);
  
  }


void drawString(Graphics g, String str, Vektor position, int offSetX,
		int offSetY) {
	Vektor px = transform(position);
	g.drawString(str, (int) px.x + offSetX, (int) px.y + offSetY);
}

void drawString(Graphics g, String str, int X, int Y) {
	Vektor position = new Vektor(X, Y);
	Vektor px = transform(position);
	g.drawString(str, (int) px.x, (int) px.y);
}

void drawString(Graphics g, String str, Vektor position) {
	Vektor px = transform(position);
	g.drawString(str, (int) px.x, (int) px.y);
}

void drawStation(Graphics g, int X, int Y ) {
	Vektor p = new Vektor (X,Y);
	Vektor px = transform(p);
	g.setColor(Color.BLACK);
	g.drawOval((int) px.x, (int) px.y, 3, 3);
	//g.setColor(Color.GRAY);
	//drawString(g, (printName ? name : ""), (int) p.x, (int) p.y);
}

//void drawStation(Graphics g,  Vektor p) {
//	drawStation(g, p, true);
//}

//void drawStation(Graphics g, int X, int Y) {
//	Vektor position = new Vektor(X, Y);
//	drawStation(g, position);
//}

//void drawStation(Graphics g, SerializableStation station, boolean printName) {
//	Vektor position = new Vektor(station.latitude, station.longitude);
//	drawStation(g, (printName ? station.stationName : ""), position);
//}

//void drawStation(Graphics g, SerializableStation station) {
//	drawStation(g, station, true);
//}

void drawStationLine(Graphics g, int aX, int aY, int bX, int bY, Boolean highlight) 
{
	Vektor p1 = new Vektor(aX, aY);
	Vektor p2 = new Vektor(bX, bY);
	Vektor p1x = transform(p1);
	Vektor p2x = transform(p2);

	if (!highlight)
		g.setColor(Color.LIGHT_GRAY);
	else
		g.setColor(Color.GREEN);

	g.drawLine((int) p1x.x, (int) p1x.y, (int) p2x.x, (int) p2x.y);
}

void drawRouteLine(Graphics g, int aX, int aY, int bX, int bY) 
{
	Vektor p1 = new Vektor(aX, aY);
	Vektor p2 = new Vektor(bX, bY);
	Vektor p1x = transform(p1);
	Vektor p2x = transform(p2);

	g.setColor(Color.RED);

	g.drawLine((int) p1x.x, (int) p1x.y, (int) p2x.x, (int) p2x.y);
}

//void drawStationLine(Graphics g, SerializableStation station1, SerializableStation station2) {
//	drawStationLine(g, station1, station2, false);
//}

//Tegner linier. Farve kan vælges.
 public void drawLine(Graphics g, Vektor p1, Vektor p2, Color c){
    Vektor px1=transform(p1);
    Vektor px2=transform(p2);
    Color old = g.getColor();
    g.setColor(c);
    g.drawLine((int)px1.x, (int)px1.y, (int)px2.x, (int)px2.y);
    g.setColor(old);
  }


 //Tegner x- og y-aksen
 public void drawAxis(Graphics g){
    drawLine(g, new Vektor(0,0), new Vektor(1,0));  //1 unit x-akse
    drawLine(g, new Vektor(0,0), new Vektor(0,1)); //1 unit y-akse

    drawLine(g, new Vektor(1,0), new Vektor(100,0));
    drawLine(g, new Vektor(0,1), new Vektor(0,100));
    drawLine(g, new Vektor(0,0), new Vektor(-100,0));
    drawLine(g, new Vektor(0,0), new Vektor(0,-100));

    //1 unit indikator - x-akse
    drawLine(g, new Vektor(1,0), new Vektor(1,0.25));
    drawLine(g, new Vektor(1,0), new Vektor(1,-0.25));
            //1 unit indikator - x-akse (minus akse)
            drawLine(g, new Vektor(-1,0), new Vektor(-1,0.25));
            drawLine(g, new Vektor(-1,0), new Vektor(-1,-0.25));

    //2 unit indikator - x-akse
    drawLine(g, new Vektor(2,0), new Vektor(2,0.25));
    drawLine(g, new Vektor(2,0), new Vektor(2,-0.25));
            //2 unit indikator - x-akse (minus akse
            drawLine(g, new Vektor(-2,0), new Vektor(-2,0.25));
            drawLine(g, new Vektor(-2,0), new Vektor(-2,-0.25));

    //3 unit indikator - x-akse
    drawLine(g, new Vektor(3,0), new Vektor(3,0.25));
    drawLine(g, new Vektor(3,0), new Vektor(3,-0.25));
            //3 unit indikator - x-akse (minus akse)
            drawLine(g, new Vektor(-3,0), new Vektor(-3,0.25));
            drawLine(g, new Vektor(-3,0), new Vektor(-3,-0.25));

    //4 unit indikator - x-akse
    drawLine(g, new Vektor(4,0), new Vektor(4,0.25));
    drawLine(g, new Vektor(4,0), new Vektor(4,-0.25));
            //4 unit indikator - x-akse (minus akse)
            drawLine(g, new Vektor(-4,0), new Vektor(-4,0.25));
            drawLine(g, new Vektor(-4,0), new Vektor(-4,-0.25));

    //1 unit indikator - y-akse
    drawLine(g, new Vektor(0,1), new Vektor(0.25, 1));
    drawLine(g, new Vektor(0,1), new Vektor(-0.25,1));
            //1 unit indikator - y-akse (minus akse)
            drawLine(g, new Vektor(0,-1), new Vektor(0.25, -1));
            drawLine(g, new Vektor(0,-1), new Vektor(-0.25, -1));

    //2 unit indikator - y-akse
    drawLine(g, new Vektor(0,2*1), new Vektor(0.25, 2*1));
    drawLine(g, new Vektor(0,2*1), new Vektor(-0.25, 2*1));
            //2 unit indikator - y-akse (minus akse)
            drawLine(g, new Vektor(0,-2*1), new Vektor(0.25, -2*1));
            drawLine(g, new Vektor(0,-2*1), new Vektor(-0.25, -2*1));
   
    //3 unit indikator - y-akse
    drawLine(g, new Vektor(0,3*1), new Vektor(0.25, 3*1));
    drawLine(g, new Vektor(0,3*1), new Vektor(-0.25, 3*1));
            //3 unit indikator - y-akse (minus akse)
            drawLine(g, new Vektor(0,-3*1), new Vektor(0.25, -3*1));
            drawLine(g, new Vektor(0,-3*1), new Vektor(-0.25, -3*1));

    //4 unit indikator - y-akse
    drawLine(g, new Vektor(0,4*1), new Vektor(0.25, 4*1));
    drawLine(g, new Vektor(0,4*1), new Vektor(-0.25, 4*1));
            //4 unit indikator - y-akse (minus akse)
            drawLine(g, new Vektor(0,-4*1), new Vektor(0.25, -4*1));
            drawLine(g, new Vektor(0,-4*1), new Vektor(-0.25, -4*1));

  }
}
