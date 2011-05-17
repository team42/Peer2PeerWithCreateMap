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
		// "Nørreport st|Kongens Nytorv st (Metro)|Christianshavn st (Metro)|Nørreport st (Metro)|Holmens Kirke|Christianshavn st. (bus)|Bodenhoffs Plads|Borgergade|Christiansborg|Børsen|Nørreport st. (bus)|Kronprinsessegade|Strøget|Refshaleøen|Kongens Nytorv/Magasin|Sølvtorvet|Esplanaden/St.Kongensgade|Fredericiag./St.Kongensg.|Nørreport st./Gothersgade|Det Kongelige Bibliotek|Kronprinsessegade/Sølvgad|Fr.borggade/Farimagsgade|Esplanaden/Grønningen|Fredericiagade/Bredgade|Odd Fellow Palæet|Dr.Tværgade/St.Kongensgad|Stormbroen, Nationalmuseet|Skt. Annæ Gade|Kommunehospitalet|Larslejsstræde|Jarmers Plads|Ahlefeldtsgade|Lynetten|Statens Museum for Kunst|Krystalgade|Tordenskjoldsgade|Gothersg./Østerfarimagsg.|Nyhavn|Søtorvet|Arsenaløen|Vingårdstræde/Bremerholm|Øster Voldgade|Fabrikmestervej|Danneskiold-Samsøes Allé|Olfert Fischers Gade|Suensonsgade|Højbro Plads|Skt. Annæ Plads|Gothersgade|Rundetaarn|Fiolstræde|Gammeltorv|Vesterport st|Dybbølsbro st|København H|Hovedbaneg./Tietgensgade|Enghave Plads|Enghavevej/Vesterbrogade|Dybbølsbro st. (bus)|Vesterport st. (bus)|Trommesalen/Vesterbrogade|Stormgade/Glyptoteket|Vodroffsvej/Rosenørns All|Rådhuspladsen|Løngangstræde/V. Voldgade|Vesterport st./Kampmannsg|Kalvebod Br./Bernstoffsg.|Vesterbros Torv|Peblinge Dossering|Det Ny Teater|Gyldenløvesg/Farimagsgade|Rådhuspladsen/ Lurblæserne|Rysensteensgade|Enghave Plads/Istedgade|Platanvej|Absalonsgade|Fr.berg Allé/Vesterbroga.|Polititorvet|Fisketorvet, Dybbølsbro|Gasværksvej/Istedgade|Godsbanegården|Saxogade|Tove Ditlevsens Plads|Hovedbanegård/Vesterbrog.|Hovedbanegård/Tietgensbro|Hovedbanegården, Tivoli|Vandkunsten|Hovedbanegård/Reventlowsg|Otto Mønsteds Plads|København Vesterbrogade (fjernbus)|Axeltorv/Studiestræde|Ny Carlsberg Glyptotek|Nationalmuseet Hovedindgang|Bülowsvej/Thorvaldsensvej|H.C. Ørsteds Vej/Danasvej|Fr.berg Allé/Kingosgade|Barfods Skole|Frydendalsvej|Værnedamsvej|Gl.Kongev./H.C. Ørstedsv.|Bülowsvej/Gl. Kongevej|Henrik Steffens Vej|Rosenørns Alle|H.C Ørstedsv/Rosenørns Al|Forum st. (bus)|Platanvej/Fr.berg Allé|Danas Plads|Dr. Abildgaards Allé|Kastanievej|Vodroffsvej|Henrik Ibsens Vej Midt|Niels Ebbesens Vej|Åboulevarden|Det Biovidenskabelige Fakultet|Forum st (Metro)|Vodroffsvej|Fuglebakken st|Peter Bangs Vej st|Fuglebakken st. (bus)|Falkoner Allé/Rolighedsve|Frederiksberg Rådhus|Fr.berg Hosp./Nyelandsvej|Nordre Fasanvej/Finsensv.|Peter Bangs Vej st. (bus)|Godthåbsvej/Ndr. Fasanvej|Borups Allé/Ndr. Fasanvej|Peter Bangs Vej/Fasanvej|De Små Haver|Zoologisk Have|Jagtvej/Ågade|Emil Chr. Hansens Vej|Marielystv./Rådm. Steins|Falkoner Allé|Frederiksberg st./Solbjergvej|Borups Plads|Dalgas Blvd./P. Bangs Vej|Dalgas Blvd/Finsensvej|Sdr.Jyllands Al/Finsensv.|Roskildevej/Sdr. Fasanvej|Frederiksberg Bredegade|Lindevangs Allé|Folkets Allé|Sønderjyllands Allé|KB Hallen|C.F.Richs Vej/Godthåbsvej|Tesdorpfsvej/Godthåbsvej|Eversvej|Solbjerg Have|Flintholm Allé|Frederiksberg Runddel|Grøndalsv./C.F. Richs Vej|Hillerødgade|Magnoliavej|Nyelandsvej Skole|Tesdorpfsvej|Femte Juni Plads|Høsterkøbgade|Mariendalsv./Ndr. Fasanv.|Dalgas Have|Domus Vista|Frederiksberg st. (bus)|Helgesvej|Kammasvej|Mathildevej|Nyelands Plads|Skellet/Roskildevej|Dalgas Bvld/Roskildevej|Frederiksberg Hosp./Vej 8|Hospitalsvej|Dronning Olgas Vej|Frederiksberg Svømmehal (bus)|Marielystvej|Marielystvej Midt|Betty Nansens Allé|Nordens Plads|Borgm. Godskesens Plads|Søndermark Kirkegård|Fasanvej st (bus)|Aksel Møllers Have|Langelands Plads|Stæhr Johansens Vej|Frederiksberg st (Metro)|Fasanvej st (Metro)|Lindevang st (Metro)|KB Hallen st|Valbyholm|Betty Nansens Allé|Ryparken st|Svanemøllen st|Nordhavn st|Østerport st|Nygårdsvej/Østerbrogade|Blegdamsvej/Tagensvej|Nordsøvej|Ryparken st. (bus)|Stubbeløbsgade|Hans Knudsens Plads|Østerport st./Oslo Plads|Nordhavn st. (bus)|Svanemøllen st. (bus)|Lille Triangel|Lyngbyvej/Haraldsgade|Gartnerivej/Ryparken|Skt. Kjelds Plads|Østerport/Folke B. Allé|Trianglen|Emdrup Søpark|Vibenshus Runddel|Haraldsg./Lersø Park Allé|Ryparken (bus)|Emdrupvej/Lyngbyvej|Parken|Brumleby|Østerbrogade/Jagtvej|Århusgade/Østerbrogade|Gustav Adolfs Gade|Øster Farimagsgade Skole|Webersgade|Universitetsparken|Rigshospitalet|Søndre Frihavn|Østerbrogade|Gribskovvej|Jagtvej/Lersøpark Allé|Nygårdsvej|Str.Boulev./Ndr Frihavnsg|Str.Boulevarden/Classensg|Thomas Laubs Gade|Hobrogade|Kalkbrænderihavnsgade|Præstøgade|Randersgade|Strødamvej|Aldersrogade|Fridtjof Nansens Plads|Irmingersgade|Kildevældsparken|Lipkesgade|Serridslevvej/Jagtvej|Strandøre|Strandvænget|Svendborggade|Århusgade|Aldersrogade/Haraldsgade|Skudehavnsvej|Baltikavej|Færgehavn Nord|Baltikavej/Kattegatvej|Færgehavnsvej|Keldsøvej|Indiakaj|Edvard Griegs Gade|Svanemøllen st., Borgervænget|Svanemøllen st., Sløjfen|DFDS Terminalen|Pakhusvej|Østerport st., Sløjfen|Fælledgården|Gammel Kloster|Klostervænget|Vibehus|Randersgade 60|Østerbrohuset|Asger Holms Vej|Ourøgade|Helsingborggade|Vognmandsmarken|Livøgade|Strandvænget|Emdrupvej|H.C. Ørstedsvej/Åboulev.|Jagtvej/Borups Allé|Nørrebro st./Mod City|Tagensvej/Jagtvej|Stefansgade|Griffenfeldsg./Rantzausg.|Fredrik Bajers Plads|Elmegade/Nørrebrogade|Blågårdsgade|Skyttegade|Fælledvej/Nørrebrogade|Hillerødga./Lundtoftegade|Tagensvej/Haraldsgade|Mimersgade|Prins Jørgens Gade|Ægirsgade|Arresøgade|Hermodsgade|Kapelvej|Nørrebro Hallen|Ravnsborggade|Rovsingsgade|Sjællandsgade|Skt. Hans Torv|Sortedam Dossering|Stevnsgade|Jægersborggade|Nørrebros Runddel|Vestamager st (Metro)|Ørestad st (Metro)|Sundby st (Metro)|Bella Center st (Metro)|Islands Brygge st (Metro)|DR Byen/Universitetet st (Metro)|Ørestad st|Lergravsparken st (Metro)|Amagerbro st (Metro)|Øresundsvej/Strandlodsvej|Lergravsparken st. (bus)|Amager Blv/Amager Fælledv|Øresundsvej/Amagerbrogade|Sundby Sejlforening|Islands Brygge/Artilleriv|Bella Center (bus)|Christmas Møllers Plads|Backersvej/Formosavej|Amagerbrog./Vejlands Allé|Amagerbro st. (bus)|Vejlands Allé/Irlandsvej|Isafjordsgade|Amager Str.Vej|Universitetet, Amager|Lyneborggade|Nordmarksvej|Ved Amagerbanen|Grønjords Kollegiet/Brydes Allé|Raffinaderivej|Skotlands Plads|Sundbyvester Plads|Vejl. Allé/Røde Mellemvej|Ørestad st. (bus)|Femøren st / Engvej|Holmbladsgade/Prags Boulevard|Italiensvej/Engvej|Samosvej|Amager Hospital (v. Italiensvej)|HF Sundbyvester|Højdevangens Skole|Sønderport|Tycho Brahes Allé|Artillerivej|Amagerfælled Skole|Sønderbro Hospital|Brydes Allé|Irlandsvej/Englandsvej|Dyvekeskolen|Sundbyvestervej/Højdevangens Pl.hj.|Vejlands Allé/Englandsvej|Hørgården|Tingvej|Sundbyvester Park|Ingolfs Allé|Femøren|HF Kløvermarken|Sønderbro Skole|Lossepladsv/Vejlands Allé|Italiensvej/Backersvej|Axel Heides Gade/Artillerivej|Følfodvej/Irlandsvej|Kastrupvej/Øresundsvej|Wibrandtsvej|Kigkurren|Uplandsgade|Wibrandtsvej/Engvej|Bergthorasgade|Drechselsgade/Artillerivej|Gårdfæstevej|Halfdansgade|Kløvermarkens Idrætsanlæg|HF Vennelyst|Kanadavej|Knapmagerstien|Kraftværksvej|Slusevej|Toskiftevej|Ved Slusen|Amager Centret|Formosavej/Backersvej|Bremensgade/Holmbladsgade|Bulgariensgade|Florensvej|Kirkegårdsvej|Persiensvej|Sundby Kirkegård|Bocentret Sundbygård|Sundparken|Vejlands Allé|Amager Strandvej 122|Amagerværket|Kraftværksvej (Østhavnen)|Smyrnavej|Amagerbrogade/Elbagade|Bella Center st. (bus)|Islands Brygge st/universitetet (bus)|Tingvej/Amagerbrogade|Backersvej|Kastrupvej|Prags Boulevard|Bella Center, Indgang Vest|Digevej|Weidekampsgade|Pallesvej|Svend Vonveds Vej|Øresundsvej|Vestamager st (bus)|Otto Baches Allé|Øresund st (Metro)|Amager Strand st (Metro)|Femøren st (Metro)|Thorvald Borgs Gade|Universitetet st. (bus)|Peder Lykke Centret|Ulvefodvej|Amsterdamvej|Amager Hospital|Keplersgade|Grækenlandsvej|Amager Hospital, bagindgangen|Sundparken, Lergravsvej|Lergravsvej|Lergravsvej/Strandlodsvej|Skipper Clements Allé|Jemtelandsgade|Dalslandsgade|Kløvermarksvej|Snorresgade|Lerfosgade|Hørgården Plejecenter|Store Krog|Sundbyvester Plads/Amagerbrogade|Nørrebro st|Bispebjerg st|Emdrup st|Bellahøj|Emdrup Torv|Fr.borgvej/Fr.sundsvej|Emdrup st./Lersø Parkalle|Bispebjerg st. (bus)|Hillerødgade/Borups Allé|Nørrebro st. (bus)|Hulgårdsvej/Borups Allé|Emdrup st./Tuborgvej|Nørrebro st./Under Banen|Hulgårds Plads|Bispebjerg Torv|Nørrebro Bycenter|Bispevej|Mågevej/Borups Allé|Utterslev Torv|Hyrdevangen/Horsebakken|Lærkebakken|Mosesvinget|Skovstjernevej|Æblevej|Birkedommervej|Landsdommervej|Bispebjerg Hosp/Tagensvej|Bispebjerg Hospital|Tuborgvej/Tomsgårdsvej|Glasvej|Tagensvej/Tuborgvej|Provstevej|Banebrinken|Bispebjerg Kirkegård|Bispebjerg Parkalle|Grøndal Centret|Lygten|Tværvangen|Utterslevvej|Godthåbsvej|Grøndalscentrets Hovedindgang|Grøndalscentret Øst|Skoleholdervej|Ringertoften|Rentemestervej|Sokkelundsvej|Ørnevej|Svanevej|Lygten|Sydhavn st|Enghave st|Bavnehøj Allé|Sydhavn st. (bus)|Ellebjerg st. (bus)|Kgs. Enghave, Valbyparken|Toldkammeret|Enghave st./Enghavevej|Sjælør st. (bus)|Mozarts Plads|Sluseholmen|Bådehavnsgade|Sydhavns Plads|Teglholmen|Spontinisvej|Scandiagade|Skandiagade/Sydhavnsgade|H. C. Ørsted Værket|Ellebjergvej/Sjælør Blvd.|Gustav Bangs Gade|Sankt Annæ Gymnasium|Bavnehøj Hallen|Händelsvej|Himmelekspressen|Otto Busses Vej|Rubinsteinsvej|Vestre Kirkegårds Allé|Østre Teglkaj|Stubmøllevej|Knud Lavards Gade|Teglholmen|Sluseholmen (Havnebus)|Sjælør st|Langgade st|Valby st|Ålholm Plads|Toftegårds Plads/Apoteket|Valby st./Lyshøjgårdsvej|Toftegårds Plads|Ellebjerg st./Folehaven|Langgade st. (bus)|Tofteg.Allé/Vigersl. Allé|Valby st./På Broen|Valby Langg./Sdr Fasanvej|Vigerslev Kirke|Folehaven/Vigerslevvej|Bjerregårdsvej|Gl.Køge Landev. Kollegiet|Carl Jacobsens Vej|Peter Bangs Vej/Ålekistev|Roskildev/Peter Bangs Vej|Vestre Kirkegård Nord|Trekronerg/C.Jacobsens V.|Centerparken|Vigerslevvej/Urtehaven|Gladbovej|Lykkebovej/Vigerslevvej|Vigerslev Alle st. (bus)|Retortvej/Folehaven|Stakhaven|Vigerslev Allé Skole|Hornemanns Vænge|Danhaven|Sjælør Boulevard 167-181|Åhaven|Blommehaven|Fengersvej/Vigerslev Allé|Gåsebæksvej|Gerdasgade|Gl. Jernbanevej|Grønttorvet|Hansstedvej|Landlystvej|Maribovej|Nakskovvej|Peder Hjorts Vej|Søndre Allé|Tingstedet|Valby Langg/Vigerslev Vej|Vigerslevv/Vigerslev Allé|Hestehaven/Folehaven|HF Stien|Centerparken Øst|Kirsebærhaven/Vigerslevv.|Skellet|Nøddehaven|Kirstinedalsvej|Danshøj st|Ålholm st. (bus)|Ålholm st|Ny Ellebjerg st|Vigerslev Alle st|Ny Ellebjerg Station|Valby mod Kastrup (fjernbus)|Valby mod Odden (fjernbus)|Valby st. (Hovedindgangen)|Frugthaven|Kirsebærhavens Plejehjem|Ramsingsvej|Solgavehjemmet|Spinderiet|Gl. Jernbanevej|Glostrup st|Plovmarksvej|Erhvervsv/Ejby Industriv.|Ejbytoften|Ejby Sommerby|Ejby Smedevej|Fabriksparken, Formervang|Hvissingevej|Glostrup Hospital (bus)|Herstedøsterv/Hovedvejen|Glostrup st. (bus)|Naverland, Farverland|Psykiatrisk Center Glostrup|Fabriksparken, Smedeland|Nordre Ringvej|Ejby Industriv/Ndr.Ringv.|Slotsherrensvej|Fabriksparken|Mellemtoftevej|Glostrup st., Glosemosevej|Gl. Landevej/Smedeland|Naverland/Smedeland|Skolevej|Hanevadsbro|Formervangen/Naverland|Herstedøstervej/Naverland|Langagervej|Tranemosevej|Borgm Munks Allé|Jyllingevej|Kindebjergvej|Leddet|Hvissingehus|Søen|Sportsvej/Hovedvejen|Springholm|Stadionvej|Stationsparken|Byparken|Digevangsvej|Elmehusene|Gl. Landevej|Glostrup Centret|Ny Vestergård|Paul Bergsøes Vej|Pilehusene|Rugmarksvej|Søndervangsskolen|Ejbylund|Trippendalscentret|Farverland|Teknisk Skole|Stenager|Poppelstien|Rødkælkevej|Sortevej|Glostrup Svømmehal|Søndervangsvej|Stenager Omsorgscenter|Nordvangskole|Glostruphallen|Herstedøstervej|Vestergårdsvej/Sportsvej|Glostrup Bibliotek (bus)|Sydvestvej|Ved Brandstationen|Nørre Alle|Præstehusene|Smedeland|Naverland|Brøndbyøster st|Roskildevej/Korsdalsvej|Åskellet|Brøndby Haveby afd. 1|Pensionatet Hulegården|Hesselager|Brøndbyøster st. (bus)|Kirkebjerg Torv|Brøndby Rådhus|Kornmarksvej|Brøndbyvej/Vallensbækvej|Avedøre Havnev/Park Allé|Søndre Ringvej/Park Allé|Vallensbækvej|Knudslundvej|Brøndby Haveby|Abildager/Vallensbækvej|Nyager/Abildager|Nyager|Brøndbyvesterv./Park Allé|Gildhøj Centret|Midtager/Park Allé|Sognevej/Brøndbyvestervej|Vallensbæk Torvev/Park Al|Bygaden|Industrisvinget|Park Allé/Industrivej|Søndre Ringv./Sydgårdsvej|Brøndbyøster Boulevard|Engager|Gammelager|Grønnedammen|Højstens Boulevard|Kærdammen|Langengvej|Ringerlodden|Skelmarksvej|Tranehaven/Park Allé|Voldgaden|Banemarksvej 40|Brøndby Hallen|Brøndbyøster Torv|DBU Allé|Rebæk Søpark|Ved Lindelund|HF Rosen 5. afdeling|Horsedammen/Højstens Blvd|Tjørnehøjskolen|Brøndbyøster Kirke|Åparken|Brøndby Haveby afd. 3|Marinegården|Kornmarksvej/Banemarksvej|Sydgårdsvej|Nygårds Plads|Brøndbyøster st., Nygårds Plads|Hvidovre st|Rødovre st|Damhustorvet|Erhvervsvej/Islevdalvej|Hvidovre st. (bus)|Islevdalv/Hvidsværmervej|Rødovre st. (bus)|Jyllingevej/Tårnvej|Krondalvej/Islevdalvej|Rødovre Centrum|Rødovrevej/Jyllingevej|Slotsherrensv/Islevdalvej|Tårnvej/Slotsherrensvej|Islev, Viemosevej|Roskildevej/Tårnvej|Fortvej/Tårnvej|Tæbyvej/Tårnvej|Rødager Alle|Damhustorvet/Hvidovrevej|Espelunden|Krondalvej|Randrupvej/Hvidovrevej|Randrupvej|Randrupvej/Roskildevej|HF Islegård|Marielundvej/Ndr Ringvej|Islevgård Allé|Rådmand Billes Vej|Hvidsværmervej|Jyllingevej/Islevdalvej|HF Dano|Grøndalslund Kirke|Egegårdsvej|Valhøjs Allé/Tårnvej|Lillekær|Milestedet|Maglekær|Ørbygård|Slotsherrensv./Rødovrevej|Birkmosevej|Roskildev/Brandholms Allé|Islevbrovej|Madumvej|Fjeldhammervej|Gunnekær|Horsevænget|Islev Torv|Islevholm|Koldbyvej|Præstebakken|Rødovre Kirke|Veronikavej|Rødovrehallen|Hendriksholm Kirke|Højrisvej|Schweizerdalsvej|Slotsherrens Have|Sylvestervej|Tæbyvej|Vestbadet|Fortvej|Guldsmedevej|Rødovre Rådhus/Tårnvej|Tårnvej|Rødovregård Museum|Engskrænten|Slotherrensvej|Islevbadet|Brunevang, Butikstorv|Albertslund st|Albertslundvej|Galgebakken/Gl. Landevej|Albertslund st. (bus)|Vegavænget|Tranehusene|Kastanjens Kvarter|Risby|Herstedøster Skole|Kildegården/Stenmosevej|Nordmarkscentret|Rønne Allé/Roskildevej|Hyldespjældet, Storetorv|Blommegården|Roskildevej/Vridsløsevej|Bispehusene|Damgården|Egelundsskolen|Fængselsvej|Hjulets Kvarter|Holsbjergvej|Hvedens Kvarter|Kløverens Kvarter|Nøglens Kvarter|Orchidevej|Sti til Herstedhøje|Teglmosevej|Tinghusbakkegård|Tingsletten|Vridsløselille Skole|Ydergårdsvej|Albertslund Gymnasium|Falkehusene|Godthåbsstien|Liljens Kvarter|Toftekærhallen|Bibliotekstorvet|Galgebakken|Hyldespjældet|Kastanie Allé|Roholmparken|Roholmsvej/Stenmosevej|Vridsløsestræde|Roholmsvej|Borgager|Degnehusene|Jydekrogen|Vallensbæk By, Toftevej|Bækrenden|Bygaden/Møllestens Ager|Park Allé|Vallensbæk Torvevej/Brøndbyvej|Vallensbækvej/-torvevej|Idræts Allé|Skolestien|Lundbækvej|Nørrestien|Pilestien|Vallensbæk Kirke|Firkløverparken|Korsagergård|Vallensbækvej|Høje Taastrup st|Taastrup st|City 2/Blekinge Boulevard|Blåkildegård|City 2|Taastrup st. (bus)|Helgeshøj Allé/Skat|Ikea|Herringløsevej|Klovtofteparken|Nørreby Torv|Høje Taastrup st. (bus)|Sønderby Torv|Saven|Sognevangen|Sengeløse Kirke/Landsbyg.|Taastrup Idrætshaller|Teknologisk Institut|Vesterby Torv|Vridsløsemagle|Erik Husfeldts Vej|Skåne Blvd/Bohus Blvd|Gadehaveskolen|Helgeshøj Allé/Mårkærvej|Helgeshøj Allé|Klovtofte Krydset|Ole Rømers Høj|Søndertoften/Sydskellet|Birkevej/Cathrinebergvej|Borgerskolen|Teknologisk Institut, Øst|Køgevej/Hveen Boulevard|Taastrup Nykirke/Køgevej|Gregersensvej|Sydskellet|Østerby Torv|Agrovej|Dronningeholmen|Engbrinken|Gøngestien|Grønhøjskolen|Hørsvinget|Husby Allé|Husmandsvej|Industribakken|Klovtoftegård|Kuldyssen|Leen|Mårkærstien|Marievej/Parkvej|Ole Rømers Vej 7-9|Parkskolen|Henriksdal|Sengeløsehallen|Slettetoften|Taastrup Valbyv/Vejtoften|Taastrupgårdsvej Midt|Espens Vænge|Høje Taastrup Gade|Høje Taastrup Rådhus|Hakkemosevej|Nyhøj Idrætspark|Rødhøjgårdsvej|Selsmoseskolen|Store Vejleå|Gasværksvej/Køgevej|Budstikken|Hveen Boulevard|Rønnevang Kirke|Rugkærgårdsvej|Sengeløse Skole|Taastrup Stationscenter|Taastrup Stationscenter Nord|Sengeløse Plejehjem|Østerparken|Gadehavegård/Sylen|Gadehavegård/Murskeen|Taastrup Nykirke|Ludvig Hegners Allé|Øksen|Gasværksvej|Halland Boulevard|Bohus Boulevard|Letland Allé|Hyldevangen|Tørvevej|Vink - Taastruphave|Vink - Ludvig Heglers Allé/ Ibsensvej|Vink - Taastrup Kulturcenter|Vesterparken|Fredensvej|Landsbygaden|Frøbjerget|Frøgård Allé|Frøhaven|Potentilvej|Brunellevej|Nordsjællands Postcenter|Håndværkerbakken|Høje Taastrup Boulevard|Kroppedal|Sønderby|Stien til EUC|Ishøj st|Baldersbækstien|Broenge|Centerstien|Vildtbanestien|Ishøj st. (bus)|Ishøjstien|Lundestien|Ishøj Skole|Ishøj Stationsvej|Litauen Allé|Skyttestien/Ishøj Søvej|Solhøjvej|Torsbo|Broenge/Vejleåvej|Vejlebrovej|Strandparkstien|Fyrrelunden|Mosegårdsstien|Industridalen|Tåstrup Valbyvej|Ellekilde Skole|Bryggergård|Friggasvej|Industriskellet|Kærbo|Kirkebjerggård|Bredebjergvej|Industrigrenen|Pedersborgvej|Tangloppen|Torslunde By|Torslunde Kirke|Vejlebroskolen|Allévej|Bredekærs Vænge|Granlunden|Industribuen|Pilemøllevej|Strandgårdskolen|Vejleåvej/Ishøj Bygade|Arken|Estland Allé (Midt)|Vandrerhjem|Ishøj Bycenter|Strandvangen|Ishøj Idræts- og Fritidscenter|Åparken|Vejleåparken Lokalcenter|Hedehusene st|Baldersbo|Hedehusene st. (bus)|Hedehusene Skole|Marbjerg|Fløng, Soderupvej|Reerslev Skole|Gammel Søvej|Soderup|Baldersbuen";

		// new station names
		String stations = "Fr.sund gymnasium|Frederikssund Torv|Græse Bakkeby|Fr.sundsvej/Havelse Mølle|Frederikssund st. (bus)|Oppe Sundby Skole|Sigerslevøster|Sigerslevvester|Sti til Fr.sund Hospital|Store Rørbæk|Nordsjællands Hospital - Fr.sund/Kapellet|Sti til Frederikssund Hospital|Kildebakken/Højvang|Ellekær|Rosenvænget/Færgevej|Skyllebakkegade|Højskolevej|Agervej|Byvej/Højvang|Byvej/Linderupvej|Drosselvej/Frejasvej|Dyrlægegårds Allé|Elsenbakken|Græse Skolevej|Lundekærgård|Lille Hofvej|Skelvej|Snostrup|Englystvej|Baneledet|Bassegrav|Bruhnsvej|Fællesvej|Gartnervænget|Klinten|Løgismose|Linderupvej|Morbærvænget|Søndervang|Strølillevej|Strandgårds Allé|Sundbyvej|Tokkehøjgård|Kalvøvej|Borgervænget|Falck Station|Falkenborgvej|Golfbanen|Omkørselsvejen/Kocksvej|Lerager|Pedersholm|Frederikssund Posthus|Sagavej|Stagetornsvej|Varmedalsvej|Bellisvej|Fr.sundsvej/St. Rørbækvej|Kastanie Allé/Roskildevej|Fr.borgvej/Ådalsvej|Omkørselsvej|Byvej/Elsenbakken|Gl. Slangerupvej|Daginstitutionen Rørskov|Bakkekammen, Græse Bakkeby|Græse Bakkeby skole|Græse Bakkeby, Byvej|Hjorthøjvej|Sundbylille|Dalby|Dalby Huse|Gerlev|Solsortevej|Bakkesvinget|Kulhuse Havn|Kyndby|Vink - Kyndbyværket|Landerslev|Lyngerup|Kignæskrogen|Vink - Orebjerg Allé|Over Dråby Kirke|Over Dråby Strand|Skoven Kirke|Jægerspris Slot|Tørslev|Dalby Husevej|Holmegårdsv/Landerslevvej|Rugtoften|Jægerspris, JAS|Over Dråby|Møllegårdsskolen|Møllehegnet|Neder Dråby/Vængetvej|Færgegård|Birkemosevej|Smede Bakken|Strandbovej|Bag Skovens Brugs|Barakvejen|Beckersvej|Egelundsvej/Skovnæsvej|Hornsved (Købmand)|Kyndby Huse|Solbakkeskolen|Jægersprislejren|Vink - Hovleddet|Nyhuse|Vestervangsvej|Vink - Seksgårde/Kulhusevej|Barakvejen/Kulhusvej|Højvangen/Barakvejen|Kulhus Tværvej|Egelyvej|Rosenbakken|Fiskervej|Jægerspris, Dyrnæsvej|Præstegårdsvej|skolerne Jægersprie|Ølstykke st|Gl. Toftegård st|Ølstykke Kirke|Frodebjergvej/Lyshøjvej|Gl. Ølstykke/Fr.sundsvej|Rørsangervej|Ølstykke st. (bus)|Roarsvej|Svalehøjvej|Svanholm Vænge|Tangbjerg|Vandmanden|Svestrup|Skyhøj|Astersvej/Violvej|Lærkevej/Udlejrevej|Kildegårdsvej|Københavnsvej|Søholmvej|Solsikkevej|Ring Nord/Ny Toftegårdsv.|Løven|Ny Toftegårdsv./Østervej|Stengårdsskolen|Svestrupvej|Tusindfrydvej|Krebsen|Normannervej|Skjoldsvej|Udlejregård|Vølundsvej|Fyrrevej/Ørnebjergvej|Fiskene|Lupinvej|Vermundsvej|Fyrkatvej|Kærgårdsvej|Gl. Toftegård st|Bækkegårdsskolen|Hans Erik Nielsens Vej|Udlejre Kirke|Stenløse st|Krogholmvej/Fr.sundsvej|Hyldegårdsvej|Ganløse Skole|Knardrup Bygade|Fr.fredegodsv/Krogholmvej|Stenløse st. (bus)|Slagslunde Bygade|Stenløse/Frederikssundsv.|Mosevej/Hvidehøjvej|Galgebakken/Måløvvej|Farumvej 127|Fluebjergvej|Tranemosevej/Hesselvej|Valmuevej/Stenløsevej|Bogøgårdsvej|Farumvej|Ganløseparken|Helsevej|Hesselvangen|Kalkgården|Kalveholmvej|Langåsen|Langebæk Gård|Langtoften|Rolandsgård|Stenløse Privatskole|Toppevadvej|Undinevej|Asserhøj|Ganløse Kirke|Halkærvej|Hyrdeleddet|Ringbakken|Nyvangs Allé|Slagslunde Forsamlingshus|Rosenvænget/Søsumvej|Gulnarevej|Veksø st|Poppelvang|Østrup Trafikplads|Knudsbjergvej|Veksø st. (bus)|Søsum|Veksø/Frederikssundsvej|Veksø Kirke|Løjesøvej|Agervej/Frederikssundsvej|Skovvangsvej|Fugleøjevej|Stenpilstræde|Thorkildgårdsvej|Bjellekjærvej|Korshøjgårdsvej|Svinemosevej|Holmegård|Rønne Havn (Bornholm)|Muleby (Bornholm)|Nyker (Bornholm)|Aarsballe (Bornholm)|Snellemark centrum (Bornholm)|Rønne Rådhus (Bornholm)|Knudsker (Bornholm)|Peterskolen (Bornholm)|Sandemandsvej (Bornholm)|Hallebakken/Erlandsgårdsv. (Bornholm)|Nordskovvej/Almegårdsk. (Bornholm)|Gartnervangen (Bornholm)|Borgm.Niels.Vej - Nord (Bornholm)|Østergade/Voldgade (Bornholm)|Harbovej (Bornholm)|Ydunsvej/Asavej (Bornholm)|Lillevangsv. v/ Sandemandsv. (Bornholm)|Centralbiblioteket (Bornholm)|Sagavej/Aakirkebyv. (Bornholm)|Centralsygehuset (Bornholm)|Sagavej/Søndergårds Allé (Bornholm)|Fredensborgvej (Bornholm)|Strandvejen/Campingplads (Bornholm)|Munch Petersens Vej v/ Kirken  (Bornholm)|Center Lunden (Bornholm)|Vestermarie (Bornholm)|Lufthavnen (Bornholm)|Arnager (Bornholm)|Sorthat v/ Sahara (Bornholm)|Sorthat v/ Havvej (Bornholm)|Nyker Vest (Bornholm)|Nyker  v/ skolen (Bornholm)|Aarsballev./Kongstubbev. (Bornholm)|Aarsballe By/Sdr.Lyngvej (Bornholm)|Åbyvej/Kirkebyvej (Bornholm)|Ågårdsv/Karlsgårdsv. (Bornholm)|Sdr.Lyngv/Kongensmark (Bornholm)|Sdr.Lyngv/Dyndegårdsv. (Bornholm)|Rønne - St.Torvegade - Kystparken (Bornholm)|Rønne - Nørregade v. Tværstræde (Bornholm)|Rønne - Møllegade (Bornholm)|Rønne - St.Torvegade 57 (Bornholm)|Rønne - Østergade 44a (Bornholm)|Rønne - Ll.Torv (Bornholm)|Borgm.Nielsens Vej - Syd (Bornholm)|Knudsker-Uddannelsescentret (Bornholm)|Haslevej - rute 25 (Bornholm)|Rønne Nord (Bornholm)|Rønne - Haslevej  v/ Ndr.Ringvej  (Bornholm)|Rønne - Haslevej  v/ Efterskolen (Bornholm)|Rønne - Haslevej 138b (Bornholm)|Rønne - Haslevej v/ Almegårdsv. (Bornholm)|Ringeby Bro (Bornholm)|Rønne - Blykobbevej Nord (Bornholm)|Rønne - Blykobbevej Midt (Bornholm)|Rønne - Blykobbevej Syd (Bornholm)|Rønne - Landemærket (Bornholm)|Rønne - Sagavej v/ Fredensborgv. (Bornholm)|Rønne - Fredensborgv. v/ Kabbelejeløkk, (Bornholm)|Rønne - Sdr.Alle v/ kirkegård (Bornholm)|Rønne v/ Ryttergården (Bornholm)|Rønne v/ Fredensborg (Bornholm)|Rønne v/ Lersøvej (Bornholm)|Rønne - Strandvejen 47/49 (Bornholm)|Åvangsskolen/Smallesund (Bornholm)|Søndermarksskolen (Bornholm)|Rønne - Aakirkebyvej v/ Sigynsvej (Bornholm)|Rønne v/ Vibegård (Bornholm)|Lillevangsv./Kanegårdsv. (Bornholm)|Stavelund (Bornholm)|Kærby (Bornholm)|Erlandsgårdsvej (Bornholm)|Aakirkebyvej/Østre skole (Bornholm)|Rønne - Aakirkebyvej 40/42 (Bornholm)|Svanekevej/Østre Skole (Bornholm)|Aakirkebyvej/DR Bornholm (Bornholm)|Almindingvej/Østkraft (Bornholm)|Rønne - Almindingsvej v/ Brovangen (Bornholm)|Rønne - Sagavej v/ Paradisvej (Bornholm)|Rønne - Helsevej (Bornholm)|Skovgårdsv. v/ Pindeløkkegd. (Bornholm)|Skovgårdsv. v/ Lærkene (Bornholm)|Rønnevej/Skrædderbakkevej (Bornholm)|Sdr.Landev. v/ Stampenv. (Bornholm)|Sdr.Landev./Skrædderbakkev. (Bornholm)|Skrædderbakkevej (Bornholm)|Vellensbyv./Blemmelyngv. (Bornholm)|Bolsterbjerg (Bornholm)|Segenvej/Kongsstubbevej (Bornholm)|Almindingen - Kongemindev. (Bornholm)|Rønne Havn (færge)|Almindingen -Koldek.hus (Bornholm)|Aakirkeby - Terminalen (Bornholm)|Pedersker (Bornholm)|Nylars (Bornholm)|Lobbæk (Bornholm)|Egeby (Bornholm)|Sdr. Landevej v. Limensgaden (Bornholm)|Østre Sømarken (Bornholm)|Aakirkeby - centrum (Bornholm)|Koldekildehus (Bornholm)|Almindingen - Travbanen (Bornholm)|Aakirkeby - Amindingsvej v/ TV2 (Bornholm)|Pederskervej v/ skolen (Bornholm)|Pedersker Hovedgade 5 (Bornholm)|Sdr.Landevej/Sandvejen (Bornholm)|Nexøvej/Kratgårdsvej (Bornholm)|Sdr.Landev./Sosevej (Bornholm)|Sdr.landevej/Vasegårdsvej (Bornholm)|Strandvejen/Sandvejen (Bornholm)|Aakirkeby - Nybyvej (Bornholm)|Aakirkeby - Kuleborgvej (Bornholm)|Grammegårdsvej/Rundløkkevej (Bornholm)|Fejleregård (Bornholm)|Sosevej (Bornholm)|Tvillingegårde (Bornholm)|Springbakkevej (Bornholm)|Almindingsvej/Ekkodalsvej (Bornholm)|Ølenevejen/Højlyngsvej (Bornholm)|Bygaden/Ll.Myregårdsvejen (Bornholm)|Produktionshøjskolen (Bornholm)|Bygaden/Sigtebrovej (Bornholm)|Sigtemøllev./Fårebyvej (Bornholm)|Pedersker skole (Bornholm)|Sdr.Landev./Grammegårdsv. (Bornholm)|Pederskervej v/ kirken (Bornholm)|Rosengården (Bornholm)|Vasagård (Bornholm)|Grammegårdsv/Rundløkkev. (Bornholm)|Hegnedevejen (Bornholm)|Ølenevej (Bornholm)|Sandvejen (Bornholm)|Strandvejen/Baunevej (Bornholm)|Almindingen - Lilleborg (Bornholm)|Nyvestcentret (Bornholm)|Snogebæk v. Turistvej (Bornholm)|Balka (Bornholm)|Nexø rtb. (Bornholm)|Bodils Kirke (Bornholm)|Dueodde (Bornholm)|Snogebæk v/ Smedevej (Bornholm)|Snogebæk - Havnevej (Bornholm)|Nexø - Ferskesø (Bornholm)|Nexø v/ Falckvej (Bornholm)|Nexø v/ varmeværket (Bornholm)|Nexø - Ndr. Strandvej (Bornholm)|Paradisv/Harilds Løkkev. (Bornholm)|Nexø Hallen/Nørremøllec. (Bornholm)|Nexø - Nørremøllecentret (Bornholm)|Bodilsker Skole (Bornholm)|Rønnevej v. Lyngvejen (Bornholm)|Dalevejen/Plantagevej (Bornholm)|Slamrebjergv. v.Klintebyv. (Bornholm)|Rønnevej v. Slamrebjergv. (Bornholm)|Rønnevej v/ Skimlevejen (Bornholm)|Langedebyv./Skimlevejen (Bornholm)|Ø.Slamrevej/Paradisvej (Bornholm)|Kannikkegårdsv./Birkevej (Bornholm)|Stenseby (Bornholm)|Holsmyrevejen (Bornholm)|Pouls Kirke (Bornholm)|Poulskervej v/ Skolevejen (Bornholm)|Strandmarksv./Udegårdsv. (Bornholm)|Strandmarksv./Fyrvej (Bornholm)|Poulskerhallen (Bornholm)|Pilemølle (Bornholm)|Brandsgårdsv./Pederskerv. (Bornholm)|Nexø Camping (BAT)|Nexø v/Nexøhuset (BAT)|Ibskervej/Nørremøllevej (BAT)|Snogebæk/v Smedevej (BAT)|Bølshavn (Bornholm)|Listed havn (Bornholm)|Svaneke (Bornholm)|Aarsdale (Bornholm)|Paradisb. v/Oksemyrvej (Bornholm)|Brændesgårdshaven (Bornholm)|Bølshavn v/ Sommervej (Bornholm)|Lyrsbyvej/Louisenlundv. (Bornholm)|Svanekevej/Byfogedv. (Bornholm)|Listed Nord (Bornholm)|Listed Syd (Bornholm)|Svaneke v/ Mariegården (Bornholm)|Svaneke v/ fodboldbanen (Bornholm)|Svaneke v/ skolen (Bornholm)|Aarsdale - Gaden (Bornholm)|Ibskervej/Paradisbakkevej (Bornholm)|Ibsker (Bornholm)|Højevej/Brændsgårdhavesv. (Bornholm)|Østermarievej/Højevej (Bornholm)|Svaneke Skole (Bornholm)|Lindholmsv.v/ Degnebrov. (Bornholm)|Svaneke - Gryneparken (Bornholm)|Svaneke - Møllebakken (Bornholm)|Svaneke v.Madvigs Minde (BAT)|Østermarie (Bornholm)|Østermarie v/ skolen (Bornholm)|Østermarie - Godthåbsvej (Bornholm)|Østermarie v/ kirken (Bornholm)|Svanekevej/Lindesvej (Bornholm)|Randkløvevej/Kirkebyvej (Bornholm)|Randkløvevej v/ Hvide Hus (Bornholm)|Østermarie skole (Bornholm)|Lindetsvej v/ Svanekevej (Bornholm)|Aspevej/Dalslundevej (Bornholm)|Svanekevej/Bølshavnvej (Bornholm)|Kirkebyvej v/ Randkløvev. (Bornholm)|Almindingensvej/Åløsevej (Bornholm)|Dyndevej/Havrehøjvej (Bornholm)|Ølenevej/Plantagevej (Bornholm)|Ølenevej/Myregårdsvej (Bornholm)|Østerlars (Bornholm)|Østerlars Rundkirke (Bornholm)|Gudhjem - museet (Bornholm)|Gudhjem Havn (Bornholm)|Rø (Bornholm)|Helligdommen - Kunstmuseet (Bornholm)|Melsted (Bornholm)|Gudhjem - kirkegård (Bornholm)|Saltuna v/Kjeldsebyvej (Bornholm)|Gudhjem færgehavn (Bornholm)|Middelaldercentret (Bornholm)|Gudhjem øvre Busstation (Bornholm)|Østerlars v/ skolen (Bornholm)|Østerlars v/ friskolen (Bornholm)|Østerlars - Gudhjemvej|Gudhjem v/ plejehejm (Bornholm)|Gudhjem - Nørresand (Bornholm)|Døndalen (Bornholm)|Sdr.Strandvej v/ Bådsted (Bornholm)|Stammershalle (Bornholm)|Melstedvej v/ Kobbeåen (Bornholm)|Nordlandets Rideklub (Bornholm)|Røbro (Bornholm)|Klemenskervej v/ Ridehal (Bornholm)|Rø v/ kirken (Bornholm)|Østerlars skole (Bornholm)|Gamlevældev/Smedevej (Bornholm)|Stavsdalv./Oksholmv. (Bornholm)|Studebyv/Risenholmsv. (Bornholm)|Oksholmsvej v/Åsedamsvej (Bornholm)|Humledal (Bornholm)|Helligdomsv/Klemenskerv. (Bornholm)|Allinge - Lindeplads (Bornholm)|Sandvig - gl. station (Bornholm)|Olsker (Bornholm)|Hammershus (Bornholm)|Tejn Havn (Bornholm)|Sandkås (Bornholm)|Allinge Havn (Bornholm)|Borrelyngvej v. Lyngholt (Bornholm)|Allinge - Tårnhuset (Bornholm)|Allinge - Havnegade (Bornholm)|Sandvig v/ Hotel Sandvig (Bornholm)|Allinge - Kongeskærskolen (Bornholm)|Sandvig - Langebjergvej (Bornholm)|Allinge - Rønnevej (Bornholm)|Hammerhavn (Bornholm)|Sdr.Strandvej v/ Kåsevej (Bornholm)|Tejn / Møllegade (Bornholm)|Tejn Syd (Bornholm)|Tejn v/ Klippestien (Bornholm)|Tejn v/ Smedebakken (Bornholm)|Tejn / Smedeløkken (Bornholm)|Smedeløkken Syd|Tejn Nord (Bornholm)|Sandkåsbakken (Bornholm)|Allinge - Tejnvej/Grønnedalsvej (Bornholm)|Allinge - Tejnvej/Genvej (Bornholm)|Kongeskærskolen (Bornholm)|Allinge - Borrelyngsvej (Bornholm)|Allinge (fjernbus)|Klemensker (Bornholm)|Klemensker Vest (Bornholm)|Klemensker v/ Bjørnemøllev. (Bornholm)|Klemensker v/ Aagårdsv. (Bornholm)|Klemensker skole (Bornholm)|Splitsgårdsv./Sdr.Lyngv. (Bornholm)|Vedby (Bornholm)|Bedegadev/Petersborgv. (Bornholm)|N.Lyngv./Krashavev. (Bornholm)|Lyngholt (Bornholm)|Hasle - Torvet (Bornholm)|Jons Kapel - Landevejen (Bornholm)|Vang havn (Bornholm)|Rutsker (Bornholm)|Vang v/  Udsigten  (Bornholm)|Fælledvej v/ Levkavej (Bornholm)|Fælledvej v/ Tofte (Bornholm)|Fælledvej v/ Hvide Hus (Bornholm)|Fælledvej v/ Glasværksvej (Bornholm)|Fælledvej v/ Campanella (Bornholm)|Fælledvej v/ H.C. Sigerstedsvej (Bornholm)|Hasle Fælled (Bornholm)|Hasle - Damløkkevej (Bornholm)|Hasle Nord (Bornholm)|Hasle - Toftelunden (Bornholm)|Borrelyngsv. v/ Helligpederv. (Bornholm)|Vang overfor nr. 75 (Bornholm)|Rutsker v/ Fuglesangsvej (Bornholm)|Svalhøj (Bornholm)|Borrelyngvej v. Teknisk saml.|Trekroner st|Roskilde st|Ågerup, Omsorgscentret|Øm|Vestvejen/Lindenborgvej|Lammegade|Alfarvejen|Rådhuset|Bakkekammen|Højvangsvej/Tingvej|Herringløse Sportsplads|Boserup|Roskilde Svømmehal|Vestergade|Roskilde st./Ny Terminal|Roskilde Idrætscenter|Darup|Æblehaven|Holbækvej/Fælledvej|Thomas Bredsdorffs Allé|Svendborgvej|Gevninge/Lindenborgvej|Glimvej|Gundsølille|Gulddyssevej|Østervangsskolen|Gyvelvej/Bymarken|Korskær|Himmelevgård|Himmelev, Egelund|Sønderlundsvej|Møllehusvej|Strandparken|Hvedstrup|Katedralskolen|Kirkerup|Kongebakken|Dronning Margrethes Vej|Kornerup|Kamstrup, Gadekæret|Margrethehåbsvej|Lindenborg|Trekroner st. (bus)|Handelsskolen, Maglelunden|Navervej|Neergårdsparken|Oldvejsparken|Osted Friskole|Osted Kro|Præstemarksvej/Østbyvej|Bjergmarken|Roskilde st., Sygehuset|Risø|Rådmandshaven|Sankt Jørgens Skole|Østhospitalet|Vesthospitalet|Schmeltz Plads|Roskilde st./Gl. Terminal|Roskilde st./Hersegade|Roskilde Ring|Stændertorvet|Store Valby|Søstien/Kongemarksvej|Svogerslev|Tågerup|Terrasserne|Tune Kirke|Veddelev|Vor Frue Kirke|Vindinge, Ved Kirken|Lotusvej|Pilevej/Tune Parkvej|Tune Center|Lillevangsvej|Guldblommevej|Margretheskolen|Stenlandsvej|Mannerupvej|Teglværksvej/Kamstrupvej|Kamstrup/Kamstrupvej|Roskilde Hjemmet|Mørbjergvænget|Hedeland/Tunevej|Jernbanegade/Hersegade|Klosterengen/Rundkørslen|Klosterengen/Ternevej|Møllehusvej/Vestergade|Koldekildevej|Vink - Koldekildevej/St.Valbyvej|Neergårdsvej/Holbækvej|Solvænget/Ternevej|Himmelev, Store Valbyvej|Tjærebyvej/Tjæreby|Kornerup/Gl. Landevej|Skovdalen|Gundsølillevej|Åbakken|Helligkorsvej/Fælledvej|Ostedhallen|Skovbovængets Allé|Duevej|Henrik Nielsens Vej|Gammelgårdsvej/Hovedvejen|Hejnstrupvej|Vink - Hejnstrup|Himmelev Skole|Margretheskolen/Sognevej|Niels Frederiksens Vej|Søtoften|Rørmosen|Hedegade|Stamvej|Rønnebærparken|Ringparken|Østre Ringvej/Østbyvej|Østre Ringvej|Østre Ringvej/Vindingevej|Absalons Skole|Bakkedraget|Roskilde Handelsskole|Høkerstræde|Jonstrupvej/Hovedvejen|Klostermarksskolen|Byageren/Klosterengen|Ternehaven|Møllevej (Øst)|Nørregade|Roskilde st./Hestetorvet|Gartnervang|Sankt Peders Stræde|Himmelev Sognevej|Hejrevej|Svogerslev Kirke|Lykkegårdsvej|Ærøvej|Baunehøjv/Veddelev Bygade|Bausager|Østhospitalet Nord|Bistrup Vænge|Boserup Skov|Brovej|Dyrskueplads|Fælledgården|Fenrisvej|Guldborgvej|Nyvej/Sognevej|Gundsømagle, Rådhuset|Undervisningscenter|Herringløse Bygade|Holbækvej|Låddenhøj, Vestergade|Hyldestien|Jordbrugsskolen|Kirkebjerg|Knud den Stores Vej|Kongemarken|Lavringemose|Vink - Lejre Å|Lindegården|Lufthavnsvej|Prins Buris Vej|Sankt Clara Vej|Trægården|Vor Frue Hovedgade|Assensvej|Bygvænget|Dalager/Gevninge Bygade|Dronning Emmas Vej|Dronning Sofies Vej|Eleonoravej|Langvad, Glim Skole|Gyldenkærnevej|Hedeboparken|Ro´s Torv|Himmelev Gymnasium|Kumlehusvej|Lyngageren|Nødager|Nordhøjen|Svogerslev, Søbredden|Sankt Ibs Vej|Spraglehøjvej|Strandhøjen|Roskilde Postcenter|Maglehøjen|Vikingeskibshallen|Asylgade|Kong Valdemars Vej|Hestehavevej|Åbrinken|Åvej/Sognevej|Bøgevej/Tune Parkvej|Baldersvej|Fiskergårdsvej|Hedevænget|Ved Holbækmotorvejen|Himmelev, Kragholm|Hyldekærparken|Kornerups Vænge|Låddenhøj/Holbækvej|Motelvej|Rørsangervej/Klosterengen|Søndergade/Tune Bygade|Strandengen|Sct. Jørgensbjerg Plejehjem|Vestre Kirkevej|Ahornvej|Hedegade/Møllehusvej|Snæversti|Fyrrevej|Troldehøj|Låddenhøj|Wiemosen|Langebjerg/Marbjergvej|RUC (Syd)|Skt. Jørgens Vej|Gundsømagle, Rosentorvet|Gundsømagle Kirke|Flyveskolen|Roskilde Lufthavn|Vindinge, Rosenvang|Smedegårdsparken|Svaleøvej|Vink - Lille Valby|Astersvej|Tune Mark|Himmelev Center|Bernadottegården|Valhalvej|Margrethegården|Lillehjemmet|Bymarken|Østre Ringvej/Gyvelvej|Asterscentret|Isafjordvej, Munksøgaard|Linkøpingvej|Mindstrupgård|Roskilde Sygehus|Roskilde Sygehus Hovedindgang|Rådmandshaven/Møllehusvej|Neergårdsvej|Højvangsvænge|Trekroner Skole|Margrethehåb|Lysalleen|Genoptræningscenter|Byvejen|Kløverdalen|Kristiansminde Plejecenter|Henrik Nielsens Vej|Firkløvervej|Hyrdehøj Bygade|Hyrdehøj|Ledreborg Allé|Roskilde mod Odden (fjernbus)|Roskilde mod Kbh. (fjernbus)|RUC (Øst)|Storemøllevænget|Ejboparken|Østerled (Lejre)|Teknisk skole, Maglelunden|Hospice Sjælland|Tønsbjergvej|Lindegårdsparken, Boserupvej|Katinge Bygade|Katingevej|Strandvej|Herslev Kirke|Trællerup|Gevninge Kirke|Birkholmvej|Alfarvejen|Åholmvej|Søndertoften|Stentoften|Langetoften|Osted kirke|Baunehøj|Kongemarksvej|Risen|Landbrugsskolen Roskilde|Jernbaneviadukten|Nordmarksvej/Osvej|Jyllingecentret|Værebrovej/Osvej|Kløvervej/Rådalsvej|Rådalsvej/Værebrovej|Agerskellet|Møllevej/Møllehaven|Råmosevej|Egesvinget|Lønager|Paulsvej|Rådalsvej|Møllehavegård|Læhegnet|Lindegård|Markskellet|Stærevej|Østby|Bonderupvej|Ferslev/ved gadekæret|Manderup|Ny Krogstrup|Onsved|Vink - Røgerup|Sønderby|Selsø Kirke|Vink - Skibby Industrivej|Møllehøj/Skuldelev skole|Vejleby|Vellerup Kirke|Venslev|Kildevej/Østbyvej|Elmevej|Skibbyhøj|Skibby, Ved Skuldelevvej|Damgårdsvej/Hovedgaden|Bonderup Old|Krogstrup Kirke|Onsved Huse|Skibby Kirke|Egevej|Teglværksvej/Østbyvej|Vibevej/Marbækhallen|Birkebækvej|Hammer Bakke|Skibby Rådhus|Skuldelev Kirke|Kærvej/Selsøvej|Vink - Lindegårdsparken|Skuldelev skole|Marbækskolen|Ferslev skole|Hanghøj|Svanholm Allé|Torpevej|Vink - Ungdomsskolen/Nyvej|Ferslev, præstegården|Onsved|Englerup|Kirke Sonnerup/Englerupv.|Korsvejen|Kirke Såby Kirke|Langtvedkrydset|Rye|Torkilstrup|Vintre Mølle|Borrevejle Skov|Magnolievej/Egevej|Borrevejle|Munkholmv./Hornsherredvej|Lindevangsvej|Egegård/Landevejen|Vink - Frihedsgård|Bomgård|Dyvelslyst|Vink - Acacievej/Landevejen|Vink - Rye Kirke|Omøvej|Vink - Egtvedgård|Vink - Skovvejen|Vink - Torkilstrupvej (Dyrlægen)|Møllegårdsvej|Vink - Ordrup|Vink - Ryegaard|Skolevang|Spurvevej|Bjergskovvej|Vink - Lyndbyparken|Uglestrup|Vink - Havrevænget|Vink - Bygmarken|Vink - Englerup, Skovstien|Vink - Skovstien|Vink - Kirke Sonnerup Skole|Vink - Dyvelslyst|Thorsvej|Ejby, Buen|Ejby, Dyssemosen|Ejby Strand, Åvej|Gershøj, Ved Kirken|Kyndeløse|Lille Karleby|Lyndby, Lyndbyparken|Nørre Hyllinge|Sæby (Sjælland)|Store Karleby|Sydmarksvej/Møllehøj|Vink - Hvidemosegård|Krabbesholm/Elverdamsvej|Vink - Krabbesholm|Kyndeløse, Kyndeløsevej|Biltris|Biltris, Elverdamsvej|Bigårdsvej|Hyllingeparken|Kirke Hyllinge|Sæby Kirke|Kyndeløse, Sydmarksvej|Sydmarksvej/Elverdamsvej|Tværvej/Karlebyvej|Vink - Vandværket/Hyrdehuset|Nørrevang|Vibevej/Gershøjvej|Vink - Åhusene|Vink - Brydevang|Vink - Egholm Hovedgård|Sæby Gershøj Skole|Karleby Forsamlingshus|Vink - Kirke Hyllinge Kirke|Lyndby Kirke|Vink - Polakhuset|Vink - Skræppenkærgård|Vink - Trehøje|Christian Hansens Vej|Gershøjgårdsvej|Sæbyparken|Strandbakken|Egevej/Ejby Strandvej|Møllehøjvej|Brydevang/Hornsherredvej|Rytterlodden|Vink - Vestervang/Karlebyvej|Stærevænget|Ilsøvej|Ejby/Bramsnæsvigskolen|Knudsvej|Kornbakken|Vink - Drejerbakken|Vink - Jenslev|Vink - Lyndby, Ved Købmanden|Vink - Åhøjgård|Kirke Hyllinge Skole|Bramsnæsvighallen|Karlebyvej|Ammershøj|Vink - Røde Smedie|Vink - Langtved|Vink - Sæby Gershøj Skole|Hornsherredvej|Hornsherredvej|Hornsherredvej|Ringsted st|Ringstedvej|Lundegårdsvej|Skovbo Efterskole|Slimmingevej/Ringstedvej|Slimminge, Egelundsvej|Bringstrupvej|Langemosevej|Sjællandsgade/Hovmarksvej|Maglemosevej/Mosevænget|Ørslevvestervej|Kyringe|Sigersted|Langebækgård|Skellerødvej|Høm Møllevej|Høm vendeplads|Vetterslev / Næstvedvej|Vetterslev (Bygaden)|Vetterslev-Høm skole|Haslevvej/Bragesvej|Haslevvej|Galtehus|Haslevvej|Præstebrovej|Farendløsevej|Havemarksvej|Ørslev|Kværkebyvej|Adamshøjvej|Kærehave|Bondebjergvej|Bakkegårdsvej|Nordbjergvej|Bringstrup|Holbækvej|Eventyrvej|Estrup|Holbækvej";
		

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