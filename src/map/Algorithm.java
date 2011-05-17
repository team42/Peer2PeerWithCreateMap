package map;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Algorithm 
{

   Connection conn = null;  
    ResultSet res = null;  
    Statement statement = null;  
    DatabaseMetaData meta = null;
    
   //sqlDb s = new sqlDb();
   ArrayList<Coordinates> stations = new ArrayList<Coordinates>(); //HENT ARRAYLIST!;
   ArrayList<Integer> openList = new ArrayList<Integer>();
   ArrayList<Integer> closedList = new ArrayList<Integer>();
   ArrayList<Integer> taxiList = new ArrayList<Integer>();
   int id, x, y, NON, N1, N2, N3, N4, N5;
   
   public ArrayList<Coordinates> Algorithm()
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
         
            
            
            // System.out.println("Det var da godt!");
             
             Statement stat = conn.createStatement();
             
               ResultSet rs = stat.executeQuery("SELECT * FROM \"coordinates\" ORDER BY \"id\" ASC");         
               //int i = 1;
               //System.out.println("Hej igen");
               //System.out.println("Got results:");
               while(rs.next()) // process results one row at a time
               {
                // System.out.println("Halløj!");
                  id = rs.getInt(1);
                  x = rs.getInt(2);
                  y = rs.getInt(3);
                  NON = rs.getInt(4);
                  N1 = rs.getInt(5);
                 //if (rs.wasNull()){N1 = 9999;}
                  N2 = rs.getInt(6);
                 if (rs.wasNull()){N2 = 9999;}
                  N3 = rs.getInt(7);
                 if (rs.wasNull()){N3 = 9999;}
                  N4 = rs.getInt(8);
                 if (rs.wasNull()){N4 = 9999;}                   
                  N5 = rs.getInt(9);
                 if (rs.wasNull()){N5 = 9999;}
                 
               // System.out.println("N1: " + N1);
//              if (N2 == 0){N2 = 9999;}
//              if (N3 == 0){N3 = 9999;}
//              if (N4 == 0){N4 = 9999;}
//              if (N5 == 0){N5 = 9999;}
//        
                
                 
                 //coordinates = new coordinates(id, lat, longi, neighNumb, neighId1, neighId2, neighId3, neighId4, neighId5);
                 stations.add(new Coordinates(id, x, y, NON, N1, N2, N3, N4, N5));
               }
               stat.close();
               conn.close();
            System.out.println("ID: " + stations.get(0).ID + " X: " + stations.get(0).ownX + " Y: " + stations.get(0).ownY + " NON: " + stations.get(0).getNON() + " N1: " + stations.get(0).getN1() + " N2: " + stations.get(0).getN2() + " N3: " + stations.get(0).getN3() + " N4: " + stations.get(0).getN4() + " N5: " + stations.get(0).getN5());             
            System.out.println("ID: " + stations.get(1).ID + " X: " + stations.get(1).ownX + " Y: " + stations.get(1).ownY + " NON: " + stations.get(1).getNON() + " N1: " + stations.get(1).getN1() + " N2: " + stations.get(1).getN2() + " N3: " + stations.get(1).getN3() + " N4: " + stations.get(1).getN4() + " N5: " + stations.get(1).getN5());             
//          System.out.println("ID: " + a.get(2).ID + " X: " + a.get(2).ownX + " Y: " + a.get(2).ownY + " NON: " + a.get(2).getNON() + " N1: " + a.get(2).getN1() + " N2: " + stations.get(2).getN2() + " N3: " + a.get(2).getN3() + " N4: " + a.get(2).getN4() + " N5: " + a.get(2).getN5());            
//          System.out.println("ID: " + a.get(3).ID + " X: " + a.get(3).ownX + " Y: " + a.get(3).ownY + " NON: " + a.get(3).getNON() + " N1: " + a.get(3).getN1() + " N2: " + stations.get(3).getN2() + " N3: " + a.get(3).getN3() + " N4: " + a.get(3).getN4() + " N5: " + a.get(3).getN5());            
//          System.out.println("ID: " + a.get(4).ID + " X: " + a.get(4).ownX + " Y: " + a.get(4).ownY + " NON: " + a.get(4).getNON() + " N1: " + a.get(4).getN1() + " N2: " + stations.get(4).getN2() + " N3: " + a.get(4).getN3() + " N4: " + a.get(4).getN4() + " N5: " + a.get(4).getN5());            
//             
//             for (int u = 0; u < a.size(); u++)
//             {
//               System.out.println("Plads" + u + " ID = " + a.get(u).ID + " " + a.get(u).N5);
//             }
             //System.out.println("List: " + a.get(5).);
                  
            //algoTest.TestAlgo();
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
	return stations; 
   }
   
   public void AddToOpenList(int c)
   {
      openList.add(c);
   }
   
   public void RemoveFromOpenlist(int c)
   {
      openList.remove(c);
   }
   
   public void AddToClosedList(int c)

   {
      closedList.add(c);
   }

   public void AddToTaxiList(int c)
	{
		taxiList.add(c);
	}

	public double CalcDist(int a, int b)
	{
		int latA = stations.get(a).ownX;
		int lonA = stations.get(a).ownY;

		int latB = stations.get(b).ownX;
		int lonB = stations.get(b).ownY;

		return Math.sqrt(((latA-latB)*(latA-latB))+((lonA-lonB)*(lonA-lonB)));
	}

	public double CalcG(int a)
	{
		int parentCell = stations.get(a).parentID;
		double g = stations.get(parentCell).G;

		g = g + CalcDist(a,parentCell);

		return g;
	}

	public double CalcH(int a, int goal)
	{
		double h = CalcDist(a,goal);

		return h;
	}

	public double CalcF(int a)
	{
		double g = stations.get(a).G;
		double h = stations.get(a).H;

		double f = g + h;

		return f;
	}

	public double CalcTempG(int a)
	{
		int parentCell = stations.get(a).parentID;
		double g = stations.get(parentCell).getG();

		g = g + CalcDist(a,parentCell);

		return g;
	}

	public void Route(int ST, int END)
	{

		// Switch Start and goal to backtrack route
		int start = END;
		int goal = ST;
		ArrayList<Integer> routeList = new ArrayList<Integer>();

		AddToOpenList(start);                     		// add start point to open list
		stations.get(start).setG(0);              		// save G
		stations.get(start).setH(CalcH(start,goal));    // calculate and save H
		stations.get(start).setF(CalcF(start));     	// calculate and save F
		int currentID = start;
		while(!closedList.contains(goal) && !openList.isEmpty()) // (Goal is not on closed list and open list is not empty)
		{
			System.out.println("OPENLIST  : " + openList);
			System.out.println("CLOSEDLIST: " + closedList);

			// select from open list where f is lowest;
			int currentLowestF = 2147483647;
			int currentLowestFID = 9999;

			for(int i=0; i < stations.size(); i++)
			{
				if(stations.get(i).getF() < currentLowestF && openList.contains(stations.get(i).ID))
				{
					currentLowestF = (int) stations.get(i).getF();
					currentLowestFID = (int) stations.get(i).ID;
				}

			}        

			currentID = currentLowestFID;
			int i = stations.get(currentID).NON; // Number of Neighbors
			int n = 1; // Neighbor number

			while(i > 0)
			{
				int currentNeighbor = stations.get(currentID).getNn(n);

				if(closedList.contains(currentNeighbor)) // (neighbor is on closed list)
				{
					// Look at next neighbor
					i--;
					n++;
				}
				else if(!openList.contains(currentNeighbor)) // (neighbor is not on open list)
				{
					AddToOpenList(currentNeighbor); // add neighbor to open list
					stations.get(currentNeighbor).setParentID(currentID);             // set parentID
					stations.get(currentNeighbor).setG(CalcG(currentNeighbor));       // calculate and save G
					stations.get(currentNeighbor).setH(CalcH(currentNeighbor,goal));  // calculate and save H
					stations.get(currentNeighbor).setF(CalcF(currentNeighbor));       // calculate and save F
					i--;
					n++;
				}
				else
				{
					// Set tempG
					stations.get(currentNeighbor).setTempG(CalcTempG(currentNeighbor));
					double tempG = stations.get(currentNeighbor).getTempG();
					
					if(tempG < stations.get(currentNeighbor).getG()) // (g is lower than before)
					{
						stations.get(currentNeighbor).setParentID(currentID);          // change parent
						stations.get(currentNeighbor).setG(CalcG(currentNeighbor));    // calculate and save G
						stations.get(currentNeighbor).setF(CalcF(currentNeighbor));    // calculate and save F
						i--;
						n++;
					}
					else // (g is NOT lower than before)
					{
						// look at next neighbor
						i--;
						n++;
					}
				}
			} // end while(i > 0) loop

			AddToClosedList(currentID);
			RemoveFromOpenlist(openList.indexOf(currentID));
			
		} // end while (!closedList.contains(goal) && !openList.isEmpty()) loop

		if(closedList.contains(goal))
		{
			// BackTrack and show route
			int from = goal;
			int to = stations.get(goal).getParentID();
			routeList.add(goal);
			while(to != start)
			{
				from = stations.get(from).getParentID();
				to = stations.get(from).getParentID();
				routeList.add(from);
			}

			routeList.add(start);
		}
		else
		{
			System.out.println("No route was found!");
		}
	} // End method Route

	public double RouteLength(int X1, int Y1, int X2, int Y2)
	{
		// Switch Start and goal to backtrack route
		int start = findClosestPoint(X2,Y2,5);
		int goal = findClosestPoint(X1,Y1,2);

		AddToOpenList(start);                     // add start point to open list
		stations.get(start).setG(0);              // save G
		stations.get(start).setH(CalcH(start,goal));    // calculate and save H
		stations.get(start).setF(CalcF(start));      // calculate and save F
		int currentID = start;
		while(!closedList.contains(goal) && !openList.isEmpty()) // (Goal is not on closed list and open list is not empty)
		{
			// select from open list where f is lowest;
			int currentLowestF = 2147483647;
			int currentLowestFID = 9999;

			for(int i=0; i < stations.size(); i++)
			{
				if(stations.get(i).getF() < currentLowestF && openList.contains(stations.get(i).ID))
				{
					currentLowestF = (int) stations.get(i).getF();
					currentLowestFID = (int) stations.get(i).ID;
				}
			}        

			currentID = currentLowestFID;
			int i = stations.get(currentID).NON; // Number of Neighbors
			int n = 1; // Neighbor number

			while(i > 0)
			{
				int currentNeighbor = stations.get(currentID).getNn(n);

				if(closedList.contains(currentNeighbor)) // (neighbor is on closed list)
				{
					// Look at next neighbor
					i--;
					n++;
				}
				else if(!openList.contains(currentNeighbor)) // (neighbor is not on open list)
				{
					AddToOpenList(currentNeighbor); // add neighbor to open list
					stations.get(currentNeighbor).setParentID(currentID);             // set parentID
					stations.get(currentNeighbor).setG(CalcG(currentNeighbor));       // calculate and save G
					stations.get(currentNeighbor).setH(CalcH(currentNeighbor,goal));  // calculate and save H
					stations.get(currentNeighbor).setF(CalcF(currentNeighbor));       // calculate and save F
					i--;
					n++;
				}
				else
				{
					// Set tempG
					stations.get(currentNeighbor).setTempG(CalcTempG(currentNeighbor));
					double tempG = stations.get(currentNeighbor).getTempG();
					
					if(tempG < stations.get(currentNeighbor).getG()) // (g is lower than before)
					{
						stations.get(currentNeighbor).setParentID(currentID);          // change parent
						stations.get(currentNeighbor).setG(CalcG(currentNeighbor));    // calculate and save G
						stations.get(currentNeighbor).setF(CalcF(currentNeighbor));    // calculate and save F
						i--;
						n++;
					}
					else // (g is NOT lower than before)
					{
						// look at next neighbor
						System.out.println("This Neighbor is on Open list, calculating new G value... G is not lower than before");
						i--;
						n++;
					}
				}
			} // end while(i > 0) loop

			AddToClosedList(currentID);
			RemoveFromOpenlist(openList.indexOf(currentID));
			
		} // end while (!closedList.contains(goal) && !openList.isEmpty()) loop

		if(closedList.contains(goal))
		{
			// Return length of route
			return stations.get(goal).getF();
		}
		else
		{
			System.out.println("No route was found!");
			return 0;
		}
	} // End method RouteLength

