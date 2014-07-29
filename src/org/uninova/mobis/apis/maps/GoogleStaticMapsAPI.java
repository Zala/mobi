package org.uninova.mobis.apis.maps;

import java.util.ArrayList;

import org.uninova.mobis.pojos.GoogleMapMarker;
import org.uninova.mobis.pojos.GoogleMapPath;

public interface GoogleStaticMapsAPI {

	public String getStaticMapURL(String center, int zoom, String size, int scale, String format, String mapType, boolean sensor) ;
	
	public String addMarkersToMapURL(String baseURL, ArrayList<GoogleMapMarker> markers) ;
	
	public String addPathsToMapURL(String baseURL, ArrayList<GoogleMapPath> paths) ;
	
}
