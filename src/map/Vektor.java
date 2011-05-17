package map;

public class Vektor {
  public double x,y;
 public Vektor(double x, double y){
    this.x=x;
    this.y=y;
  }
  
  public Vektor add(Vektor v){
    return new Vektor(x+v.x, y+v.y);
  }

    public Vektor add(double s){
    return new Vektor(x+s, y+s);
  }
    
  public Vektor sub(Vektor v){
    return new Vektor(x-v.x, y-v.y);
  }

  //Vektor på vektor
 public Vektor mul(Vektor v){
    return new Vektor(x*v.x, y*v.y);
  }

   //Scalar på vektor
 public Vektor mul(double s){
    return new Vektor(x*s, y*s);
  }

   //Beregner modulus (længden) af en vektor
 public double modulus(Vektor v){
    return (Math.sqrt(Math.pow(v.x, 2)+Math.pow(v.y, 2)));
  }

 //Beregner dens enhedsvektor
 public Vektor enhed(Vektor v){
    return new Vektor (v.x/v.modulus(v), v.y/v.modulus(v));
  }

 //Beregner dens enhedsvektor
 public Vektor enhed(){
    return new Vektor (x/Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)), y/Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)));
  }

  public Vektor div(double k){
    return new Vektor (x/k, y/k);
  }
}
