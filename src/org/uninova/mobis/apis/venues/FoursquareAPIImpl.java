package org.uninova.mobis.apis.venues;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.MobisVenue;
import org.uninova.mobis.pojos.MobisVenueCategory;
import org.uninova.mobis.utils.HTTPUtils;
import org.uninova.mobis.utils.HTTPUtilsImpl;

public class FoursquareAPIImpl implements FoursquareAPI {

	public FoursquareAPIImpl() {}
	
	public ArrayList<MobisVenue> getFSVenues(String categories, String radius, String position, String limit) {
		String foursquareVenuesSearch = "" ;
		String clientId = "", clientSecret = "", result = "" ;
		JSONObject obj, responseObj, thisObj, venue, location ; 
		JSONArray groups, itemsArray, categoriesArray, venues ;
		HTTPUtils httpUtils = new HTTPUtilsImpl() ;
//		ArrayList<MobisVenue> venuesList = new ArrayList<>() ;
		ArrayList<MobisVenue> venuesList = new ArrayList<>() ;
		ArrayList<MobisVenueCategory> categoryList = null ;
		
		clientId = StringConstants.FOURSQUARE_CLIENT_ID ;
		clientSecret = StringConstants.FOURSQUARE_CLIENT_SECRET ;
		
		if (categories!= null && !categories.equals("")) {
			foursquareVenuesSearch = StringConstants.FOURSQUARE_URL + "ll=" + position + "&intent=browse&radius=" + radius + "&categoryId=" + categories + "&limit=" + limit + "&client_id=" + clientId +"&client_secret=" + clientSecret + "&v=" + getTodaysDate();
		}
		else {
			foursquareVenuesSearch = StringConstants.FOURSQUARE_URL + "ll=" + position + "&intent=browse&radius=" + radius + "&limit=" + limit + "&client_id=" + clientId +"&client_secret=" + clientSecret + "&v=" + getTodaysDate();
		}
		try {
			result = httpUtils.requestURLConnection(foursquareVenuesSearch) ;
			if (!result.contains("error:")) {
				obj = JSONObject.fromObject(result) ;
				
				responseObj = obj.getJSONObject("response") ;					
				groups = responseObj.getJSONArray("venues") ;
				
				thisObj = groups.getJSONObject(0) ;
				if (thisObj.containsKey("items"))
					itemsArray = thisObj.getJSONArray("items") ;
				if (!groups.isEmpty()) {
					for (int j = 0; j < groups.size(); j++) {
						venue = groups.getJSONObject(j) ;
						location = venue.getJSONObject("location") ;
						if (location.getInt("distance") < Integer.parseInt(radius)) {
							MobisVenue v = new MobisVenue() ;
							v.setProviderId(venue.getString("id")) ;
							v.setName(venue.getString("name")) ;
							String address = (location.has("address") ? (location.getString("address") + ", ") : "") + (location.has("city") ? (location.getString("city") + ", ") : "") + location.getString("country") ;
							v.setAddress(address) ;
							v.setLat(location.getString("lat")) ;
							v.setLng(location.getString("lng")) ;
							
							categoriesArray = venue.getJSONArray("categories") ;
							
							categoryList = new ArrayList<>() ;
							
							for (int i = 0; i < categoriesArray.size(); i++) {
								JSONObject categoryObj = categoriesArray.getJSONObject(i) ;
								MobisVenueCategory cat = new MobisVenueCategory() ;
								cat.setId(categoryObj.getString("id")) ;
								cat.setName(categoryObj.getString("name")) ;
								cat.setIcon(categoryObj.getString("icon")) ;
								categoryList.add(cat) ;
							}
							v.setCategories(categoryList) ;
							
							venuesList.add(v) ;
						}
					}
					return venuesList ;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	public ArrayList<MobisVenue> getFSVenuesByBoundingBox(String categories, String ne, String sw, String limit) {
		String foursquareVenuesSearch = "" ;
		String clientId = "", clientSecret = "", result = "" ;
		JSONObject obj, responseObj, thisObj, venue, location ; 
		JSONArray groups, itemsArray, categoriesArray ;
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		ArrayList<MobisVenue> venuesList = new ArrayList<>() ;
		ArrayList<MobisVenueCategory> categoryList = null ;
		
		clientId = StringConstants.FOURSQUARE_CLIENT_ID ;
		clientSecret = StringConstants.FOURSQUARE_CLIENT_SECRET ;
		
		if (categories!= null && !categories.equals("")) {
			foursquareVenuesSearch = StringConstants.FOURSQUARE_URL + "ne=" + ne + "&sw=" + sw + "&intent=browse&categoryId=" + categories + "&limit=" + limit + "&client_id=" + clientId +"&client_secret=" + clientSecret ;
		}
		else {
			foursquareVenuesSearch = StringConstants.FOURSQUARE_URL + "ne=" + ne + "&sw=" + sw + "&intent=browse&limit=" + limit + "&client_id=" + clientId +"&client_secret=" + clientSecret ;
		}
		try {
			result = httpUtils.requestURLConnection(foursquareVenuesSearch) ;
			if (!result.contains("error:")) {
				obj = JSONObject.fromObject(result) ;
				
				responseObj = obj.getJSONObject("response") ;
				groups = responseObj.getJSONArray("groups") ;
				
				itemsArray = JSONArray.fromObject("[]") ;
				thisObj = groups.getJSONObject(0) ;
				if (thisObj.containsKey("items"))
					itemsArray = thisObj.getJSONArray("items") ;
				if (!itemsArray.isEmpty()) {
					for (int j = 0; j < itemsArray.size(); j++) {
						venue = itemsArray.getJSONObject(j) ;
						location = venue.getJSONObject("location") ;
						
						MobisVenue v = new MobisVenue() ;
						v.setProviderId(venue.getString("id")) ;
						v.setName(venue.getString("name")) ;
						String address = (location.has("address") ? (location.getString("address") + ", ") : "") + (location.has("city") ? (location.getString("city") + ", ") : "") + location.getString("country") ;
						v.setAddress(address) ;
						v.setLat(location.getString("lat")) ;
						v.setLng(location.getString("lng")) ;
						
						categoriesArray = venue.getJSONArray("categories") ;
						
						categoryList = new ArrayList<>() ;
						
						for (int i = 0; i < categoriesArray.size(); i++) {
							JSONObject categoryObj = categoriesArray.getJSONObject(i) ;
							MobisVenueCategory cat = new MobisVenueCategory() ;
							cat.setId(categoryObj.getString("id")) ;
							cat.setName(categoryObj.getString("name")) ;
							cat.setIcon(categoryObj.getString("icon")) ;
							categoryList.add(cat) ;
						}
						v.setCategories(categoryList) ;
						
						venuesList.add(v) ;
					}
					return venuesList ;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;

	}
	
    private static String getTodaysDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		return dateFormat.format(date);
    }
}
