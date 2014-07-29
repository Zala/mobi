package org.uninova.mobis.apis.venues;

import java.util.ArrayList;

import org.uninova.mobis.pojos.MobisVenue;

public interface FoursquareAPI {

	public ArrayList<MobisVenue> getFSVenues(String categories, String radius, String position, String limit) ;
	
	public ArrayList<MobisVenue> getFSVenuesByBoundingBox(String categories, String ne, String sw, String limit) ;
	
}
