package map;

public class Matricer {
  double a, b;
  double c, d;

 public Matricer(double a, double b,
     double c, double d) {
    this.a=a;
    this.b=b;
    this.c=c;
    this.d=d;
  }

 //Vector på matrice
 public Vektor mul(Vektor v){
    return new Vektor(a*v.x+b*v.y,
                  c*v.x+d*v.y);
  }
  
 public Matricer mul(Matricer m) {
    return new Matricer(a*m.a+b*m.c, a*m.b+b*m.d,
                  c*m.a+d*m.c, c*m.b+d*m.d);
  }

}
