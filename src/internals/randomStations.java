package internals;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import map.Map;
import map.MapOld;
import map.Sqldb;
import pathCalculation.Station;

public class randomStations {

	static int longitude = 0, latitude = 0, numStations = 4000, gridLat = 1000, gridLon = 1000, borderMargin = 0,
			minConns = 2, maxConns = 5, maxDistance = 100, stationMinDist = 5, numBorderStations = 6,
			numTotalStations = numStations + numBorderStations;

	static Station stations[] = new Station[numTotalStations];
	static int conns[] = new int[numTotalStations];
	static int connsWanted[] = new int[numTotalStations];
	static String stationNames[] = new String[numStations];
	static Station coordinates[][] = new Station[numTotalStations][numTotalStations];
	static ArrayList<Station[]> connections = new ArrayList<Station[]>();

	static Random r = new Random();
	static Sqldb sqlDb = new Sqldb();
	
	public static void main(String args[]) {

//		if (!args.equals("doRun")) {
//			System.out.println("RandomStations.java disabled!");
//			return;
//		}

		// load stationnames into array
		stationNames();

		int n = 0;

		String name;

		boolean coordinateUnavailable;

		for (int i = 0; i < numStations; i++) 
		{		
			coordinateUnavailable = true;

			n = 0;
			// is this coordinate available
			while (coordinateUnavailable) {

				// coordinate calculation
				longitude = borderMargin + r.nextInt(gridLon - borderMargin);
				latitude = borderMargin + r.nextInt(gridLat - borderMargin);

				// check for availability
				if (coordinateAvailable(latitude, longitude))
					coordinateUnavailable = false;

				n++;

			}

			// name of station
			name = stationNames[i];// (i + 1) + "";

			// generate number of connections wanted
			connsWanted[i] = r.nextInt(maxConns + 1 - minConns) + minConns;
			
//			// hub chance: 1%
//			if (r.nextInt(100) == 0)
//				connsWanted[i] += 10;

			// create station object
			stations[i] = new Station(longitude, latitude, name, i, i);

			// save reference to object on coordinate
			coordinates[latitude][longitude] = stations[i];

		}
		//sqlDb.setNeighbor(connsWanted);
		// insert borderstations
		borderStations();

		// create connections
		for (int i = 0; i < numTotalStations; i++) {
			// not enough connections created
			int retries = 0;
			int direction = 1 + r.nextInt(4);
			while (true) {
				// random direction
				findNeighbor(i, direction + retries);
				retries++;

				if (conns[i] >= connsWanted[i] || retries >= connsWanted[i] * 80)
					break;
			}
		}

		// do db magic
		//loadHer();

		MapOld frame = new MapOld(stations, connections, connsWanted);
		frame.setTitle("Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1020, 1020);
		frame.setLocationRelativeTo(null); // Center the frame
		frame.setVisible(true);

		
		
	}

	/**
	 * find neighbor in random direction
	 * 
	 * @param i
	 * @param direction
	 */
	private static void findNeighbor(int i, int direction) {
		int lat = 0, lon = 0;

		for (int ix = 1; ix < maxDistance; ix++) {
			for (int iy = -(ix - 1); iy < ix; iy++) {

				switch (direction % 4) {
				case 1:
					// east
					lat = (int) (stations[i].latitude + ix);
					lon = (int) (stations[i].longitude + iy);
					break;

				case 2:
					// south
					lat = (int) (stations[i].latitude + iy);
					lon = (int) (stations[i].longitude - ix);
					break;

				case 3:
					// west
					lat = (int) (stations[i].latitude - ix);
					lon = (int) (stations[i].longitude + iy);
					break;

				case 4:
					// north
					lat = (int) (stations[i].latitude + iy);
					lon = (int) (stations[i].longitude + ix);
					break;
				}

				if (lat >= 0 && lon >= 0 && lat <= (gridLat - 1) && lon <= (gridLon - 1)
						&& coordinates[lat][lon] != null
						&& conns[coordinates[lat][lon].dbid] < connsWanted[coordinates[lat][lon].dbid]) 
				{

					// nieghbor found

					// if connection already exists
					if (connectionExists(stations[i], coordinates[lat][lon]))
						return;

					Station[] conn = new Station[2];
					conn[0] = stations[i];
					conn[1] = coordinates[lat][lon];

					// conns[i]++;

					connections.add(conn);
					conns[i] = getConnections(stations[i]);
					conns[coordinates[lat][lon].dbid] = getConnections(stations[coordinates[lat][lon].dbid]);

					return;
				}
			}
		}

	}

	/**
	 * check for availability
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private static boolean coordinateAvailable(int latitude, int longitude) {
		int lat = 0, lon = 0;
		for (int ix = -stationMinDist; ix < stationMinDist; ix++) {
			for (int iy = -stationMinDist; iy < stationMinDist; iy++) {
				// calculate coordinate to check
				lat = latitude + ix;
				lon = longitude + iy;
				if (lat >= 0 && lon >= 0 && lat <= (gridLat - 1) && lon <= (gridLon - 1)
						&& coordinates[lat][lon] != null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	private static boolean connectionExists(Station s1, Station s2) {
		for (Station[] connection : connections) {
			if ((connection[0].equals(s1) && connection[1].equals(s2))
					|| (connection[0].equals(s2) && connection[1].equals(s1))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param station
	 * @return
	 */
	private static int getConnections(Station station) {
		int i = 0;
		for (Station[] connection : connections) {
			if (connection[0].equals(station) || connection[1].equals(station)) {
				i++;
			}
		}
		return i;
	}

	/**
	 * 
	 */
	private static void loadHer() {
		Database db = Database.getInstance();

		// load stations to database
		db.loadStations(stations);

		// load connections to database
		db.loadConnections(connections);

		// TODO load borderstations to database

		// init db
		db.init();
	}

	/**
	 * 
	 */
	private static void stationNames() {
		// old station names (region 1)
		// String stations =
		// "N�rreport st|Kongens Nytorv st (Metro)|Christianshavn st (Metro)|N�rreport st (Metro)|Holmens Kirke|Christianshavn st. (bus)|Bodenhoffs Plads|Borgergade|Christiansborg|B�rsen|N�rreport st. (bus)|Kronprinsessegade|Str�get|Refshale�en|Kongens Nytorv/Magasin|S�lvtorvet|Esplanaden/St.Kongensgade|Fredericiag./St.Kongensg.|N�rreport st./Gothersgade|Det Kongelige Bibliotek|Kronprinsessegade/S�lvgad|Fr.borggade/Farimagsgade|Esplanaden/Gr�nningen|Fredericiagade/Bredgade|Odd Fellow Pal�et|Dr.Tv�rgade/St.Kongensgad|Stormbroen, Nationalmuseet|Skt. Ann� Gade|Kommunehospitalet|Larslejsstr�de|Jarmers Plads|Ahlefeldtsgade|Lynetten|Statens Museum for Kunst|Krystalgade|Tordenskjoldsgade|Gothersg./�sterfarimagsg.|Nyhavn|S�torvet|Arsenal�en|Ving�rdstr�de/Bremerholm|�ster Voldgade|Fabrikmestervej|Danneskiold-Sams�es All�|Olfert Fischers Gade|Suensonsgade|H�jbro Plads|Skt. Ann� Plads|Gothersgade|Rundetaarn|Fiolstr�de|Gammeltorv|Vesterport st|Dybb�lsbro st|K�benhavn H|Hovedbaneg./Tietgensgade|Enghave Plads|Enghavevej/Vesterbrogade|Dybb�lsbro st. (bus)|Vesterport st. (bus)|Trommesalen/Vesterbrogade|Stormgade/Glyptoteket|Vodroffsvej/Rosen�rns All|R�dhuspladsen|L�ngangstr�de/V. Voldgade|Vesterport st./Kampmannsg|Kalvebod Br./Bernstoffsg.|Vesterbros Torv|Peblinge Dossering|Det Ny Teater|Gyldenl�vesg/Farimagsgade|R�dhuspladsen/ Lurbl�serne|Rysensteensgade|Enghave Plads/Istedgade|Platanvej|Absalonsgade|Fr.berg All�/Vesterbroga.|Polititorvet|Fisketorvet, Dybb�lsbro|Gasv�rksvej/Istedgade|Godsbaneg�rden|Saxogade|Tove Ditlevsens Plads|Hovedbaneg�rd/Vesterbrog.|Hovedbaneg�rd/Tietgensbro|Hovedbaneg�rden, Tivoli|Vandkunsten|Hovedbaneg�rd/Reventlowsg|Otto M�nsteds Plads|K�benhavn Vesterbrogade (fjernbus)|Axeltorv/Studiestr�de|Ny Carlsberg Glyptotek|Nationalmuseet Hovedindgang|B�lowsvej/Thorvaldsensvej|H.C. �rsteds Vej/Danasvej|Fr.berg All�/Kingosgade|Barfods Skole|Frydendalsvej|V�rnedamsvej|Gl.Kongev./H.C. �rstedsv.|B�lowsvej/Gl. Kongevej|Henrik Steffens Vej|Rosen�rns Alle|H.C �rstedsv/Rosen�rns Al|Forum st. (bus)|Platanvej/Fr.berg All�|Danas Plads|Dr. Abildgaards All�|Kastanievej|Vodroffsvej|Henrik Ibsens Vej Midt|Niels Ebbesens Vej|�boulevarden|Det Biovidenskabelige Fakultet|Forum st (Metro)|Vodroffsvej|Fuglebakken st|Peter Bangs Vej st|Fuglebakken st. (bus)|Falkoner All�/Rolighedsve|Frederiksberg R�dhus|Fr.berg Hosp./Nyelandsvej|Nordre Fasanvej/Finsensv.|Peter Bangs Vej st. (bus)|Godth�bsvej/Ndr. Fasanvej|Borups All�/Ndr. Fasanvej|Peter Bangs Vej/Fasanvej|De Sm� Haver|Zoologisk Have|Jagtvej/�gade|Emil Chr. Hansens Vej|Marielystv./R�dm. Steins|Falkoner All�|Frederiksberg st./Solbjergvej|Borups Plads|Dalgas Blvd./P. Bangs Vej|Dalgas Blvd/Finsensvej|Sdr.Jyllands Al/Finsensv.|Roskildevej/Sdr. Fasanvej|Frederiksberg Bredegade|Lindevangs All�|Folkets All�|S�nderjyllands All�|KB Hallen|C.F.Richs Vej/Godth�bsvej|Tesdorpfsvej/Godth�bsvej|Eversvej|Solbjerg Have|Flintholm All�|Frederiksberg Runddel|Gr�ndalsv./C.F. Richs Vej|Hiller�dgade|Magnoliavej|Nyelandsvej Skole|Tesdorpfsvej|Femte Juni Plads|H�sterk�bgade|Mariendalsv./Ndr. Fasanv.|Dalgas Have|Domus Vista|Frederiksberg st. (bus)|Helgesvej|Kammasvej|Mathildevej|Nyelands Plads|Skellet/Roskildevej|Dalgas Bvld/Roskildevej|Frederiksberg Hosp./Vej 8|Hospitalsvej|Dronning Olgas Vej|Frederiksberg Sv�mmehal (bus)|Marielystvej|Marielystvej Midt|Betty Nansens All�|Nordens Plads|Borgm. Godskesens Plads|S�ndermark Kirkeg�rd|Fasanvej st (bus)|Aksel M�llers Have|Langelands Plads|St�hr Johansens Vej|Frederiksberg st (Metro)|Fasanvej st (Metro)|Lindevang st (Metro)|KB Hallen st|Valbyholm|Betty Nansens All�|Ryparken st|Svanem�llen st|Nordhavn st|�sterport st|Nyg�rdsvej/�sterbrogade|Blegdamsvej/Tagensvej|Nords�vej|Ryparken st. (bus)|Stubbel�bsgade|Hans Knudsens Plads|�sterport st./Oslo Plads|Nordhavn st. (bus)|Svanem�llen st. (bus)|Lille Triangel|Lyngbyvej/Haraldsgade|Gartnerivej/Ryparken|Skt. Kjelds Plads|�sterport/Folke B. All�|Trianglen|Emdrup S�park|Vibenshus Runddel|Haraldsg./Lers� Park All�|Ryparken (bus)|Emdrupvej/Lyngbyvej|Parken|Brumleby|�sterbrogade/Jagtvej|�rhusgade/�sterbrogade|Gustav Adolfs Gade|�ster Farimagsgade Skole|Webersgade|Universitetsparken|Rigshospitalet|S�ndre Frihavn|�sterbrogade|Gribskovvej|Jagtvej/Lers�park All�|Nyg�rdsvej|Str.Boulev./Ndr Frihavnsg|Str.Boulevarden/Classensg|Thomas Laubs Gade|Hobrogade|Kalkbr�nderihavnsgade|Pr�st�gade|Randersgade|Str�damvej|Aldersrogade|Fridtjof Nansens Plads|Irmingersgade|Kildev�ldsparken|Lipkesgade|Serridslevvej/Jagtvej|Strand�re|Strandv�nget|Svendborggade|�rhusgade|Aldersrogade/Haraldsgade|Skudehavnsvej|Baltikavej|F�rgehavn Nord|Baltikavej/Kattegatvej|F�rgehavnsvej|Kelds�vej|Indiakaj|Edvard Griegs Gade|Svanem�llen st., Borgerv�nget|Svanem�llen st., Sl�jfen|DFDS Terminalen|Pakhusvej|�sterport st., Sl�jfen|F�lledg�rden|Gammel Kloster|Klosterv�nget|Vibehus|Randersgade 60|�sterbrohuset|Asger Holms Vej|Our�gade|Helsingborggade|Vognmandsmarken|Liv�gade|Strandv�nget|Emdrupvej|H.C. �rstedsvej/�boulev.|Jagtvej/Borups All�|N�rrebro st./Mod City|Tagensvej/Jagtvej|Stefansgade|Griffenfeldsg./Rantzausg.|Fredrik Bajers Plads|Elmegade/N�rrebrogade|Bl�g�rdsgade|Skyttegade|F�lledvej/N�rrebrogade|Hiller�dga./Lundtoftegade|Tagensvej/Haraldsgade|Mimersgade|Prins J�rgens Gade|�girsgade|Arres�gade|Hermodsgade|Kapelvej|N�rrebro Hallen|Ravnsborggade|Rovsingsgade|Sj�llandsgade|Skt. Hans Torv|Sortedam Dossering|Stevnsgade|J�gersborggade|N�rrebros Runddel|Vestamager st (Metro)|�restad st (Metro)|Sundby st (Metro)|Bella Center st (Metro)|Islands Brygge st (Metro)|DR Byen/Universitetet st (Metro)|�restad st|Lergravsparken st (Metro)|Amagerbro st (Metro)|�resundsvej/Strandlodsvej|Lergravsparken st. (bus)|Amager Blv/Amager F�lledv|�resundsvej/Amagerbrogade|Sundby Sejlforening|Islands Brygge/Artilleriv|Bella Center (bus)|Christmas M�llers Plads|Backersvej/Formosavej|Amagerbrog./Vejlands All�|Amagerbro st. (bus)|Vejlands All�/Irlandsvej|Isafjordsgade|Amager Str.Vej|Universitetet, Amager|Lyneborggade|Nordmarksvej|Ved Amagerbanen|Gr�njords Kollegiet/Brydes All�|Raffinaderivej|Skotlands Plads|Sundbyvester Plads|Vejl. All�/R�de Mellemvej|�restad st. (bus)|Fem�ren st / Engvej|Holmbladsgade/Prags Boulevard|Italiensvej/Engvej|Samosvej|Amager Hospital (v. Italiensvej)|HF Sundbyvester|H�jdevangens Skole|S�nderport|Tycho Brahes All�|Artillerivej|Amagerf�lled Skole|S�nderbro Hospital|Brydes All�|Irlandsvej/Englandsvej|Dyvekeskolen|Sundbyvestervej/H�jdevangens Pl.hj.|Vejlands All�/Englandsvej|H�rg�rden|Tingvej|Sundbyvester Park|Ingolfs All�|Fem�ren|HF Kl�vermarken|S�nderbro Skole|Lossepladsv/Vejlands All�|Italiensvej/Backersvej|Axel Heides Gade/Artillerivej|F�lfodvej/Irlandsvej|Kastrupvej/�resundsvej|Wibrandtsvej|Kigkurren|Uplandsgade|Wibrandtsvej/Engvej|Bergthorasgade|Drechselsgade/Artillerivej|G�rdf�stevej|Halfdansgade|Kl�vermarkens Idr�tsanl�g|HF Vennelyst|Kanadavej|Knapmagerstien|Kraftv�rksvej|Slusevej|Toskiftevej|Ved Slusen|Amager Centret|Formosavej/Backersvej|Bremensgade/Holmbladsgade|Bulgariensgade|Florensvej|Kirkeg�rdsvej|Persiensvej|Sundby Kirkeg�rd|Bocentret Sundbyg�rd|Sundparken|Vejlands All�|Amager Strandvej 122|Amagerv�rket|Kraftv�rksvej (�sthavnen)|Smyrnavej|Amagerbrogade/Elbagade|Bella Center st. (bus)|Islands Brygge st/universitetet (bus)|Tingvej/Amagerbrogade|Backersvej|Kastrupvej|Prags Boulevard|Bella Center, Indgang Vest|Digevej|Weidekampsgade|Pallesvej|Svend Vonveds Vej|�resundsvej|Vestamager st (bus)|Otto Baches All�|�resund st (Metro)|Amager Strand st (Metro)|Fem�ren st (Metro)|Thorvald Borgs Gade|Universitetet st. (bus)|Peder Lykke Centret|Ulvefodvej|Amsterdamvej|Amager Hospital|Keplersgade|Gr�kenlandsvej|Amager Hospital, bagindgangen|Sundparken, Lergravsvej|Lergravsvej|Lergravsvej/Strandlodsvej|Skipper Clements All�|Jemtelandsgade|Dalslandsgade|Kl�vermarksvej|Snorresgade|Lerfosgade|H�rg�rden Plejecenter|Store Krog|Sundbyvester Plads/Amagerbrogade|N�rrebro st|Bispebjerg st|Emdrup st|Bellah�j|Emdrup Torv|Fr.borgvej/Fr.sundsvej|Emdrup st./Lers� Parkalle|Bispebjerg st. (bus)|Hiller�dgade/Borups All�|N�rrebro st. (bus)|Hulg�rdsvej/Borups All�|Emdrup st./Tuborgvej|N�rrebro st./Under Banen|Hulg�rds Plads|Bispebjerg Torv|N�rrebro Bycenter|Bispevej|M�gevej/Borups All�|Utterslev Torv|Hyrdevangen/Horsebakken|L�rkebakken|Mosesvinget|Skovstjernevej|�blevej|Birkedommervej|Landsdommervej|Bispebjerg Hosp/Tagensvej|Bispebjerg Hospital|Tuborgvej/Tomsg�rdsvej|Glasvej|Tagensvej/Tuborgvej|Provstevej|Banebrinken|Bispebjerg Kirkeg�rd|Bispebjerg Parkalle|Gr�ndal Centret|Lygten|Tv�rvangen|Utterslevvej|Godth�bsvej|Gr�ndalscentrets Hovedindgang|Gr�ndalscentret �st|Skoleholdervej|Ringertoften|Rentemestervej|Sokkelundsvej|�rnevej|Svanevej|Lygten|Sydhavn st|Enghave st|Bavneh�j All�|Sydhavn st. (bus)|Ellebjerg st. (bus)|Kgs. Enghave, Valbyparken|Toldkammeret|Enghave st./Enghavevej|Sj�l�r st. (bus)|Mozarts Plads|Sluseholmen|B�dehavnsgade|Sydhavns Plads|Teglholmen|Spontinisvej|Scandiagade|Skandiagade/Sydhavnsgade|H. C. �rsted V�rket|Ellebjergvej/Sj�l�r Blvd.|Gustav Bangs Gade|Sankt Ann� Gymnasium|Bavneh�j Hallen|H�ndelsvej|Himmelekspressen|Otto Busses Vej|Rubinsteinsvej|Vestre Kirkeg�rds All�|�stre Teglkaj|Stubm�llevej|Knud Lavards Gade|Teglholmen|Sluseholmen (Havnebus)|Sj�l�r st|Langgade st|Valby st|�lholm Plads|Tofteg�rds Plads/Apoteket|Valby st./Lysh�jg�rdsvej|Tofteg�rds Plads|Ellebjerg st./Folehaven|Langgade st. (bus)|Tofteg.All�/Vigersl. All�|Valby st./P� Broen|Valby Langg./Sdr Fasanvej|Vigerslev Kirke|Folehaven/Vigerslevvej|Bjerreg�rdsvej|Gl.K�ge Landev. Kollegiet|Carl Jacobsens Vej|Peter Bangs Vej/�lekistev|Roskildev/Peter Bangs Vej|Vestre Kirkeg�rd Nord|Trekronerg/C.Jacobsens V.|Centerparken|Vigerslevvej/Urtehaven|Gladbovej|Lykkebovej/Vigerslevvej|Vigerslev Alle st. (bus)|Retortvej/Folehaven|Stakhaven|Vigerslev All� Skole|Hornemanns V�nge|Danhaven|Sj�l�r Boulevard 167-181|�haven|Blommehaven|Fengersvej/Vigerslev All�|G�seb�ksvej|Gerdasgade|Gl. Jernbanevej|Gr�nttorvet|Hansstedvej|Landlystvej|Maribovej|Nakskovvej|Peder Hjorts Vej|S�ndre All�|Tingstedet|Valby Langg/Vigerslev Vej|Vigerslevv/Vigerslev All�|Hestehaven/Folehaven|HF Stien|Centerparken �st|Kirseb�rhaven/Vigerslevv.|Skellet|N�ddehaven|Kirstinedalsvej|Dansh�j st|�lholm st. (bus)|�lholm st|Ny Ellebjerg st|Vigerslev Alle st|Ny Ellebjerg Station|Valby mod Kastrup (fjernbus)|Valby mod Odden (fjernbus)|Valby st. (Hovedindgangen)|Frugthaven|Kirseb�rhavens Plejehjem|Ramsingsvej|Solgavehjemmet|Spinderiet|Gl. Jernbanevej|Glostrup st|Plovmarksvej|Erhvervsv/Ejby Industriv.|Ejbytoften|Ejby Sommerby|Ejby Smedevej|Fabriksparken, Formervang|Hvissingevej|Glostrup Hospital (bus)|Hersted�sterv/Hovedvejen|Glostrup st. (bus)|Naverland, Farverland|Psykiatrisk Center Glostrup|Fabriksparken, Smedeland|Nordre Ringvej|Ejby Industriv/Ndr.Ringv.|Slotsherrensvej|Fabriksparken|Mellemtoftevej|Glostrup st., Glosemosevej|Gl. Landevej/Smedeland|Naverland/Smedeland|Skolevej|Hanevadsbro|Formervangen/Naverland|Hersted�stervej/Naverland|Langagervej|Tranemosevej|Borgm Munks All�|Jyllingevej|Kindebjergvej|Leddet|Hvissingehus|S�en|Sportsvej/Hovedvejen|Springholm|Stadionvej|Stationsparken|Byparken|Digevangsvej|Elmehusene|Gl. Landevej|Glostrup Centret|Ny Vesterg�rd|Paul Bergs�es Vej|Pilehusene|Rugmarksvej|S�ndervangsskolen|Ejbylund|Trippendalscentret|Farverland|Teknisk Skole|Stenager|Poppelstien|R�dk�lkevej|Sortevej|Glostrup Sv�mmehal|S�ndervangsvej|Stenager Omsorgscenter|Nordvangskole|Glostruphallen|Hersted�stervej|Vesterg�rdsvej/Sportsvej|Glostrup Bibliotek (bus)|Sydvestvej|Ved Brandstationen|N�rre Alle|Pr�stehusene|Smedeland|Naverland|Br�ndby�ster st|Roskildevej/Korsdalsvej|�skellet|Br�ndby Haveby afd. 1|Pensionatet Huleg�rden|Hesselager|Br�ndby�ster st. (bus)|Kirkebjerg Torv|Br�ndby R�dhus|Kornmarksvej|Br�ndbyvej/Vallensb�kvej|Aved�re Havnev/Park All�|S�ndre Ringvej/Park All�|Vallensb�kvej|Knudslundvej|Br�ndby Haveby|Abildager/Vallensb�kvej|Nyager/Abildager|Nyager|Br�ndbyvesterv./Park All�|Gildh�j Centret|Midtager/Park All�|Sognevej/Br�ndbyvestervej|Vallensb�k Torvev/Park Al|Bygaden|Industrisvinget|Park All�/Industrivej|S�ndre Ringv./Sydg�rdsvej|Br�ndby�ster Boulevard|Engager|Gammelager|Gr�nnedammen|H�jstens Boulevard|K�rdammen|Langengvej|Ringerlodden|Skelmarksvej|Tranehaven/Park All�|Voldgaden|Banemarksvej 40|Br�ndby Hallen|Br�ndby�ster Torv|DBU All�|Reb�k S�park|Ved Lindelund|HF Rosen 5. afdeling|Horsedammen/H�jstens Blvd|Tj�rneh�jskolen|Br�ndby�ster Kirke|�parken|Br�ndby Haveby afd. 3|Marineg�rden|Kornmarksvej/Banemarksvej|Sydg�rdsvej|Nyg�rds Plads|Br�ndby�ster st., Nyg�rds Plads|Hvidovre st|R�dovre st|Damhustorvet|Erhvervsvej/Islevdalvej|Hvidovre st. (bus)|Islevdalv/Hvidsv�rmervej|R�dovre st. (bus)|Jyllingevej/T�rnvej|Krondalvej/Islevdalvej|R�dovre Centrum|R�dovrevej/Jyllingevej|Slotsherrensv/Islevdalvej|T�rnvej/Slotsherrensvej|Islev, Viemosevej|Roskildevej/T�rnvej|Fortvej/T�rnvej|T�byvej/T�rnvej|R�dager Alle|Damhustorvet/Hvidovrevej|Espelunden|Krondalvej|Randrupvej/Hvidovrevej|Randrupvej|Randrupvej/Roskildevej|HF Isleg�rd|Marielundvej/Ndr Ringvej|Islevg�rd All�|R�dmand Billes Vej|Hvidsv�rmervej|Jyllingevej/Islevdalvej|HF Dano|Gr�ndalslund Kirke|Egeg�rdsvej|Valh�js All�/T�rnvej|Lillek�r|Milestedet|Maglek�r|�rbyg�rd|Slotsherrensv./R�dovrevej|Birkmosevej|Roskildev/Brandholms All�|Islevbrovej|Madumvej|Fjeldhammervej|Gunnek�r|Horsev�nget|Islev Torv|Islevholm|Koldbyvej|Pr�stebakken|R�dovre Kirke|Veronikavej|R�dovrehallen|Hendriksholm Kirke|H�jrisvej|Schweizerdalsvej|Slotsherrens Have|Sylvestervej|T�byvej|Vestbadet|Fortvej|Guldsmedevej|R�dovre R�dhus/T�rnvej|T�rnvej|R�dovreg�rd Museum|Engskr�nten|Slotherrensvej|Islevbadet|Brunevang, Butikstorv|Albertslund st|Albertslundvej|Galgebakken/Gl. Landevej|Albertslund st. (bus)|Vegav�nget|Tranehusene|Kastanjens Kvarter|Risby|Hersted�ster Skole|Kildeg�rden/Stenmosevej|Nordmarkscentret|R�nne All�/Roskildevej|Hyldespj�ldet, Storetorv|Blommeg�rden|Roskildevej/Vridsl�sevej|Bispehusene|Damg�rden|Egelundsskolen|F�ngselsvej|Hjulets Kvarter|Holsbjergvej|Hvedens Kvarter|Kl�verens Kvarter|N�glens Kvarter|Orchidevej|Sti til Herstedh�je|Teglmosevej|Tinghusbakkeg�rd|Tingsletten|Vridsl�selille Skole|Yderg�rdsvej|Albertslund Gymnasium|Falkehusene|Godth�bsstien|Liljens Kvarter|Toftek�rhallen|Bibliotekstorvet|Galgebakken|Hyldespj�ldet|Kastanie All�|Roholmparken|Roholmsvej/Stenmosevej|Vridsl�sestr�de|Roholmsvej|Borgager|Degnehusene|Jydekrogen|Vallensb�k By, Toftevej|B�krenden|Bygaden/M�llestens Ager|Park All�|Vallensb�k Torvevej/Br�ndbyvej|Vallensb�kvej/-torvevej|Idr�ts All�|Skolestien|Lundb�kvej|N�rrestien|Pilestien|Vallensb�k Kirke|Firkl�verparken|Korsagerg�rd|Vallensb�kvej|H�je Taastrup st|Taastrup st|City 2/Blekinge Boulevard|Bl�kildeg�rd|City 2|Taastrup st. (bus)|Helgesh�j All�/Skat|Ikea|Herringl�sevej|Klovtofteparken|N�rreby Torv|H�je Taastrup st. (bus)|S�nderby Torv|Saven|Sognevangen|Sengel�se Kirke/Landsbyg.|Taastrup Idr�tshaller|Teknologisk Institut|Vesterby Torv|Vridsl�semagle|Erik Husfeldts Vej|Sk�ne Blvd/Bohus Blvd|Gadehaveskolen|Helgesh�j All�/M�rk�rvej|Helgesh�j All�|Klovtofte Krydset|Ole R�mers H�j|S�ndertoften/Sydskellet|Birkevej/Cathrinebergvej|Borgerskolen|Teknologisk Institut, �st|K�gevej/Hveen Boulevard|Taastrup Nykirke/K�gevej|Gregersensvej|Sydskellet|�sterby Torv|Agrovej|Dronningeholmen|Engbrinken|G�ngestien|Gr�nh�jskolen|H�rsvinget|Husby All�|Husmandsvej|Industribakken|Klovtofteg�rd|Kuldyssen|Leen|M�rk�rstien|Marievej/Parkvej|Ole R�mers Vej 7-9|Parkskolen|Henriksdal|Sengel�sehallen|Slettetoften|Taastrup Valbyv/Vejtoften|Taastrupg�rdsvej Midt|Espens V�nge|H�je Taastrup Gade|H�je Taastrup R�dhus|Hakkemosevej|Nyh�j Idr�tspark|R�dh�jg�rdsvej|Selsmoseskolen|Store Vejle�|Gasv�rksvej/K�gevej|Budstikken|Hveen Boulevard|R�nnevang Kirke|Rugk�rg�rdsvej|Sengel�se Skole|Taastrup Stationscenter|Taastrup Stationscenter Nord|Sengel�se Plejehjem|�sterparken|Gadehaveg�rd/Sylen|Gadehaveg�rd/Murskeen|Taastrup Nykirke|Ludvig Hegners All�|�ksen|Gasv�rksvej|Halland Boulevard|Bohus Boulevard|Letland All�|Hyldevangen|T�rvevej|Vink - Taastruphave|Vink - Ludvig Heglers All�/ Ibsensvej|Vink - Taastrup Kulturcenter|Vesterparken|Fredensvej|Landsbygaden|Fr�bjerget|Fr�g�rd All�|Fr�haven|Potentilvej|Brunellevej|Nordsj�llands Postcenter|H�ndv�rkerbakken|H�je Taastrup Boulevard|Kroppedal|S�nderby|Stien til EUC|Ish�j st|Baldersb�kstien|Broenge|Centerstien|Vildtbanestien|Ish�j st. (bus)|Ish�jstien|Lundestien|Ish�j Skole|Ish�j Stationsvej|Litauen All�|Skyttestien/Ish�j S�vej|Solh�jvej|Torsbo|Broenge/Vejle�vej|Vejlebrovej|Strandparkstien|Fyrrelunden|Moseg�rdsstien|Industridalen|T�strup Valbyvej|Ellekilde Skole|Bryggerg�rd|Friggasvej|Industriskellet|K�rbo|Kirkebjergg�rd|Bredebjergvej|Industrigrenen|Pedersborgvej|Tangloppen|Torslunde By|Torslunde Kirke|Vejlebroskolen|All�vej|Bredek�rs V�nge|Granlunden|Industribuen|Pilem�llevej|Strandg�rdskolen|Vejle�vej/Ish�j Bygade|Arken|Estland All� (Midt)|Vandrerhjem|Ish�j Bycenter|Strandvangen|Ish�j Idr�ts- og Fritidscenter|�parken|Vejle�parken Lokalcenter|Hedehusene st|Baldersbo|Hedehusene st. (bus)|Hedehusene Skole|Marbjerg|Fl�ng, Soderupvej|Reerslev Skole|Gammel S�vej|Soderup|Baldersbuen";

		// new station names
		String stations = "Fr.sund gymnasium|Frederikssund Torv|Gr�se Bakkeby|Fr.sundsvej/Havelse M�lle|Frederikssund st. (bus)|Oppe Sundby Skole|Sigerslev�ster|Sigerslevvester|Sti til Fr.sund Hospital|Store R�rb�k|Nordsj�llands Hospital - Fr.sund/Kapellet|Sti til Frederikssund Hospital|Kildebakken/H�jvang|Ellek�r|Rosenv�nget/F�rgevej|Skyllebakkegade|H�jskolevej|Agervej|Byvej/H�jvang|Byvej/Linderupvej|Drosselvej/Frejasvej|Dyrl�geg�rds All�|Elsenbakken|Gr�se Skolevej|Lundek�rg�rd|Lille Hofvej|Skelvej|Snostrup|Englystvej|Baneledet|Bassegrav|Bruhnsvej|F�llesvej|Gartnerv�nget|Klinten|L�gismose|Linderupvej|Morb�rv�nget|S�ndervang|Str�lillevej|Strandg�rds All�|Sundbyvej|Tokkeh�jg�rd|Kalv�vej|Borgerv�nget|Falck Station|Falkenborgvej|Golfbanen|Omk�rselsvejen/Kocksvej|Lerager|Pedersholm|Frederikssund Posthus|Sagavej|Stagetornsvej|Varmedalsvej|Bellisvej|Fr.sundsvej/St. R�rb�kvej|Kastanie All�/Roskildevej|Fr.borgvej/�dalsvej|Omk�rselsvej|Byvej/Elsenbakken|Gl. Slangerupvej|Daginstitutionen R�rskov|Bakkekammen, Gr�se Bakkeby|Gr�se Bakkeby skole|Gr�se Bakkeby, Byvej|Hjorth�jvej|Sundbylille|Dalby|Dalby Huse|Gerlev|Solsortevej|Bakkesvinget|Kulhuse Havn|Kyndby|Vink - Kyndbyv�rket|Landerslev|Lyngerup|Kign�skrogen|Vink - Orebjerg All�|Over Dr�by Kirke|Over Dr�by Strand|Skoven Kirke|J�gerspris Slot|T�rslev|Dalby Husevej|Holmeg�rdsv/Landerslevvej|Rugtoften|J�gerspris, JAS|Over Dr�by|M�lleg�rdsskolen|M�llehegnet|Neder Dr�by/V�ngetvej|F�rgeg�rd|Birkemosevej|Smede Bakken|Strandbovej|Bag Skovens Brugs|Barakvejen|Beckersvej|Egelundsvej/Skovn�svej|Hornsved (K�bmand)|Kyndby Huse|Solbakkeskolen|J�gersprislejren|Vink - Hovleddet|Nyhuse|Vestervangsvej|Vink - Seksg�rde/Kulhusevej|Barakvejen/Kulhusvej|H�jvangen/Barakvejen|Kulhus Tv�rvej|Egelyvej|Rosenbakken|Fiskervej|J�gerspris, Dyrn�svej|Pr�steg�rdsvej|skolerne J�gersprie|�lstykke st|Gl. Tofteg�rd st|�lstykke Kirke|Frodebjergvej/Lysh�jvej|Gl. �lstykke/Fr.sundsvej|R�rsangervej|�lstykke st. (bus)|Roarsvej|Svaleh�jvej|Svanholm V�nge|Tangbjerg|Vandmanden|Svestrup|Skyh�j|Astersvej/Violvej|L�rkevej/Udlejrevej|Kildeg�rdsvej|K�benhavnsvej|S�holmvej|Solsikkevej|Ring Nord/Ny Tofteg�rdsv.|L�ven|Ny Tofteg�rdsv./�stervej|Steng�rdsskolen|Svestrupvej|Tusindfrydvej|Krebsen|Normannervej|Skjoldsvej|Udlejreg�rd|V�lundsvej|Fyrrevej/�rnebjergvej|Fiskene|Lupinvej|Vermundsvej|Fyrkatvej|K�rg�rdsvej|Gl. Tofteg�rd st|B�kkeg�rdsskolen|Hans Erik Nielsens Vej|Udlejre Kirke|Stenl�se st|Krogholmvej/Fr.sundsvej|Hyldeg�rdsvej|Ganl�se Skole|Knardrup Bygade|Fr.fredegodsv/Krogholmvej|Stenl�se st. (bus)|Slagslunde Bygade|Stenl�se/Frederikssundsv.|Mosevej/Hvideh�jvej|Galgebakken/M�l�vvej|Farumvej 127|Fluebjergvej|Tranemosevej/Hesselvej|Valmuevej/Stenl�sevej|Bog�g�rdsvej|Farumvej|Ganl�separken|Helsevej|Hesselvangen|Kalkg�rden|Kalveholmvej|Lang�sen|Langeb�k G�rd|Langtoften|Rolandsg�rd|Stenl�se Privatskole|Toppevadvej|Undinevej|Asserh�j|Ganl�se Kirke|Halk�rvej|Hyrdeleddet|Ringbakken|Nyvangs All�|Slagslunde Forsamlingshus|Rosenv�nget/S�sumvej|Gulnarevej|Veks� st|Poppelvang|�strup Trafikplads|Knudsbjergvej|Veks� st. (bus)|S�sum|Veks�/Frederikssundsvej|Veks� Kirke|L�jes�vej|Agervej/Frederikssundsvej|Skovvangsvej|Fugle�jevej|Stenpilstr�de|Thorkildg�rdsvej|Bjellekj�rvej|Korsh�jg�rdsvej|Svinemosevej|Holmeg�rd|R�nne Havn (Bornholm)|Muleby (Bornholm)|Nyker (Bornholm)|Aarsballe (Bornholm)|Snellemark centrum (Bornholm)|R�nne R�dhus (Bornholm)|Knudsker (Bornholm)|Peterskolen (Bornholm)|Sandemandsvej (Bornholm)|Hallebakken/Erlandsg�rdsv. (Bornholm)|Nordskovvej/Almeg�rdsk. (Bornholm)|Gartnervangen (Bornholm)|Borgm.Niels.Vej - Nord (Bornholm)|�stergade/Voldgade (Bornholm)|Harbovej (Bornholm)|Ydunsvej/Asavej (Bornholm)|Lillevangsv. v/ Sandemandsv. (Bornholm)|Centralbiblioteket (Bornholm)|Sagavej/Aakirkebyv. (Bornholm)|Centralsygehuset (Bornholm)|Sagavej/S�nderg�rds All� (Bornholm)|Fredensborgvej (Bornholm)|Strandvejen/Campingplads (Bornholm)|Munch Petersens Vej v/ Kirken  (Bornholm)|Center Lunden (Bornholm)|Vestermarie (Bornholm)|Lufthavnen (Bornholm)|Arnager (Bornholm)|Sorthat v/ Sahara (Bornholm)|Sorthat v/ Havvej (Bornholm)|Nyker Vest (Bornholm)|Nyker  v/ skolen (Bornholm)|Aarsballev./Kongstubbev. (Bornholm)|Aarsballe By/Sdr.Lyngvej (Bornholm)|�byvej/Kirkebyvej (Bornholm)|�g�rdsv/Karlsg�rdsv. (Bornholm)|Sdr.Lyngv/Kongensmark (Bornholm)|Sdr.Lyngv/Dyndeg�rdsv. (Bornholm)|R�nne - St.Torvegade - Kystparken (Bornholm)|R�nne - N�rregade v. Tv�rstr�de (Bornholm)|R�nne - M�llegade (Bornholm)|R�nne - St.Torvegade 57 (Bornholm)|R�nne - �stergade 44a (Bornholm)|R�nne - Ll.Torv (Bornholm)|Borgm.Nielsens Vej - Syd (Bornholm)|Knudsker-Uddannelsescentret (Bornholm)|Haslevej - rute 25 (Bornholm)|R�nne Nord (Bornholm)|R�nne - Haslevej  v/ Ndr.Ringvej  (Bornholm)|R�nne - Haslevej  v/ Efterskolen (Bornholm)|R�nne - Haslevej 138b (Bornholm)|R�nne - Haslevej v/ Almeg�rdsv. (Bornholm)|Ringeby Bro (Bornholm)|R�nne - Blykobbevej Nord (Bornholm)|R�nne - Blykobbevej Midt (Bornholm)|R�nne - Blykobbevej Syd (Bornholm)|R�nne - Landem�rket (Bornholm)|R�nne - Sagavej v/ Fredensborgv. (Bornholm)|R�nne - Fredensborgv. v/ Kabbelejel�kk, (Bornholm)|R�nne - Sdr.Alle v/ kirkeg�rd (Bornholm)|R�nne v/ Rytterg�rden (Bornholm)|R�nne v/ Fredensborg (Bornholm)|R�nne v/ Lers�vej (Bornholm)|R�nne - Strandvejen 47/49 (Bornholm)|�vangsskolen/Smallesund (Bornholm)|S�ndermarksskolen (Bornholm)|R�nne - Aakirkebyvej v/ Sigynsvej (Bornholm)|R�nne v/ Vibeg�rd (Bornholm)|Lillevangsv./Kaneg�rdsv. (Bornholm)|Stavelund (Bornholm)|K�rby (Bornholm)|Erlandsg�rdsvej (Bornholm)|Aakirkebyvej/�stre skole (Bornholm)|R�nne - Aakirkebyvej 40/42 (Bornholm)|Svanekevej/�stre Skole (Bornholm)|Aakirkebyvej/DR Bornholm (Bornholm)|Almindingvej/�stkraft (Bornholm)|R�nne - Almindingsvej v/ Brovangen (Bornholm)|R�nne - Sagavej v/ Paradisvej (Bornholm)|R�nne - Helsevej (Bornholm)|Skovg�rdsv. v/ Pindel�kkegd. (Bornholm)|Skovg�rdsv. v/ L�rkene (Bornholm)|R�nnevej/Skr�dderbakkevej (Bornholm)|Sdr.Landev. v/ Stampenv. (Bornholm)|Sdr.Landev./Skr�dderbakkev. (Bornholm)|Skr�dderbakkevej (Bornholm)|Vellensbyv./Blemmelyngv. (Bornholm)|Bolsterbjerg (Bornholm)|Segenvej/Kongsstubbevej (Bornholm)|Almindingen - Kongemindev. (Bornholm)|R�nne Havn (f�rge)|Almindingen -Koldek.hus (Bornholm)|Aakirkeby - Terminalen (Bornholm)|Pedersker (Bornholm)|Nylars (Bornholm)|Lobb�k (Bornholm)|Egeby (Bornholm)|Sdr. Landevej v. Limensgaden (Bornholm)|�stre S�marken (Bornholm)|Aakirkeby - centrum (Bornholm)|Koldekildehus (Bornholm)|Almindingen - Travbanen (Bornholm)|Aakirkeby - Amindingsvej v/ TV2 (Bornholm)|Pederskervej v/ skolen (Bornholm)|Pedersker Hovedgade 5 (Bornholm)|Sdr.Landevej/Sandvejen (Bornholm)|Nex�vej/Kratg�rdsvej (Bornholm)|Sdr.Landev./Sosevej (Bornholm)|Sdr.landevej/Vaseg�rdsvej (Bornholm)|Strandvejen/Sandvejen (Bornholm)|Aakirkeby - Nybyvej (Bornholm)|Aakirkeby - Kuleborgvej (Bornholm)|Grammeg�rdsvej/Rundl�kkevej (Bornholm)|Fejlereg�rd (Bornholm)|Sosevej (Bornholm)|Tvillingeg�rde (Bornholm)|Springbakkevej (Bornholm)|Almindingsvej/Ekkodalsvej (Bornholm)|�lenevejen/H�jlyngsvej (Bornholm)|Bygaden/Ll.Myreg�rdsvejen (Bornholm)|Produktionsh�jskolen (Bornholm)|Bygaden/Sigtebrovej (Bornholm)|Sigtem�llev./F�rebyvej (Bornholm)|Pedersker skole (Bornholm)|Sdr.Landev./Grammeg�rdsv. (Bornholm)|Pederskervej v/ kirken (Bornholm)|Roseng�rden (Bornholm)|Vasag�rd (Bornholm)|Grammeg�rdsv/Rundl�kkev. (Bornholm)|Hegnedevejen (Bornholm)|�lenevej (Bornholm)|Sandvejen (Bornholm)|Strandvejen/Baunevej (Bornholm)|Almindingen - Lilleborg (Bornholm)|Nyvestcentret (Bornholm)|Snogeb�k v. Turistvej (Bornholm)|Balka (Bornholm)|Nex� rtb. (Bornholm)|Bodils Kirke (Bornholm)|Dueodde (Bornholm)|Snogeb�k v/ Smedevej (Bornholm)|Snogeb�k - Havnevej (Bornholm)|Nex� - Ferskes� (Bornholm)|Nex� v/ Falckvej (Bornholm)|Nex� v/ varmev�rket (Bornholm)|Nex� - Ndr. Strandvej (Bornholm)|Paradisv/Harilds L�kkev. (Bornholm)|Nex� Hallen/N�rrem�llec. (Bornholm)|Nex� - N�rrem�llecentret (Bornholm)|Bodilsker Skole (Bornholm)|R�nnevej v. Lyngvejen (Bornholm)|Dalevejen/Plantagevej (Bornholm)|Slamrebjergv. v.Klintebyv. (Bornholm)|R�nnevej v. Slamrebjergv. (Bornholm)|R�nnevej v/ Skimlevejen (Bornholm)|Langedebyv./Skimlevejen (Bornholm)|�.Slamrevej/Paradisvej (Bornholm)|Kannikkeg�rdsv./Birkevej (Bornholm)|Stenseby (Bornholm)|Holsmyrevejen (Bornholm)|Pouls Kirke (Bornholm)|Poulskervej v/ Skolevejen (Bornholm)|Strandmarksv./Udeg�rdsv. (Bornholm)|Strandmarksv./Fyrvej (Bornholm)|Poulskerhallen (Bornholm)|Pilem�lle (Bornholm)|Brandsg�rdsv./Pederskerv. (Bornholm)|Nex� Camping (BAT)|Nex� v/Nex�huset (BAT)|Ibskervej/N�rrem�llevej (BAT)|Snogeb�k/v Smedevej (BAT)|B�lshavn (Bornholm)|Listed havn (Bornholm)|Svaneke (Bornholm)|Aarsdale (Bornholm)|Paradisb. v/Oksemyrvej (Bornholm)|Br�ndesg�rdshaven (Bornholm)|B�lshavn v/ Sommervej (Bornholm)|Lyrsbyvej/Louisenlundv. (Bornholm)|Svanekevej/Byfogedv. (Bornholm)|Listed Nord (Bornholm)|Listed Syd (Bornholm)|Svaneke v/ Marieg�rden (Bornholm)|Svaneke v/ fodboldbanen (Bornholm)|Svaneke v/ skolen (Bornholm)|Aarsdale - Gaden (Bornholm)|Ibskervej/Paradisbakkevej (Bornholm)|Ibsker (Bornholm)|H�jevej/Br�ndsg�rdhavesv. (Bornholm)|�stermarievej/H�jevej (Bornholm)|Svaneke Skole (Bornholm)|Lindholmsv.v/ Degnebrov. (Bornholm)|Svaneke - Gryneparken (Bornholm)|Svaneke - M�llebakken (Bornholm)|Svaneke v.Madvigs Minde (BAT)|�stermarie (Bornholm)|�stermarie v/ skolen (Bornholm)|�stermarie - Godth�bsvej (Bornholm)|�stermarie v/ kirken (Bornholm)|Svanekevej/Lindesvej (Bornholm)|Randkl�vevej/Kirkebyvej (Bornholm)|Randkl�vevej v/ Hvide Hus (Bornholm)|�stermarie skole (Bornholm)|Lindetsvej v/ Svanekevej (Bornholm)|Aspevej/Dalslundevej (Bornholm)|Svanekevej/B�lshavnvej (Bornholm)|Kirkebyvej v/ Randkl�vev. (Bornholm)|Almindingensvej/�l�sevej (Bornholm)|Dyndevej/Havreh�jvej (Bornholm)|�lenevej/Plantagevej (Bornholm)|�lenevej/Myreg�rdsvej (Bornholm)|�sterlars (Bornholm)|�sterlars Rundkirke (Bornholm)|Gudhjem - museet (Bornholm)|Gudhjem Havn (Bornholm)|R� (Bornholm)|Helligdommen - Kunstmuseet (Bornholm)|Melsted (Bornholm)|Gudhjem - kirkeg�rd (Bornholm)|Saltuna v/Kjeldsebyvej (Bornholm)|Gudhjem f�rgehavn (Bornholm)|Middelaldercentret (Bornholm)|Gudhjem �vre Busstation (Bornholm)|�sterlars v/ skolen (Bornholm)|�sterlars v/ friskolen (Bornholm)|�sterlars - Gudhjemvej|Gudhjem v/ plejehejm (Bornholm)|Gudhjem - N�rresand (Bornholm)|D�ndalen (Bornholm)|Sdr.Strandvej v/ B�dsted (Bornholm)|Stammershalle (Bornholm)|Melstedvej v/ Kobbe�en (Bornholm)|Nordlandets Rideklub (Bornholm)|R�bro (Bornholm)|Klemenskervej v/ Ridehal (Bornholm)|R� v/ kirken (Bornholm)|�sterlars skole (Bornholm)|Gamlev�ldev/Smedevej (Bornholm)|Stavsdalv./Oksholmv. (Bornholm)|Studebyv/Risenholmsv. (Bornholm)|Oksholmsvej v/�sedamsvej (Bornholm)|Humledal (Bornholm)|Helligdomsv/Klemenskerv. (Bornholm)|Allinge - Lindeplads (Bornholm)|Sandvig - gl. station (Bornholm)|Olsker (Bornholm)|Hammershus (Bornholm)|Tejn Havn (Bornholm)|Sandk�s (Bornholm)|Allinge Havn (Bornholm)|Borrelyngvej v. Lyngholt (Bornholm)|Allinge - T�rnhuset (Bornholm)|Allinge - Havnegade (Bornholm)|Sandvig v/ Hotel Sandvig (Bornholm)|Allinge - Kongesk�rskolen (Bornholm)|Sandvig - Langebjergvej (Bornholm)|Allinge - R�nnevej (Bornholm)|Hammerhavn (Bornholm)|Sdr.Strandvej v/ K�sevej (Bornholm)|Tejn / M�llegade (Bornholm)|Tejn Syd (Bornholm)|Tejn v/ Klippestien (Bornholm)|Tejn v/ Smedebakken (Bornholm)|Tejn / Smedel�kken (Bornholm)|Smedel�kken Syd|Tejn Nord (Bornholm)|Sandk�sbakken (Bornholm)|Allinge - Tejnvej/Gr�nnedalsvej (Bornholm)|Allinge - Tejnvej/Genvej (Bornholm)|Kongesk�rskolen (Bornholm)|Allinge - Borrelyngsvej (Bornholm)|Allinge (fjernbus)|Klemensker (Bornholm)|Klemensker Vest (Bornholm)|Klemensker v/ Bj�rnem�llev. (Bornholm)|Klemensker v/ Aag�rdsv. (Bornholm)|Klemensker skole (Bornholm)|Splitsg�rdsv./Sdr.Lyngv. (Bornholm)|Vedby (Bornholm)|Bedegadev/Petersborgv. (Bornholm)|N.Lyngv./Krashavev. (Bornholm)|Lyngholt (Bornholm)|Hasle - Torvet (Bornholm)|Jons Kapel - Landevejen (Bornholm)|Vang havn (Bornholm)|Rutsker (Bornholm)|Vang v/  Udsigten  (Bornholm)|F�lledvej v/ Levkavej (Bornholm)|F�lledvej v/ Tofte (Bornholm)|F�lledvej v/ Hvide Hus (Bornholm)|F�lledvej v/ Glasv�rksvej (Bornholm)|F�lledvej v/ Campanella (Bornholm)|F�lledvej v/ H.C. Sigerstedsvej (Bornholm)|Hasle F�lled (Bornholm)|Hasle - Daml�kkevej (Bornholm)|Hasle Nord (Bornholm)|Hasle - Toftelunden (Bornholm)|Borrelyngsv. v/ Helligpederv. (Bornholm)|Vang overfor nr. 75 (Bornholm)|Rutsker v/ Fuglesangsvej (Bornholm)|Svalh�j (Bornholm)|Borrelyngvej v. Teknisk saml.|Trekroner st|Roskilde st|�gerup, Omsorgscentret|�m|Vestvejen/Lindenborgvej|Lammegade|Alfarvejen|R�dhuset|Bakkekammen|H�jvangsvej/Tingvej|Herringl�se Sportsplads|Boserup|Roskilde Sv�mmehal|Vestergade|Roskilde st./Ny Terminal|Roskilde Idr�tscenter|Darup|�blehaven|Holb�kvej/F�lledvej|Thomas Bredsdorffs All�|Svendborgvej|Gevninge/Lindenborgvej|Glimvej|Gunds�lille|Gulddyssevej|�stervangsskolen|Gyvelvej/Bymarken|Korsk�r|Himmelevg�rd|Himmelev, Egelund|S�nderlundsvej|M�llehusvej|Strandparken|Hvedstrup|Katedralskolen|Kirkerup|Kongebakken|Dronning Margrethes Vej|Kornerup|Kamstrup, Gadek�ret|Margretheh�bsvej|Lindenborg|Trekroner st. (bus)|Handelsskolen, Maglelunden|Navervej|Neerg�rdsparken|Oldvejsparken|Osted Friskole|Osted Kro|Pr�stemarksvej/�stbyvej|Bjergmarken|Roskilde st., Sygehuset|Ris�|R�dmandshaven|Sankt J�rgens Skole|�sthospitalet|Vesthospitalet|Schmeltz Plads|Roskilde st./Gl. Terminal|Roskilde st./Hersegade|Roskilde Ring|St�ndertorvet|Store Valby|S�stien/Kongemarksvej|Svogerslev|T�gerup|Terrasserne|Tune Kirke|Veddelev|Vor Frue Kirke|Vindinge, Ved Kirken|Lotusvej|Pilevej/Tune Parkvej|Tune Center|Lillevangsvej|Guldblommevej|Margretheskolen|Stenlandsvej|Mannerupvej|Teglv�rksvej/Kamstrupvej|Kamstrup/Kamstrupvej|Roskilde Hjemmet|M�rbjergv�nget|Hedeland/Tunevej|Jernbanegade/Hersegade|Klosterengen/Rundk�rslen|Klosterengen/Ternevej|M�llehusvej/Vestergade|Koldekildevej|Vink - Koldekildevej/St.Valbyvej|Neerg�rdsvej/Holb�kvej|Solv�nget/Ternevej|Himmelev, Store Valbyvej|Tj�rebyvej/Tj�reby|Kornerup/Gl. Landevej|Skovdalen|Gunds�lillevej|�bakken|Helligkorsvej/F�lledvej|Ostedhallen|Skovbov�ngets All�|Duevej|Henrik Nielsens Vej|Gammelg�rdsvej/Hovedvejen|Hejnstrupvej|Vink - Hejnstrup|Himmelev Skole|Margretheskolen/Sognevej|Niels Frederiksens Vej|S�toften|R�rmosen|Hedegade|Stamvej|R�nneb�rparken|Ringparken|�stre Ringvej/�stbyvej|�stre Ringvej|�stre Ringvej/Vindingevej|Absalons Skole|Bakkedraget|Roskilde Handelsskole|H�kerstr�de|Jonstrupvej/Hovedvejen|Klostermarksskolen|Byageren/Klosterengen|Ternehaven|M�llevej (�st)|N�rregade|Roskilde st./Hestetorvet|Gartnervang|Sankt Peders Str�de|Himmelev Sognevej|Hejrevej|Svogerslev Kirke|Lykkeg�rdsvej|�r�vej|Bauneh�jv/Veddelev Bygade|Bausager|�sthospitalet Nord|Bistrup V�nge|Boserup Skov|Brovej|Dyrskueplads|F�lledg�rden|Fenrisvej|Guldborgvej|Nyvej/Sognevej|Gunds�magle, R�dhuset|Undervisningscenter|Herringl�se Bygade|Holb�kvej|L�ddenh�j, Vestergade|Hyldestien|Jordbrugsskolen|Kirkebjerg|Knud den Stores Vej|Kongemarken|Lavringemose|Vink - Lejre �|Lindeg�rden|Lufthavnsvej|Prins Buris Vej|Sankt Clara Vej|Tr�g�rden|Vor Frue Hovedgade|Assensvej|Bygv�nget|Dalager/Gevninge Bygade|Dronning Emmas Vej|Dronning Sofies Vej|Eleonoravej|Langvad, Glim Skole|Gyldenk�rnevej|Hedeboparken|Ro�s Torv|Himmelev Gymnasium|Kumlehusvej|Lyngageren|N�dager|Nordh�jen|Svogerslev, S�bredden|Sankt Ibs Vej|Spragleh�jvej|Strandh�jen|Roskilde Postcenter|Magleh�jen|Vikingeskibshallen|Asylgade|Kong Valdemars Vej|Hestehavevej|�brinken|�vej/Sognevej|B�gevej/Tune Parkvej|Baldersvej|Fiskerg�rdsvej|Hedev�nget|Ved Holb�kmotorvejen|Himmelev, Kragholm|Hyldek�rparken|Kornerups V�nge|L�ddenh�j/Holb�kvej|Motelvej|R�rsangervej/Klosterengen|S�ndergade/Tune Bygade|Strandengen|Sct. J�rgensbjerg Plejehjem|Vestre Kirkevej|Ahornvej|Hedegade/M�llehusvej|Sn�versti|Fyrrevej|Troldeh�j|L�ddenh�j|Wiemosen|Langebjerg/Marbjergvej|RUC (Syd)|Skt. J�rgens Vej|Gunds�magle, Rosentorvet|Gunds�magle Kirke|Flyveskolen|Roskilde Lufthavn|Vindinge, Rosenvang|Smedeg�rdsparken|Svale�vej|Vink - Lille Valby|Astersvej|Tune Mark|Himmelev Center|Bernadotteg�rden|Valhalvej|Margretheg�rden|Lillehjemmet|Bymarken|�stre Ringvej/Gyvelvej|Asterscentret|Isafjordvej, Munks�gaard|Link�pingvej|Mindstrupg�rd|Roskilde Sygehus|Roskilde Sygehus Hovedindgang|R�dmandshaven/M�llehusvej|Neerg�rdsvej|H�jvangsv�nge|Trekroner Skole|Margretheh�b|Lysalleen|Genoptr�ningscenter|Byvejen|Kl�verdalen|Kristiansminde Plejecenter|Henrik Nielsens Vej|Firkl�vervej|Hyrdeh�j Bygade|Hyrdeh�j|Ledreborg All�|Roskilde mod Odden (fjernbus)|Roskilde mod Kbh. (fjernbus)|RUC (�st)|Storem�llev�nget|Ejboparken|�sterled (Lejre)|Teknisk skole, Maglelunden|Hospice Sj�lland|T�nsbjergvej|Lindeg�rdsparken, Boserupvej|Katinge Bygade|Katingevej|Strandvej|Herslev Kirke|Tr�llerup|Gevninge Kirke|Birkholmvej|Alfarvejen|�holmvej|S�ndertoften|Stentoften|Langetoften|Osted kirke|Bauneh�j|Kongemarksvej|Risen|Landbrugsskolen Roskilde|Jernbaneviadukten|Nordmarksvej/Osvej|Jyllingecentret|V�rebrovej/Osvej|Kl�vervej/R�dalsvej|R�dalsvej/V�rebrovej|Agerskellet|M�llevej/M�llehaven|R�mosevej|Egesvinget|L�nager|Paulsvej|R�dalsvej|M�llehaveg�rd|L�hegnet|Lindeg�rd|Markskellet|St�revej|�stby|Bonderupvej|Ferslev/ved gadek�ret|Manderup|Ny Krogstrup|Onsved|Vink - R�gerup|S�nderby|Sels� Kirke|Vink - Skibby Industrivej|M�lleh�j/Skuldelev skole|Vejleby|Vellerup Kirke|Venslev|Kildevej/�stbyvej|Elmevej|Skibbyh�j|Skibby, Ved Skuldelevvej|Damg�rdsvej/Hovedgaden|Bonderup Old|Krogstrup Kirke|Onsved Huse|Skibby Kirke|Egevej|Teglv�rksvej/�stbyvej|Vibevej/Marb�khallen|Birkeb�kvej|Hammer Bakke|Skibby R�dhus|Skuldelev Kirke|K�rvej/Sels�vej|Vink - Lindeg�rdsparken|Skuldelev skole|Marb�kskolen|Ferslev skole|Hangh�j|Svanholm All�|Torpevej|Vink - Ungdomsskolen/Nyvej|Ferslev, pr�steg�rden|Onsved|Englerup|Kirke Sonnerup/Englerupv.|Korsvejen|Kirke S�by Kirke|Langtvedkrydset|Rye|Torkilstrup|Vintre M�lle|Borrevejle Skov|Magnolievej/Egevej|Borrevejle|Munkholmv./Hornsherredvej|Lindevangsvej|Egeg�rd/Landevejen|Vink - Frihedsg�rd|Bomg�rd|Dyvelslyst|Vink - Acacievej/Landevejen|Vink - Rye Kirke|Om�vej|Vink - Egtvedg�rd|Vink - Skovvejen|Vink - Torkilstrupvej (Dyrl�gen)|M�lleg�rdsvej|Vink - Ordrup|Vink - Ryegaard|Skolevang|Spurvevej|Bjergskovvej|Vink - Lyndbyparken|Uglestrup|Vink - Havrev�nget|Vink - Bygmarken|Vink - Englerup, Skovstien|Vink - Skovstien|Vink - Kirke Sonnerup Skole|Vink - Dyvelslyst|Thorsvej|Ejby, Buen|Ejby, Dyssemosen|Ejby Strand, �vej|Gersh�j, Ved Kirken|Kyndel�se|Lille Karleby|Lyndby, Lyndbyparken|N�rre Hyllinge|S�by (Sj�lland)|Store Karleby|Sydmarksvej/M�lleh�j|Vink - Hvidemoseg�rd|Krabbesholm/Elverdamsvej|Vink - Krabbesholm|Kyndel�se, Kyndel�sevej|Biltris|Biltris, Elverdamsvej|Big�rdsvej|Hyllingeparken|Kirke Hyllinge|S�by Kirke|Kyndel�se, Sydmarksvej|Sydmarksvej/Elverdamsvej|Tv�rvej/Karlebyvej|Vink - Vandv�rket/Hyrdehuset|N�rrevang|Vibevej/Gersh�jvej|Vink - �husene|Vink - Brydevang|Vink - Egholm Hovedg�rd|S�by Gersh�j Skole|Karleby Forsamlingshus|Vink - Kirke Hyllinge Kirke|Lyndby Kirke|Vink - Polakhuset|Vink - Skr�ppenk�rg�rd|Vink - Treh�je|Christian Hansens Vej|Gersh�jg�rdsvej|S�byparken|Strandbakken|Egevej/Ejby Strandvej|M�lleh�jvej|Brydevang/Hornsherredvej|Rytterlodden|Vink - Vestervang/Karlebyvej|St�rev�nget|Ils�vej|Ejby/Bramsn�svigskolen|Knudsvej|Kornbakken|Vink - Drejerbakken|Vink - Jenslev|Vink - Lyndby, Ved K�bmanden|Vink - �h�jg�rd|Kirke Hyllinge Skole|Bramsn�svighallen|Karlebyvej|Ammersh�j|Vink - R�de Smedie|Vink - Langtved|Vink - S�by Gersh�j Skole|Hornsherredvej|Hornsherredvej|Hornsherredvej|Ringsted st|Ringstedvej|Lundeg�rdsvej|Skovbo Efterskole|Slimmingevej/Ringstedvej|Slimminge, Egelundsvej|Bringstrupvej|Langemosevej|Sj�llandsgade/Hovmarksvej|Maglemosevej/Mosev�nget|�rslevvestervej|Kyringe|Sigersted|Langeb�kg�rd|Skeller�dvej|H�m M�llevej|H�m vendeplads|Vetterslev / N�stvedvej|Vetterslev (Bygaden)|Vetterslev-H�m skole|Haslevvej/Bragesvej|Haslevvej|Galtehus|Haslevvej|Pr�stebrovej|Farendl�sevej|Havemarksvej|�rslev|Kv�rkebyvej|Adamsh�jvej|K�rehave|Bondebjergvej|Bakkeg�rdsvej|Nordbjergvej|Bringstrup|Holb�kvej|Eventyrvej|Estrup|Holb�kvej";
		

		StringTokenizer st = new StringTokenizer(stations);
		for (int i = 0; i < 1000; i++)
			stationNames[i] = st.nextToken("|");
	}

	/**
	 * insert borderstations according to convention
	 */
	private static void borderStations() {
		String[] stationNames = { "BS12a", "BS12b", "BS25a", "BS25b", "BS23a", "BS23b" };
		int[] stationIDs = { 1000, 1001, 1002, 1003, 1004, 1005 };
		int latitudes[] = { 250, 500, 750, 1000, 1000, 1000 };
		int longitudes[] = { 0, 0, 0, 250, 500, 750};

		// add to list
		for (int i = 0; i < numBorderStations; i++) {
			// create station object
			stations[numStations + i] = new Station(longitudes[i], latitudes[i], stationNames[i], stationIDs[i],
					numStations + i);

			// generate number of connections wanted
			connsWanted[numStations + i] = r.nextInt(maxConns + 1 - minConns) + minConns;

			// save reference to object on coordinate
			coordinates[latitudes[i]][longitudes[i]] = stations[numStations + i];
		}
	}

}