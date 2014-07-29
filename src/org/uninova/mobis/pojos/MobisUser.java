package org.uninova.mobis.pojos;

import java.util.ArrayList;

/**
 * POJO MobisUser
 * @author PAF@UNINOVA
 */
public class MobisUser {

	private long userId ;
	private String firstName ;
	private String lastName ;
	private String username ;
	private String password ;
	private String country ;
	private String email ;
	private String token ;
	private boolean hasOfflineGoogleAccess;
	
	private ArrayList<MobisRoute> routes ;
	
	public MobisUser() {}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ArrayList<MobisRoute> getRoutes() {
		return routes;
	}

	public void setRoutes(ArrayList<MobisRoute> routes) {
		this.routes = routes;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean hasOfflineGoogleAccess() {
		return hasOfflineGoogleAccess;
	}

	public void setHasOfflineGoogleAccess(boolean hasOfflineGoogleAccess) {
		this.hasOfflineGoogleAccess = hasOfflineGoogleAccess;
	}
}
