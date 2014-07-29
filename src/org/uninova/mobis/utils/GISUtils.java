package org.uninova.mobis.utils;

import java.util.ArrayList;
import java.util.List;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisSegment;

/**
 * Interface GISUtils
 * @author PAF@UNINOVA
 */
public interface GISUtils {

	/**
	 * Decodes a input {@link java.util.String} in the Encoded Polyline Algorithm Format into a list of {@link org.uninova.mobis.pojos.Coordinate} objects
	 * @param encoded	The {@link java.util.String} in Encoded Polyline Algorithm Format to be decoded
	 * @return			An {@link java.util.ArrayList} object with coordinates for the encoded line
	 * @see				http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 */
	public List<Coordinate> decodePoly(String encoded) ;
	
	/**
	 * Calculates the distance (in meters) from one coordinate <code>coord1</code> to another coordinate <code>coord2</code>.
	 * 
	 * @param coord1	the values for the first coordinate
	 * @param coord2	the values for the second coordinate
	 * @return		the distance (in meters) between the two input coordinates
	 */
	public double distFrom(Coordinate coord1, Coordinate coord2) ;
	
	/**
	 * Sorts an {@link java.util.ArrayList} object with coordinates depending on their distances to the <code>origin</code> coordinate
	 * @param sorted	The sorted {@link java.util.ArrayList} object
	 * @param coords	The input {@link java.util.ArrayList} object
	 * @param origin	The {@link org.uninova.mobis.pojos.Coordinate} object to compare distances to
	 * @return			The sorted {@link java.util.ArrayList} object
	 */
	public ArrayList<Coordinate> sortByDistance(ArrayList<Coordinate> sorted, ArrayList<Coordinate> coords, Coordinate origin) ;
	
	/**
	 * Checks if a coordinate is located within Sweden
	 * @param lat	The latitude value for the coordinate
	 * @param lng	The longitude value for the coordinate
	 * @return		<code>true</code> if the coordinate is located within Sweden, <code>false</code> otherwise
	 */
	public boolean locatedInSweden(double lat, double lng) ;
	
	/**
	 * Checks if a coordinate is located within Slovenia
	 * @param lat	The latitude value for the coordinate
	 * @param lng	The longitude value for the coordinate
	 * @return		<code>true</code> if the coordinate is located within Slovenia, <code>false</code> otherwise
	 */
	public boolean locatedInSlovenia(double lat, double lng) ;
	
	/**
	 * Checks if a coordinate is located within Greece
	 * @param lat	The latitude value for the coordinate
	 * @param lng	The longitude value for the coordinate
	 * @return		<code>true</code> if the coordinate is located within Greece, <code>false</code> otherwise
	 */
	public boolean locatedInGreece(double lat, double lng) ;
	
	/**
	 * Transforms a {@link java.lang.String} object (coordinate or address) into a {@link org.uninova.mobis.pojos.Coordinate} object. If the 
	 * {@link java.lang.String} object is a physical address, a geocoding tool (Nominatim) is used to geocode the address into a GPS coordinate. 
	 * @param str	The {@link java.lang.String} object, corresponding to a GPS WSG84 coordinate or to a physical (postal) address
	 * @return		The {@link org.uninova.mobis.pojos.Coordinate} object resulting from the {@link java.lang.String} object transformation
	 */
	public Coordinate getCoordinateFromString(String str) ;
	
	/**
	 * Gets the coordinate for a specific place, given the street name and the city name
	 * @param street 	The name of the street
	 * @param city		The name of the city
	 * @return			The coordinate for the specific place
	 */
	public Coordinate getCoordinateFromAddress(String street, String city) ;
	
	/**
	 * Returns the coordinates of a bounding box, given the central coordinate and the radius of the box
	 * @param coord		The central coordinate for the bounding box
	 * @param radius	The radius for the bounding box in meters
	 * @return			An {@link java.util.ArrayList} object with the coordinates for the South-West and North-East positions of the bounding box
	 */
	public ArrayList<Coordinate> getBoundingBoxFromCoordinate(Coordinate coord, double radius) ;
	
	/**
	 * Checks if the input {@link java.lang.String} object corresponds to a numeric value
	 * @param str	The input {@link java.lang.String} object
	 * @return		<code>true</code> if the {@link java.lang.String} object is a numeric value; <code>false</code> otherwise
	 */
	public boolean isNumeric(String str) ;
	
	public String getStaticMapForSegment(MobisSegment segment, String color, int weight, String size, int scale, String format, String mapType, boolean sensor) ;
}