//	public void closestTaxis(int NoTaxis, int ClosestTo)
//	{ 
//		int NoT = NoTaxis;
//		int Point = ClosestTo;
//		int counter = 0;
//
//		AddToOpenList(Point); 			// add start point to open list
//		stations.get(Point).setG(0); 	// set G
//		stations.get(Point).setF(0);	// set F
//		int currentID = Point;
//
//		while(counter < NoT)
//		{
//			// select from open list where g is lowest;
//			int currentLowestF = 2147483647;
//			int currentLowestFID = 9999;
//
//			for(int i=0; i < stations.size(); i++)
//			{
//				if(stations.get(i).getF() < currentLowestF && openList.contains(stations.get(i).ID))
//				{
//					currentLowestF = (int) stations.get(i).getF();
//					currentLowestFID = (int) stations.get(i).ID;
//				}
//			}			
//
//			currentID = currentLowestFID;
//			int i = stations.get(currentID).NON; // Number of Neighbors
//			int n = 1;
//
//			while(i > 0)
//			{
//				int currentNeighbor = stations.get(currentID).getNn(n);
//				
//				if(closedList.contains(currentNeighbor)) // (neighbor is on closed list)
//				{
//					// Look at next neighbor
//					i--;
//					n++;
//				}
//				else if(!openList.contains(currentNeighbor)) // (neighbor is not on open list)
//				{
//					AddToOpenList(currentNeighbor); // add neighbor to open list
//					stations.get(currentNeighbor).setParentID(currentID); 						// set parentID
//					stations.get(currentNeighbor).setG(CalcG(currentNeighbor)); 				// calculate and save G
//					stations.get(currentNeighbor).setF(stations.get(currentNeighbor).getG());	// save F
//					i--;
//					n++;
//				}
//				else
//				{
//					// Set tempG
//					stations.get(currentNeighbor).setTempG(CalcTempG(currentNeighbor));
//					double tempG = stations.get(currentNeighbor).getTempG();
//					
//					if(tempG < stations.get(currentNeighbor).getG()) // (g is lower than before)
//					{
//						stations.get(currentNeighbor).setParentID(currentID); 						// change parent
//						stations.get(currentNeighbor).setG(CalcG(currentNeighbor)); 				// calculate and save G
//						stations.get(currentNeighbor).setF(stations.get(currentNeighbor).getG());	// calculate and save F
//						i--;
//						n++;
//					}
//					else // (g is NOT lower than before)
//					{
//						// look at next neighbor
//						i--;
//						n++;
//					}
//				}
//
//			} // end while(i > 0) loop
//
//			if(stations.get(currentID).getTaxi())
//			{
//				AddToTaxiList(currentID);
//				counter = counter + stations.get(currentID).getTaxiIDs().size();
//			} // End if
//
//			AddToClosedList(currentID);
//			RemoveFromOpenlist(openList.indexOf(currentID));
//			
//		} // End while (counter < NoT)
//
//		System.out.println("\n\nThe closest " + NoT + " taxis to node " + Point + " is:");
//		for(int k = 0; k < taxiList.size(); k++)
//		{
//			int tempNodeID = taxiList.get(k);
//
//			for(int z = 0; z < stations.get(tempNodeID).taxiIDs.size(); z++)
//			{
//				System.out.printf("\nTaxi with ID: %03d found at node " + tempNodeID, stations.get(tempNodeID).taxiIDs.get(z));
//			}
//		}
//	} // End function closestTaxis

	public int findClosestPoint(int xvalue, int yvalue, int taxiID)
	{
		int thisX = xvalue;
		int thisY = yvalue;
		int nodeID = 9999;
		double close = 9999.9;
		double tempValue;

		for(int i=0; i < stations.size(); i++)
		{
			tempValue = Math.sqrt(((thisX-stations.get(i).ownX)*(thisX-stations.get(i).ownX))+((thisY-stations.get(i).ownY)*(thisY-stations.get(i).ownY)));

			if(tempValue < close)
			{
				close = tempValue;
				nodeID = stations.get(i).ID;
			} // End if

		} // End for loop

		stations.get(nodeID).setTaxi(true);
		stations.get(nodeID).AddTaxiIDs(taxiID);
		return nodeID;

	} // End function findClosestPoint

}