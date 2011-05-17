package map;
import common.*;

public class V2 {

    double x, y, x1, y1;
    double r;
    // constructor

    V2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    V2(V2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    // constructor
    V2(double x, double y, double x1, double y1) {
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
    }

    // constructor
    V2(double x) {
        this.x = x;
    }

        // Addition med vectorer
    double dot(V2 v) {
        double res = (this.x * v.x)+(this.y * v.y);
        return res;
    }


    // Addition med vectorer
    V2 add(V2 v) {
        V2 res = new V2(x + v.x, y + v.y);
        return res;
    }

    // Subtraction med vectorer
    V2 sub(V2 v) {
        V2 res = new V2(x - v.x, y - v.y);
        return res;
    }

    // multiplicere en vector med en konstant k
    V2 mul(double k) {
        V2 res = new V2(k * (this.x), k * (this.y));
        return res;
    }

    // multiplicere en vector med en konstant k
    V2 mul(double k, V2 v) {
        V2 res = new V2(k * (v.x), k * (v.y));
        return res;
    }

    // Lenght of vector
    double length(V2 v) {
        double res = Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
        return res;
    }

    // argumentet til en vector = vinklen fra x til vectoren.
    double arg(V2 v) {
        double res = (Math.asin((v.y) / (Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2)))));
        return res;
    }

    // enhedsvectoren til en vector
    V2 enhed(V2 v) {
        double xe = (Math.asin((v.x) / (Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2)))));
        double xy = (Math.asin((v.y) / (Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2)))));
        V2 res = new V2(xe, xy);
        return res;
    }

    V2 angle2(V2 va, V2 vb) {
        //V2 v1 = new V2(va.x, va.y);
        //V2 v2 = new V2(vb.x1, vb.y1);
        double prikprodukt = ((va.x + vb.x1) + (va.y + vb.y1));
        double produkt1 = (Math.pow(va.x, 2) + Math.pow(va.y, 2));
        double produkt2 = (Math.pow(vb.x1, 2) + Math.pow(vb.y1, 2));
        double sqrt = (Math.sqrt(produkt1) * Math.sqrt(produkt2));
        double tempRes = (prikprodukt / sqrt);
        V2 res = new V2(Math.acos(tempRes));
        return res;

    }

    // Tværvector der står vinkelret på vectoren v
    V2 transverse(V2 v) {
        V2 res = new V2(-v.y, v.x);
        return res;
    }

    // Determinanten mellem 2 vectorer
    V2 det(V2 va, V2 vb) {
        V2 res = new V2(va.x * vb.y1 - va.y * vb.x1);
        return res;
    }

    V2 projekt(V2 va, V2 vb) {
        double prikprodukt = ((va.x * vb.x1) + (va.y * vb.y1));
        double produkt1 = (Math.pow(va.x, 2) + Math.pow(va.y, 2));
        double produkt2 = (Math.pow(vb.x1, 2) + Math.pow(vb.y1, 2));
        double sqrt = (Math.sqrt(produkt1) * Math.sqrt(produkt2));
        double tempRes = (prikprodukt / sqrt);
        double angle = (Math.acos(tempRes));
        double length = (Math.sqrt(Math.pow(va.x, 2) + Math.pow(va.y, 2)));
        V2 res = new V2(Math.cos(angle) * length);
        return res;

    }

    V2 rotate(V2 C, double phi) {
        M2 rot = new M2(Math.cos(phi), -Math.sin(phi), Math.sin(phi), Math.cos(phi));
        V2 res = this;
        res = rot.mul(res.sub(C)).add(C);
        return res;
    }

    V2 spejlix() {
        V2 res = new V2(this.x, this.y * -1);
        return res;
    }

    V2 spejliy() {
        V2 res = new V2(this.x * -1, this.y);
        return res;
    }

    V2 spejlix(double Xakse) {
        V2 res = new V2(this.x, ((this.y - Xakse) * -1) + Xakse);
        return res;
    }

    V2 spejliy(double Yakse) {
        V2 res = new V2(((this.x - Yakse) * -1) + Yakse, this.y);
        return res;
    }

    V2 strech(double xstrech, double ystrech, V2 Centrum) {
        V2 res = new V2(((this.x - Centrum.x) * (xstrech)) + Centrum.x, ((this.y - Centrum.y) * (ystrech)) + Centrum.y);
        return res;
    }

    V2 skrew(double skrew) {
        double d = skrew;
        V2 res = new M2(1.0, d, 0.0, 1.0).mul(this);
        return res;
    }

    V2 spejllinje(V2 p1, V2 p2, V2 Cen) {
        double vinkel = ((Math.atan((p2.y - p1.y) / (p2.x - p1.x))));
        V2 origo = new V2(0, 0);
        V2 res = new V2(this.rotate(origo, -vinkel).spejlix().rotate(origo, vinkel));
        return res;
    }

    V2 unit() {

        V2 res = new V2(x / this.length(this), y / this.length(this));

        return res;
    }

    V2 hat() {
        V2 res = new V2(-this.y, this.x);
        return res;
    }

    @Override
    public String toString() {
        String result = "|" + this.x + "," + this.y + "|";
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof V2) {
            return this.toString().equals(obj.toString());
        } else {
            return false;
        }
    }
}
