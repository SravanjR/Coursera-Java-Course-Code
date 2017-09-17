package finalProjectUCSD1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */

//Create a Pop up for each city (when Clicked on) corresponding to the Earthquakes whose threat range includes the city
// Pop up displays the number of quakes, average magnitude and information about the most recent quake
//Pop up also displays information about the city clicked on, removing the need to hover over the city

public class EarthquakeCityMap extends PApplet {
	
	//Add variables for the information to be updated
	static int countquakes = 0;
	static double totalMag = 0.0;
	static EarthquakeMarker mostRecentQuake = null;
	static CityMarker clickedCity = null;
	static boolean trueCityClicked = false;
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(1200, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Microsoft.RoadProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    sortAndPrint(20);
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		//If a city was clicked on, the pop up for that city will be drawn
		if(trueCityClicked){
			drawQuakeStuff(countquakes, totalMag, mostRecentQuake, clickedCity);
		}
	}
	
	private static void swap(EarthquakeMarker[] quakes, int ind1, int ind2)
	{
		EarthquakeMarker temp = quakes[ind1];
		quakes[ind1] = quakes[ind2];
		quakes[ind2] = temp;
	}
	
	// TODO: Add the method:
	private void sortAndPrint(int numToPrint){
		EarthquakeMarker[] quakes = quakeMarkers.toArray(new EarthquakeMarker[quakeMarkers.size()]);
		for(int i = 0; i < quakes.length - 1; i++){
			int maxdex = i;
			for (int j = i + 1; j < quakes.length; j++){
                if (quakes[j].compareTo(quakes[maxdex]) > 0) {
                    maxdex = j;
                }
			}
			swap(quakes, maxdex, i);	
		}
		if(numToPrint > quakes.length){
			for(int k = 0; k<quakes.length; k++)
				println(quakes[k]);
		}
		else{
			for(int k = 0; k<numToPrint; k++)
				println(quakes[k]);
		}
		
	}
	// and then call that method from setUp
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			//Reset Information about Each quake per city to default 
			lastClicked = null;
			countquakes = 0;
			totalMag = 0.0;
			mostRecentQuake = null;
			clickedCity = null;
			trueCityClicked = false;
		}
		else if (lastClicked == null) 
		{
			checkEarthquakesForClick();
			if (lastClicked == null ) {
				checkCitiesForClick();
			}
		}
	}
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
					else{
						//the city has to be updated 
						clickedCity = (CityMarker)mhide;
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if (quakeMarker.getDistanceTo(marker.getLocation()) 
							> quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
					else{
						//collect information for drawQuakeStuff function
						countquakes++;
						totalMag += quakeMarker.getMagnitude();
						mostRecentQuake = determineRecency(quakeMarker, mostRecentQuake);
					}
				}
				trueCityClicked = true;
				return;
			}			
		}
		return;
	}
	
	//Draw Pop Up Window with given information
	private void drawQuakeStuff(int countquakes, double totalMag, EarthquakeMarker mostRecentQuake, 
			CityMarker x) {
		// TODO Auto-generated method stub
		float avgmag = 0;
		if(countquakes != 0)
			avgmag = (float) (totalMag/countquakes);
		fill(255, 250, 240);
		int xbase = 875;
		int ybase = 50;
		rect(xbase, ybase, 300, 225);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("City And Quake Info", xbase+75, ybase+25);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Number of Quakes: ", xbase+25, ybase+50);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text(countquakes, xbase + 140, ybase+50);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Average Magnitude: ", xbase+25, ybase+75);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text(avgmag, xbase+140, ybase+75);
		
		//To prevent a crash, check if mostRecentQuake is null or not
		if (mostRecentQuake != null){
			String recent = mostRecentQuake.toString();
			fill(0);
			textAlign(LEFT, CENTER);
			textSize(12);
			text("Most Recent Quake: ", xbase+25, ybase+100);
			
			fill(0);
			textAlign(LEFT, CENTER);
			textSize(12);
			text(mostRecentQuake.getAge(), xbase+145, ybase+100);

			fill(0);
			textAlign(LEFT, CENTER);
			textSize(12);
			text(recent, xbase+15, ybase+120);
		}
		else{
			fill(0);
			textAlign(LEFT, CENTER);
			textSize(12);
			text("Most Recent Quake: ", xbase+25, ybase+100);

			fill(0);
			textAlign(LEFT, CENTER);
			textSize(12);
			text("N/A", xbase+140, ybase+100);
		
		}
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("City Name: ", xbase+25, ybase+140);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text(x.getCity(), xbase + 100, ybase+140);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Population: ", xbase+25, ybase+160);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text(x.getPopulation() + " Million", xbase+100, ybase+160);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Country Name: ", xbase+25, ybase+180);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text(x.getCountry(), xbase + 120, ybase+180);
	}


	private EarthquakeMarker determineRecency(EarthquakeMarker x, EarthquakeMarker y) {
		// TODO Auto-generated method stub
		if(y == null){
			y = x;
		}
		else if(y.getAge() == "Past Week" && x.getAge() == "Past Day"){
			y = x;
		}
		else if(y.getAge() == "Past Day" && (String) x.getAge() == "Past Hour"){
			y = x;
		}
		else if(y.getAge() == "Past Week" && x.getAge() == "Past Hour"){
			y = x;
		}
		return y;
	}


	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
