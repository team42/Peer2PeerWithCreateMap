package map;

import java.sql.*;
import java.util.ArrayList;

import javax.swing.JFrame;

import common.SerializableStation;
import database.PostgresqlConnectionFactory;

public class Sqldb {
		
	 Connection conn = null;  
     ResultSet res = null;  
     Statement statement = null;  
     DatabaseMetaData meta = null;
     Algorithm algo = new Algorithm();
     TestAlgo algoTest = new TestAlgo();
     
     Map frame;
 
     	Coordinates c ;
		ArrayList<Coordinates> a = new ArrayList<Coordinates>();
		int id, x, y, NON, N1, N2, N3, N4, N5;
		

	public ArrayList<Coordinates> write(ArrayList<SerializableStation> stationArray, ArrayList<SerializableStation[]> connectionArray, int[] connsWanted)  //double latitude, double longitude, int ID ------- //ArrayList<SerializableStation> stationArray, ArrayList<SerializableStation[]> connectionArray, int[] connsWanted
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
		 conn = PostgresqlConnectionFactory.createConnection();
	     //conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/taxiPath", "postgres", "crosser");
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
					if (res.next())
				 {
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
				  	System.out.println("ID: " + a.get(0).ID + " X: " + a.get(0).ownX + " Y: " + a.get(0).ownY + " NON: " + a.get(0).getNON() + " N1: " + a.get(0).getN1() + " N2: " + a.get(0).getN2() + " N3: " + a.get(0).getN3() + " N4: " + a.get(0).getN4() + " N5: " + a.get(0).getN5());				 
				  	System.out.println("ID: " + a.get(1).ID + " X: " + a.get(1).ownX + " Y: " + a.get(1).ownY + " NON: " + a.get(1).getNON() + " N1: " + a.get(1).getN1() + " N2: " + a.get(1).getN2() + " N3: " + a.get(1).getN3() + " N4: " + a.get(1).getN4() + " N5: " + a.get(1).getN5());				 
				  	System.out.println("ID: " + a.get(2).ID + " X: " + a.get(2).ownX + " Y: " + a.get(2).ownY + " NON: " + a.get(2).getNON() + " N1: " + a.get(2).getN1() + " N2: " + a.get(2).getN2() + " N3: " + a.get(2).getN3() + " N4: " + a.get(2).getN4() + " N5: " + a.get(2).getN5());				 
				  	System.out.println("ID: " + a.get(3).ID + " X: " + a.get(3).ownX + " Y: " + a.get(3).ownY + " NON: " + a.get(3).getNON() + " N1: " + a.get(3).getN1() + " N2: " + a.get(3).getN2() + " N3: " + a.get(3).getN3() + " N4: " + a.get(3).getN4() + " N5: " + a.get(3).getN5());				 
				  	System.out.println("ID: " + a.get(4).ID + " X: " + a.get(4).ownX + " Y: " + a.get(4).ownY + " NON: " + a.get(4).getNON() + " N1: " + a.get(4).getN1() + " N2: " + a.get(4).getN2() + " N3: " + a.get(4).getN3() + " N4: " + a.get(4).getN4() + " N5: " + a.get(4).getN5());				 

//				      for (int u = 0; u < a.size(); u++)
//				      {
//				    	  System.out.println("Plads" + u + " ID = " + a.get(u).ID + " " + a.get(u).N5);
//				      }
					 //System.out.println("List: " + a.get(5).);
					 		
				  	//algoTest.TestAlgo();
				 }

