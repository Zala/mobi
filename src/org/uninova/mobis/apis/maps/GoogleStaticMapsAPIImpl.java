package org.uninova.mobis.apis.maps;

import java.util.ArrayList;

import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.GoogleMapMarker;
import org.uninova.mobis.pojos.GoogleMapPath;

public class GoogleStaticMapsAPIImpl implements GoogleStaticMapsAPI {


	public GoogleStaticMapsAPIImpl() {}
	
	public String getStaticMapURL(String center, int zoom, String size, int scale, String format, String mapType, boolean sensor) {
		String url = StringConstants.GOOGLE_STATIC_MAPS_URL ;
		if (center != null && !center.equals("")) {
			url += "center=" + center + "&zoom=" + zoom + "&" ;
		}
		
		if (size != null && !size.equals("")) {
			url += "size=" + size + "&" ;
		}
		else {
			url += "size=640x640&" ;
		}
		
		if (scale > 0) {
			url += "scale=" + scale + "&" ;
		}
		else {
			url += "scale=1&" ;
		}
		
		if (format != null && !format.equals("")) {
			url += "format=" + format + "&" ;
		}
		
		if (mapType != null && !mapType.equals("")) {
			url += "maptype=" + mapType + "&" ;
		}
		else {
			url += "maptype=hybrid&" ;
		}
		
		url += "sensor=" + sensor ;
		url += "&key=" + StringConstants.GOOGLE_API_KEY ;
		
		return url ;
	}
	
	public String addMarkersToMapURL(String baseURL, ArrayList<GoogleMapMarker> markers) {
		
		for (GoogleMapMarker marker : markers) {
			baseURL += "&markers=" ;
			if (marker.getColor() != null && !marker.getColor().equals("")) {
				baseURL += "color:" + marker.getColor() + "|" ;
			}
			if (marker.getSize() != null && !marker.getSize().equals("")) {
				baseURL += "size:" + marker.getSize() + "|" ;
			}
			if (marker.getLabel() != null && !marker.getLabel().equals("")) {
				baseURL += "label:" + marker.getLabel() + "|" ;
			}
			if (marker.getCoord() != null) {
				baseURL += marker.getCoord().getLat() + "," + marker.getCoord().getLng() ;
			}
			else {
				baseURL += marker.getAddress() ;
			}
		}
		
		return baseURL ;
	}
	
	public String addPathsToMapURL(String baseURL, ArrayList<GoogleMapPath> paths) {
		
		for (GoogleMapPath path : paths) {
			baseURL += "&paths=" ;
			if (path.getColor() != null && !path.getColor().equals("")) {
				baseURL += "color:" + path.getColor() + "|" ;
			}
			if (path.getWeight() >0) {
				baseURL += "weight:" + path.getWeight() + "|" ;
			}
			
			for (Coordinate coord : path.getCoords()) {
				baseURL += coord.getLat() + "," + coord.getLng() + "|" ;
			}
			
			baseURL = baseURL.substring(0, baseURL.lastIndexOf("|")) ;
		}
		
		return baseURL ;
	} 
}
