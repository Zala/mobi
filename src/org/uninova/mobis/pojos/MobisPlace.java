package org.uninova.mobis.pojos;

/**
 * POJO MobisPlace
 * @author PAF@UNINOVA
 */
public class MobisPlace {

	private long placeId = -1 ;
	private String provider = "" ;
	private String providerId = "" ;
	
	public MobisPlace() {}

	public long getPlaceId() {
		return placeId;
	}

	public void setPlaceId(long placeId) {
		this.placeId = placeId;
	}

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
}