					 else
					 {
						
							Statement stat = conn.createStatement();
							stat.executeUpdate("CREATE TABLE \"coordinates\" (id INT4, latitude INT4, longitude INT4,  " +
											   "neighborNumber INT4, neighborId1 INT4, neighborId2 INT4, neighborId3 INT4, neighborId4 INT4, neighborId5 INT4);"); //DECIMAL
						 
						 
						 	PreparedStatement prep = conn.prepareStatement("INSERT INTO \"coordinates\" values (?, ?, ?);");
							for (int i = 0; i < stationArray.size(); i++)
							{
								prep.setInt(1, stationArray.get(i).stationid);
								prep.setInt(2, (int) stationArray.get(i).latitude);
								prep.setInt(3, (int) stationArray.get(i).longitude);
							
	//
								prep.addBatch();					        
							}
							
							conn.setAutoCommit(false);
							prep.executeBatch();
					        conn.setAutoCommit(true);
					        prep.close();
					        
					        int neighborID = 1;
					        int id;
					       
					        if(connsWanted.equals(null))
				        	{
				        		System.out.println("Fatal error!");
				        	
				        	}
					 
					        for (int i = 0; i < connectionArray.size(); i++)
							{
					        				        	
					        	id = connectionArray.get(i)[0].stationid; //fra
					        	neighborID = connectionArray.get(i)[1].stationid; //til
					        					        	
					        	for (int neighbor = 1; neighbor <= 5 ;neighbor++)
					        	{
					        		ResultSet rs = stat.executeQuery("SELECT neighborid" + neighbor + " FROM \"coordinates\" WHERE id = '" + id + "'");
					        												 
					        		rs.next();
					        		int value = rs.getInt(1);
					        		if (rs.wasNull()) 
					        		{
					        			prep = conn.prepareStatement("UPDATE \"coordinates\" SET neighborid" + neighbor + " = '" + neighborID +"' WHERE id = '" + id + "'");				        			
					        			prep.addBatch();
					        			conn.setAutoCommit(false);
					        			prep.executeBatch();
					        			conn.setAutoCommit(true);
					        			break;
					        		}
					        		
					        	}					        
							}	
					       prep.close(); 
					       	  int y = 0;
						      ResultSet rs = stat.executeQuery("SELECT * FROM \"coordinates\" ORDER BY \"id\" ASC"); 
						      while(rs.next())
						      {
						    	  int idDb = rs.getInt(1);
						    	  int latDb = rs.getInt(2);
						    	  int longDb = rs.getInt(3);
	  					    	  		    	  				   
						    	  System.out.println("y = " + y);
						    	  for(int counter = 5; counter <=9; counter++)
						    	  {
						        		int value = rs.getInt(counter);
						        		if (rs.wasNull()) 
						        		{
						        			int g = counter - 5;
						        			prep = conn.prepareStatement("UPDATE \"coordinates\" SET neighborNumber = '" + g +"' WHERE id = '" + idDb + "'"); 
						        			prep.addBatch();
						        			conn.setAutoCommit(false);
						        			prep.executeBatch();
						        			conn.setAutoCommit(true);
						        			break;
						        		}
						        		System.out.println("counter = " + counter);
						    	  }
	//
					        		int value = rs.getInt(9);
					        		if (!rs.wasNull()) 
					        		{
					        			System.out.println("5 er her!!!");
					        			int g = 5;
					        			prep = conn.prepareStatement("UPDATE \"coordinates\" SET neighborNumber = '" + g +"' WHERE id = '" + idDb + "'"); //WHERE id = '" + id + "'"
					        			prep.addBatch();
					        			conn.setAutoCommit(false);
					        			prep.executeBatch();
					        			conn.setAutoCommit(true);
					        		
					        		}
						    	  
						    	  y++;
						    	  
						      }
								 
						      ResultSet rs1 = stat.executeQuery("SELECT * FROM \"coordinates\" ORDER BY \"id\" ASC");         
						      while(rs1.next())
						      {
						    	 id = rs1.getInt(1);
						         x = rs1.getInt(2);
						         y = rs1.getInt(3);
						         NON = rs1.getInt(4);
						         N1 = rs1.getInt(5);
						         N2 = rs1.getInt(6);
						         if (rs1.wasNull()){N2 = 9999;}
						         N3 = rs1.getInt(7);
						         if (rs1.wasNull()){N3 = 9999;}
						         N4 = rs1.getInt(8);
						         if (rs1.wasNull()){N4 = 9999;}				        
						         N5 = rs1.getInt(9);
						         if (rs1.wasNull()){N5 = 9999;}
					
						       if (N2 == 0){N2 = 9999;}
						       if (N3 == 0){N3 = 9999;}
						       if (N4 == 0){N4 = 9999;}
						       if (N5 == 0){N5 = 9999;}
	//
						        a.add(new Coordinates(id, x, y, NON, N1, N2, N3, N4, N5));
						      }
						      stat.close();
						      conn.close();
	//
							 	System.out.println("ID: " + a.get(5).ID + " X: " + a.get(5).ownX + " NON: " + a.get(5).getNON());				 
							 	System.out.println("ID: " + a.get(405).ID + " X: " + a.get(405).ownX + " NON: " + a.get(405).getNON());				 
							 	System.out.println("ID: " + a.get(599).ID + " X: " + a.get(599).ownX + " NON: " + a.get(599).getNON());				 
						 }
				 
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
