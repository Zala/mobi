package org.uninova.mobis.pojos;

import java.util.ArrayList;

/**
 * POJO MobisVenue
 * @author PAF@UNINOVA
 */
public class MobisVenue {
	private String provider ;
	private String providerId ;
	private String name ;						// Venue name
	private String lat ;						// Venue latitude
	private String lng ;						// Venue longitude
	private String address ;					// Venue address
	private String description ;
	private ArrayList<MobisVenueCategory> categories ;	// Venue categories
	
	public MobisVenue() {}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<MobisVenueCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<MobisVenueCategory> categories) {
		this.categories = categories;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
