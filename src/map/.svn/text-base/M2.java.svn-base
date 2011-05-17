package map;

public class M2 {
  double a,b;
  double c,d;
  
  M2(double a, double b, double c, double d){
    this.a=a;
    this.b=b;
    this.c=c;
    this.d=d;
  }
  // multiplicere en matrix med en vector. returnerer en vector.
  V2 mul(V2 v){
    V2 res=new V2(a*v.x+b*v.y, c*v.x+d*v.y);
    return res;
  }
  
  M2 mul(M2 m){
    M2 res=new M2(a*m.a+b*m.c, a*m.b+b*m.d,
                  c*m.a+d*m.c, c*m.b+d*m.d);
    return res;
  }
    // addere 2 matriser
    M2 add(M2 m){
    M2 res=new M2(a+m.a, b+m.b,
                  c+m.c, d+m.d);
    return res;
  }
    // subtrahere 2 matriser
    M2 sub(M2 m){
    M2 res=new M2(a-m.a, b-m.b,
                  c-m.c, d-m.d);
    return res;
  }
    // multiplicere med en skalar
    M2 skalar(M2 m, double k){
    M2 res=new M2(k*m.a, k*m.b,
                  k*m.c, k*m.d);
    return res;
  }
    // Transponere en matrise; række = søjle.
    M2 transpose(M2 m){
    M2 res=new M2(a=m.a, b=m.c,
                  c=m.b, d=m.d);
    return res;
  }
    // invertere en matrise
    M2 inverse(M2 m){
    M2 matrix=new M2(m.a, m.b,
                  m.a, m.d);
    double temp = ((m.a*m.d)-(m.b*m.c));
    if (temp == 0)
        System.out.println("ERROR: (a*d)-(b*c) must differ from 0");
    else
    temp = 1/temp;
    M2 res =m.skalar(matrix, 1/temp);
    return res;
  }



}
