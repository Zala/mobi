package org.uninova.mobis.apis.geolocation;

import java.util.ArrayList;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.NominatimAddress;

public interface NominatimGeolocation {

	public NominatimAddress reverseGeocoding(Double lat, Double lng, String osmType, String osmId, int zoom, boolean addressDetails) ;
	
	public ArrayList<Coordinate> geocoding(boolean querySearch, String query, String street, String city, String county, String state, String country, String postcode, ArrayList<String> countryCodes, boolean addressDetails, ArrayList<String> excludedPlaceIds, int limit) ;
	
	public Coordinate getNearestNodeCoordinate(Coordinate coord) ;
	
	public long getNearestNodeId(Coordinate coord) ;
}
