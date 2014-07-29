package org.uninova.mobis.utils;

import java.util.ArrayList;
import java.util.List;

import org.uninova.mobis.apis.geolocation.NominatimGeolocation;
import org.uninova.mobis.apis.geolocation.NominatimGeolocationImpl;
import org.uninova.mobis.apis.maps.GoogleStaticMapsAPI;
import org.uninova.mobis.apis.maps.GoogleStaticMapsAPIImpl;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.GoogleMapPath;
import org.uninova.mobis.pojos.MobisNode;
import org.uninova.mobis.pojos.MobisSegment;

/**
 * Class GISUtilsImpl
 * @author PAF@UNINOVA
 */
public class GISUtilsImpl implements GISUtils {
	
	/* MAX & MIN LATS & LNGS FOR SWEDEN*/
	private final double svMaxLat = 69.063141 ;
	private final double svMaxLng = 24.16707 ;
	private final double svMinLat = 55.001099 ;
	private final double svMinLng = 11.10694 ;
	
	/* MAX & MIN LATS & LNGS FOR SLOVENIA*/
	private final double slMaxLat = 46.872479 ;
	private final double slMaxLng = 16.60924 ;
	private final double slMinLat = 45.425598 ;
	private final double slMinLng = 13.38305 ;
	
	/* MAX & MIN LATS & LNGS FOR GREECE*/
	private final double grMaxLat = 41.744431 ;
	private final double grMaxLng = 28.24222 ;
	private final double grMinLat = 34.929989 ;
	private final double grMinLng = 19.627501 ;
	
	/**
	 * 	GISUtilsImpl class constructor
	 */
	public GISUtilsImpl() {}
	
	/**
	 * Decodes a input {@link java.util.String} in the Encoded Polyline Algorithm Format into a list of {@link org.uninova.mobis.pojos.Coordinate} objects
	 * @param encoded	The {@link java.util.String} in Encoded Polyline Algorithm Format to be decoded
	 * @return			An {@link java.util.ArrayList} object with coordinates for the encoded line
	 * @see				http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 */
	public List<Coordinate> decodePoly(String encoded) {

		List<Coordinate> poly = new ArrayList<Coordinate>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			Coordinate p = new Coordinate((lat / 1E5), (lng / 1E5));
			poly.add(p);
		}

