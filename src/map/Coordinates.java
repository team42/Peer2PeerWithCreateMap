package map;

import java.util.ArrayList;

public class Coordinates 
{

	public int ID, ownX, ownY, parentID, NON, N1, N2, N3, N4, N5;
	public double F, G, H, TempG;
	public boolean taxi;
	public ArrayList<Integer> taxiIDs;
	
	Coordinates(int id, int x, int y, int NON, int N1, int N2, int N3, int N4, int N5)
	{
		this.ID = id;
		this.ownX = x;
		this.ownY = y;
		this.parentID = 9999;
		this.F = 9999;
		this.G = 0;
		this.H = 0;
		this.TempG = 0;
		this.NON = NON;
		this.N1 = N1;
		this.N2 = N2;
		this.N3 = N3;
		this.N4 = N4;
		this.N5 = N5;
		this.taxi = false;
		this.taxiIDs = new ArrayList<Integer>();
	}
	
	public boolean getTaxi()
    {
        return taxi;
    }//end method getTaxi

    public void setTaxi (boolean TAXI)
    {
        taxi = TAXI;
    }//end method setTaxi
    
    public ArrayList<Integer> getTaxiIDs()
    {
        return taxiIDs;
    }//end method getTaxiIDs

    public void AddTaxiIDs(int TAXIID)
    {
        taxiIDs.add(TAXIID);
    }//end method setTaxiIDs
    
	public int getParentID()
    {
        return parentID;
    }//end method getParentID

    public void setParentID (int parID)
    {
        parentID = parID;
    }//end method setParentID
    
    public double getF()
    {
        return F;
    }//end method getF

    public void setF (double f)
    {
        F = f;
    }//end method setF
    
    public double getG()
    {
        return G;
    }//end method getG

    public void setG (double g)
    {
        G = g;
    }//end method setG
    
    public double getH()
    {
        return H;
    }//end method getH

    public void setH (double h)
    {
        H = h;
    }//end method setH
    
    public double getTempG()
    {
        return TempG;
    }//end method getTempG

    public void setTempG (double g)
    {
        TempG = g;
    }//end method setTempG
    
    public int getNON()
    {
        return NON;
    }//end method getNON

    public void setNON (int n)
    {
        NON = n;
    }//end method setNON
    
    public int getN1()
    {
        return N1;
    }//end method getN1

    public void setN1 (int n)
    {
        N1 = n;
    }//end method setN1
    
    public int getN2()
    {
        return N2;
    }//end method getN2

    public void setN2 (int n)
    {
        N2 = n;
    }//end method setN2
    
    public int getN3()
    {
        return N3;
    }//end method getN3

    public void setN3 (int n)
    {
        N3 = n;
    }//end method setN3
    
    public int getN4()
    {
        return N4;
    }//end method getN4

    public void setN4 (int n)
    {
        N4 = n;
    }//end method setN4
    
    public int getN5()
    {
        return N5;
    }//end method getN5

    public void setN5 (int n)
    {
        N5 = n;
    }//end method setN5

    public int getNn(int n)
    {
        switch(n){
        case 1:
        	return getN1();
        case 2:
        	return getN2();
        case 3:
        	return getN3();
        case 4:
        	return getN4();
        case 5:
        	return getN5();
        default:
        	return 9999;
        }
    }//end method getNn
}