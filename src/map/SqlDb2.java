package map;

import java.sql.*;
import java.util.ArrayList;

import javax.swing.JFrame;


public class SqlDb2 {
		
	 Connection conn = null;  
     ResultSet res = null;  
     Statement statement = null;  
     DatabaseMetaData meta = null;
     Algorithm algo = new Algorithm();
     TestAlgo algoTest = new TestAlgo();
     
     TaxiMap frame;
 
     	Coordinates c ;
		ArrayList<Coordinates> a = new ArrayList<Coordinates>();
		int id, x, y, NON, N1, N2, N3, N4, N5;
		

	public ArrayList<Coordinates> write()  //double latitude, double longitude, int ID ------- //ArrayList<SerializableStation> stationArray, ArrayList<SerializableStation[]> connectionArray, int[] connsWanted
	{		
		try 
	     {  
	         Class.forName("org.postgresql.Driver");  
	         
	     }
		catch (ClassNotFoundException e)
		{
			System.out.println("Couldn't found the PostgreSQL driver!");
			System.out.println("Error: ");
			e.printStackTrace();
		}
		try
		{
	     conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/taxiPath", "postgres", "crosser");
		}
		catch (SQLException e) 
		{
				System.out.println("Connection Failed!");
				System.out.println("Error: ");
				e.printStackTrace();
		}

		try{
		Statement stat = conn.createStatement();
	    
        meta = conn.getMetaData();
        res = meta.getTables(null, null, "coordinates", null);
        
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	         try {							 
					 Statement stat = conn.createStatement();
					 
				      ResultSet rs = stat.executeQuery("SELECT * FROM \"coordinates\" ORDER BY \"id\" ASC");         
				  
				      while(rs.next())
				      {
				    	 id = rs.getInt(1);
				         x = rs.getInt(2);
				         y = rs.getInt(3);
				         NON = rs.getInt(4);
				         N1 = rs.getInt(5);
				         N2 = rs.getInt(6);
				        if (rs.wasNull()){N2 = 9999;}
				         N3 = rs.getInt(7);
				        if (rs.wasNull()){N3 = 9999;}
				         N4 = rs.getInt(8);
				        if (rs.wasNull()){N4 = 9999;}				        
				         N5 = rs.getInt(9);
				        if (rs.wasNull()){N5 = 9999;}
				   
				        a.add(new Coordinates(id, x, y, NON, N1, N2, N3, N4, N5));
				      }
				      stat.close();
				      conn.close();
//				  	System.out.println("ID: " + a.get(0).ID + " X: " + a.get(0).ownX + " Y: " + a.get(0).ownY + " NON: " + a.get(0).getNON() + " N1: " + a.get(0).getN1() + " N2: " + a.get(0).getN2() + " N3: " + a.get(0).getN3() + " N4: " + a.get(0).getN4() + " N5: " + a.get(0).getN5());				 
//				  	System.out.println("ID: " + a.get(1).ID + " X: " + a.get(1).ownX + " Y: " + a.get(1).ownY + " NON: " + a.get(1).getNON() + " N1: " + a.get(1).getN1() + " N2: " + a.get(1).getN2() + " N3: " + a.get(1).getN3() + " N4: " + a.get(1).getN4() + " N5: " + a.get(1).getN5());				 
//				  	System.out.println("ID: " + a.get(2).ID + " X: " + a.get(2).ownX + " Y: " + a.get(2).ownY + " NON: " + a.get(2).getNON() + " N1: " + a.get(2).getN1() + " N2: " + a.get(2).getN2() + " N3: " + a.get(2).getN3() + " N4: " + a.get(2).getN4() + " N5: " + a.get(2).getN5());				 
//				  	System.out.println("ID: " + a.get(3).ID + " X: " + a.get(3).ownX + " Y: " + a.get(3).ownY + " NON: " + a.get(3).getNON() + " N1: " + a.get(3).getN1() + " N2: " + a.get(3).getN2() + " N3: " + a.get(3).getN3() + " N4: " + a.get(3).getN4() + " N5: " + a.get(3).getN5());				 
//				  	System.out.println("ID: " + a.get(4).ID + " X: " + a.get(4).ownX + " Y: " + a.get(4).ownY + " NON: " + a.get(4).getNON() + " N1: " + a.get(4).getN1() + " N2: " + a.get(4).getN2() + " N3: " + a.get(4).getN3() + " N4: " + a.get(4).getN4() + " N5: " + a.get(4).getN5());				 

				 }
	         catch (SQLException e) {
	        	System.out.println("SQL Error!");
				System.out.println("Error: ");
				e.printStackTrace();
			} 

	         catch (Exception g)
			 {
				 System.out.println("Something went wrong.");
				 System.out.println("Error: ");
				 g.printStackTrace();
			 } 
	        
	         return a;
	         
	        }
	
}