		return poly;
	}
	
	/**
	 * Calculates the distance (in meters) from one coordinate <code>coord1</code> to another coordinate <code>coord2</code>.
	 * 
	 * @param coord1	the values for the first coordinate
	 * @param coord2	the values for the second coordinate
	 * @return		the distance (in meters) between the two input coordinates
	 */
	public double distFrom(Coordinate coord1, Coordinate coord2) {
	    double earthRadius = 3958.75;
	    double lat1 = coord1.getLat(), lat2 = coord2.getLat(), lng1 = coord1.getLng(), lng2 = coord2.getLng() ;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return dist * meterConversion ;
	}
	
	/**
	 * Sorts an {@link java.util.ArrayList} object with coordinates depending on their distances to the <code>origin</code> coordinate
	 * @param sorted	The sorted {@link java.util.ArrayList} object
	 * @param coords	The input {@link java.util.ArrayList} object
	 * @param origin	The {@link org.uninova.mobis.pojos.Coordinate} object to compare distances to
	 * @return			The sorted {@link java.util.ArrayList} object
	 */
	public ArrayList<Coordinate> sortByDistance(ArrayList<Coordinate> sorted, ArrayList<Coordinate> coords, Coordinate origin) {
		if (coords.size() > 0) {
			GISUtils gisUtils = new GISUtilsImpl() ;
			Coordinate best = null ;
			double bestDistance = Double.POSITIVE_INFINITY ;
			for (int i = 0; i < coords.size(); i++) {
				double dist = gisUtils.distFrom(origin, coords.get(i)) ;
				if (dist < bestDistance) {
					bestDistance = dist ;
					best = coords.get(i) ;
				}
			}
			sorted.add(best) ;
			coords.remove(best) ;
			return sortByDistance(sorted, coords, best) ;
		}
		else {
			return sorted ;
		}
	}
	
	/**
	 * Checks if a coordinate is located within Sweden
	 * @param lat	The latitude value for the coordinate
	 * @param lng	The longitude value for the coordinate
	 * @return		<code>true</code> if the coordinate is located within Sweden, <code>false</code> otherwise
	 */
	public boolean locatedInSweden(double lat, double lng) {
		if (lat <= svMaxLat && lat >= svMinLat && lng <= svMaxLng && lng >= svMinLng) {
			return true ;
		}
		else return false ;
	}
	
	/**
	 * Checks if a coordinate is located within Slovenia
	 * @param lat	The latitude value for the coordinate
	 * @param lng	The longitude value for the coordinate
	 * @return		<code>true</code> if the coordinate is located within Slovenia, <code>false</code> otherwise
	 */
	public boolean locatedInSlovenia(double lat, double lng) {
		if (lat <= slMaxLat && lat >= slMinLat && lng <= slMaxLng && lng >= slMinLng) {
			return true ;
		}
		else return false ;
	}
	
	/**
	 * Checks if a coordinate is located within Greece
	 * @param lat	The latitude value for the coordinate
	 * @param lng	The longitude value for the coordinate
	 * @return		<code>true</code> if the coordinate is located within Greece, <code>false</code> otherwise
	 */
	public boolean locatedInGreece(double lat, double lng) {
		if (lat <= grMaxLat && lat >= grMinLat && lng <= grMaxLng && lng >= grMinLng) {
			return true ;
		}
		else return false ;
	}
	
	/**
	 * Transforms a {@link java.lang.String} object (coordinate or address) into a {@link org.uninova.mobis.pojos.Coordinate} object. If the 
	 * {@link java.lang.String} object is a physical address, a geocoding tool (Nominatim) is used to geocode the address into a GPS coordinate. 
	 * @param str	The {@link java.lang.String} object, corresponding to a GPS WSG84 coordinate or to a physical (postal) address
	 * @return		The {@link org.uninova.mobis.pojos.Coordinate} object resulting from the {@link java.lang.String} object transformation
	 */
	public Coordinate getCoordinateFromString(String str) {
		//SysUtilsImpl sysUtils = new SysUtilsImpl() ;
		NominatimGeolocation nominatimInterface = new NominatimGeolocationImpl() ;
		Coordinate coord = new Coordinate() ;
		if (str.contains(",") && str.contains(".")) {
			boolean latIsCoord = this.isNumeric(str.substring(0, str.indexOf(","))) ;
			boolean lngIsCoord = this.isNumeric(str.substring(str.indexOf(",") + 1)) ;
			if (latIsCoord && lngIsCoord) {
				coord.setLat(Double.valueOf(str.substring(0, str.indexOf(",")))) ;
				coord.setLng(Double.valueOf(str.substring(str.indexOf(",") + 1))) ;
			}
			else {
				ArrayList<Coordinate> coords = nominatimInterface.geocoding(true, str, "", "", "", "", "", "", null, false, null, 5) ;
				if ( coords != null && !coords.isEmpty()) {
					//TODO: For now, we choose the first coordinate. Later we will have to show alternatives to the user
					coord = coords.get(0) ;
					
					return coord ;
				}
				else return null ; 
			}
		} 
		else {
			ArrayList<Coordinate> coords = nominatimInterface.geocoding(true, str, "", "", "", "", "", "", null, false, null, 5) ;
			if ( coords != null && !coords.isEmpty()) {
				//TODO: For now, we choose the first coordinate. Later we will have to show alternatives to the user
				coord = coords.get(0) ;
				
				return coord ;
			}
			else return null ; 
		}
		return coord ;
	}
	
	/**
	 * Gets the coordinate for a specific place, given the street name and the city name
	 * @param street 	The name of the street
	 * @param city		The name of the city
	 * @return			The coordinate for the specific place
	 */
	public Coordinate getCoordinateFromAddress(String street, String city) {
		NominatimGeolocation nominatimInterface = new NominatimGeolocationImpl() ;
		Coordinate coord = new Coordinate() ;
		
		ArrayList<Coordinate> coords = nominatimInterface.geocoding(false, "", street, city, "", "", "", "", null, false, null, 5) ;
		if ( coords != null && !coords.isEmpty()) {
			//TODO: For now, we choose the first coordinate. Later we will have to show alternatives to the user
			coord = coords.get(0) ;
			
			return coord ;
		}
		else return null ; 
	}
	
	/**
	 * Returns the coordinates of a bounding box, given the central coordinate and the radius of the box
	 * @param coord		The central coordinate for the bounding box
	 * @param radius	The radius for the bounding box in meters
	 * @return			An {@link java.util.ArrayList} object with the coordinates for the South-West and North-East positions of the bounding box
	 */
	public ArrayList<Coordinate> getBoundingBoxFromCoordinate(Coordinate coord, double radius) {
		double earthRadius = 6371;
		//int meterConversion = 1609;
		ArrayList<Coordinate> bounding = new ArrayList<>() ;
		 
		double x1 = coord.getLng() - Math.toDegrees(radius/earthRadius/Math.cos(Math.toRadians(coord.getLat())));
		double x2 = coord.getLng() + Math.toDegrees(radius/earthRadius/Math.cos(Math.toRadians(coord.getLat())));
		double y1 = coord.getLat() + Math.toDegrees(radius/earthRadius);
		double y2 = coord.getLat() - Math.toDegrees(radius/earthRadius);
		
		bounding.add(new Coordinate(y1, x1)) ;
		bounding.add(new Coordinate(y2, x2)) ;
		
		return bounding ;
	}
	
	/**
	 * Checks if the input {@link java.lang.String} object corresponds to a numeric value
	 * @param str	The input {@link java.lang.String} object
	 * @return		<code>true</code> if the {@link java.lang.String} object is a numeric value; <code>false</code> otherwise
	 */
	public boolean isNumeric(String str) {  
		  try {  
			
			@SuppressWarnings("unused")
			Double d = Double.parseDouble(str);  
			  
		  } catch(NumberFormatException nfe) {  
			  return false;  
		  }  
		  return true;  
	}
	
	public String getStaticMapForSegment(MobisSegment segment, String color, int weight, String size, int scale, String format, String mapType, boolean sensor) {
		ArrayList<Coordinate> coords = new ArrayList<>() ;
		ArrayList<GoogleMapPath> paths = new ArrayList<>() ;
		GoogleStaticMapsAPI googleStaticMaps = new GoogleStaticMapsAPIImpl() ;
		GoogleMapPath path = new GoogleMapPath() ;
		String url = "" ;
		
		for (MobisNode node : segment.getNodes()) {
			coords.add(new Coordinate(node.getLat(),node.getLng())) ;
		}
		path.setColor(color) ;
		path.setWeight(weight) ;
		path.setCoords(coords) ;
		paths.add(path) ;
		
		url = googleStaticMaps.getStaticMapURL("", 0, size, scale, format, mapType, sensor) ;
		url = googleStaticMaps.addPathsToMapURL(url, paths) ;
		
		return url ;
	}
}

