package org.uninova.mobis.apis.venues;

import java.util.HashMap;

import org.uninova.mobis.pojos.Coordinate;

public interface FactualAPI {

	public String search(String opt, String filters, String requiredFields, boolean geoFilter, Coordinate coord, int radius, boolean rowCount, int limit, int offset, String query, String sort) ;
	
	public String match(String table, HashMap<String, String> matchParams) ;
	
	public String resolve(String table, HashMap<String, String> matchParams) ;
	
	public String geopulse(Coordinate coord, String requiredFields) ;
}
