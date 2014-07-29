package org.uninova.mobis.pojos;

/**
 * POJO MobisVenueCategory
 * @author PAF@UNINOVA
 */
public class MobisVenueCategory {
	private String provider ;
	private String id ;		// Category ID 
	private String name ;	// Category Name
	private String icon ;	// Category Icon
	
	public MobisVenueCategory() {}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
}
