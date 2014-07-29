package org.uninova.mobis.apis.geolocation;

import java.io.IOException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.uninova.mobis.apis.osm.OpenStreetMapsAPI;
import org.uninova.mobis.apis.osm.OpenStreetMapsAPIImpl;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.NominatimAddress;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.OSMNode;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;
import org.uninova.mobis.utils.HTTPUtils;
import org.uninova.mobis.utils.HTTPUtilsImpl;

/**
 * NominatimGeolocationImpl
 * @author PAF
 */
public class NominatimGeolocationImpl implements NominatimGeolocation {
	
	/**
	 * NominatimGeolocationImpl Class Constructor
	 */
	public NominatimGeolocationImpl() {}
	
	/**
	 * Reverse geocoding generates an address from a latitude and longitude. The optional zoom parameter specifies the level of detail required in terms of something suitable for an openlayers zoom level.
	 * @param lat				The latitude to reverse geocode
	 * @param lng				The longitude to reverse geocode
	 * @param osmType			The type of OpenStreetMap feature corresponding to the coordinate ('N' = Node; 'W' = Way; 'R' = Relation)
	 * @param osmId				The OpenStreetMap ID for the node/way/relation
	 * @param zoom				The level of detail required in terms of something suitable for an openlayers zoom level.
	 * @param addressDetails	<code>true</code> if address details are to be retrieved; <code>false</code> otherwise 
	 * @return					The reverse geocoded address, as a {org.uninova.mobis.pojos.NominatimAddress} object
	 */
	public NominatimAddress reverseGeocoding(Double lat, Double lng, String osmType, String osmId, int zoom, boolean addressDetails) {
		String url = StringConstants.NOMINATIM_URL + "reverse?format=json&accept-language=en" ;
		HTTPUtils httpUtils = new HTTPUtilsImpl() ;
		
		if (lat != null && lng != null && lat > 0.0 && lng > 0.0) {
			url += "&lat=" + lat + "&lon=" + lng ;
		}
		
		if (osmType != null && !osmType.equals("") && osmId != null && !osmId.equals("")) {
			url += "&osm_type=" + osmType + "&osm_id=" + osmId ;
		}
		
		if (zoom >= 0 && zoom <= 18) {
			url += "&zoom=" + zoom ;
		}
		if (addressDetails) {
			url += "&addressdetails=1" ;
		}
		else {
			url += "&addressdetails=0" ;
		}
		
		try {
			String result = httpUtils.requestURLConnection(url) ;
			JSONObject place = JSONObject.fromObject(result) ;
			JSONObject address = place.getJSONObject("address") ;
			
			NominatimAddress addr = new NominatimAddress() ;
			
			if (address.has("house_number")) {
				addr.setHouseNumber(address.getString("house_number")) ;
			}
			if (address.has("road")) {
				addr.setRoad(address.getString("road")) ;
			}
			if (address.has("suburb")) {
				addr.setSuburb(address.getString("suburb")) ;
			}
			if (address.has("village")) {
				addr.setVillage(address.getString("village")) ;
			}
			if (address.has("town")) {
				addr.setTown(address.getString("town")) ;
			}
			if (address.has("city")) {
				addr.setCity(address.getString("city")) ;
			}
			if (address.has("county")) {
				addr.setCounty(address.getString("county")) ;
			}
			if (address.has("state_district")) {
				addr.setStateDistrict(address.getString("state_district")) ;
			}
			if (address.has("state")) {
				addr.setState(address.getString("address")) ;
			}
			if (address.has("postcode")) {
				addr.setPostCode(address.getString("postcode")) ;
			}
			if (address.has("country")) {
				addr.setCountry(address.getString("country")) ;
			}
			if (address.has("country_code")) {
				addr.setCoutryCode(address.getString("country_code")) ;
			}
			if (address.has("continent")) {
				addr.setContinent(address.getString("continent")) ;
			}
			
			return addr ;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}

	/**
	 * Nominatim indexes named (or numbered) features with the OSM data set and a subset of other unnamed features (pubs, hotels, churches, etc)
	 * @param querySearch		<code>true</code> if it is a query search; <code>false</code> if the search uses the inputs 'street', 'country', etc.
	 * @param query				Query string to search for
	 * @param street			Street name to search for
	 * @param county			County to search for
	 * @param state				State to search for
	 * @param country			Country to search for
	 * @param postcode			Postal code to search for
	 * @param countryCodes		Country codes in which to search
	 * @param addressDetails	<code>true</code> if address details are to be retrieved; <code>false</code> otherwise
	 * @param excludedPlaceIds	Place IDs to exclude from search
	 * @param limit				Maximum limit of results
	 * @return					A list of coordinates that match the given address or query, as a {@link java.util.ArrayList} object
	 */
	public ArrayList<Coordinate> geocoding(boolean querySearch, String query, String street, String city, String county, String state, String country, String postcode, ArrayList<String> countryCodes, boolean addressDetails, ArrayList<String> excludedPlaceIds, int limit) {
		String url = StringConstants.NOMINATIM_URL + "search?format=json&accept-language=en" ;
		HTTPUtils httpUtils = new HTTPUtilsImpl() ;
		
		if (querySearch) {
			if (query != null && !query.equals("")) {
				url += "&q=" + query ;
			} 
		}
		else {
			if (street != null && !street.equals("")) {
				url += "&street=" + street ;
			}
			if (city != null && !city.equals("")) {
				url += "&city=" + city ;
			}
			if (county != null && !county.equals("")) {
				url += "&county=" + county ;
			}
			if (state != null && !state.equals("")) {
				url += "&state=" + state ;
			}
			if (country != null && !country.equals("")) {
				url += "&country=" + country ;
			}
			if (postcode != null && !postcode.equals("")) {
				url += "&postalcode=" + postcode ;
			}
		}
		
		if (countryCodes != null && countryCodes.size() >= 1) {
			url += "&countrycodes=" ;
			for (String code : countryCodes) {
				url += code + "," ;
			}
			url = url.substring(0, url.lastIndexOf(",")) ;
		}
		else {
			url += "&countrycodes=se,sl,gr" ;
		}
		
		if (addressDetails) {
			url += "&addressdetails=1" ;
		}
		else {
			url += "&addressdetails=0" ;
		}
		
		if (excludedPlaceIds != null && excludedPlaceIds.size() >= 1) {
			url += "&exclude_place_ids=" ;
			for (String id : excludedPlaceIds) {
				url += id + "," ;
			}
			url = url.substring(0, url.lastIndexOf(",")) ;
		}
		
		if (limit > 0) {
			url += "&limit=" + limit ;
		}
		
		
		try {
			String result = httpUtils.requestURLConnection(url);
			JSONArray places = JSONArray.fromObject(result) ;
			ArrayList<Coordinate> coords = new ArrayList<Coordinate>() ;
			Coordinate coord ;
			for (int i = 0; i < places.size(); i++) {
				JSONObject place = places.getJSONObject(i) ;
				coord = new Coordinate() ;
				coord.setLat(place.getDouble("lat")) ;
				coord.setLng(place.getDouble("lon")) ;
				coords.add(coord) ;
			}
			return coords ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	public Coordinate getNearestNodeCoordinate(Coordinate coord) {
		String url = StringConstants.NOMINATIM_URL + "reverse?format=json&accept-language=en&lat=" + coord.getLat() + "&lng=" + coord.getLng() + "&addressdetails=1" ;
		HTTPUtils httpUtils = new HTTPUtilsImpl() ;
		GISUtils gisUtils = new GISUtilsImpl() ;
		OpenStreetMapsAPI osm = new OpenStreetMapsAPIImpl() ;
		String result, osmType, osmId ;
		Double lat, lng ;
		ArrayList<OSMNode> nodes ;
		JSONObject place ;
		OSMNode node, selected = null ;
		double shortest = 0.0, current = 0.0 ;
		
		try {
			result = httpUtils.requestURLConnection(url) ;
			place = JSONObject.fromObject(result) ;
			if (place.has("osm_type")) {
				osmType = place.getString("osm_type") ;
				if (osmType.equals("node")) {
					lat = place.getDouble("lat") ;
					lng = place.getDouble("lon") ;
					return new Coordinate(lat, lng) ;
				}
				else if (osmType.equals("way")) {
					osmId = place.getString("osm_id") ;
					lat = place.getDouble("lat") ;
					lng = place.getDouble("lon") ;
					nodes = osm.getNodesInWay(osmId) ;
					for (int i = 0; i < nodes.size(); i++) {
						node = nodes.get(i) ;
						current = gisUtils.distFrom(coord, node.getCoord()) ;
						if ((i > 0 && current < shortest) || i == 0) {
							shortest = current ;
							selected = node ; 
						}
					}
					return selected.getCoord() ;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coord ;
	}
	
	public long getNearestNodeId(Coordinate coord) {
		String url = StringConstants.NOMINATIM_URL + "reverse?format=json&accept-language=en&lat=" + coord.getLat() + "&lon=" + coord.getLng() + "&addressdetails=1" ;
		HTTPUtils httpUtils = new HTTPUtilsImpl() ;
		GISUtils gisUtils = new GISUtilsImpl() ;
		OpenStreetMapsAPI osm = new OpenStreetMapsAPIImpl() ;
		String result, osmType, osmId ;
		ArrayList<OSMNode> nodes ;
		JSONObject place ;
		OSMNode node, selected = null ;
		double shortest = 0.0, current = 0.0 ;
		
		try {
			result = httpUtils.requestURLConnection(url) ;
			place = JSONObject.fromObject(result) ;
			if (place.has("osm_type")) {
				osmType = place.getString("osm_type") ;
				if (osmType.equals("node")) {
					return place.getLong("osm_id") ;
				}
				else if (osmType.equals("way")) {
					osmId = place.getString("osm_id") ;
					nodes = osm.getNodesInWay(osmId) ;
					for (int i = 0; i < nodes.size(); i++) {
						node = nodes.get(i) ;
						current = gisUtils.distFrom(coord, node.getCoord()) ;
						if ((i > 0 && current < shortest) || i == 0) {
							shortest = current ;
							selected = node ; 
						}
					}
					return selected.getOsmId() ;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
}
