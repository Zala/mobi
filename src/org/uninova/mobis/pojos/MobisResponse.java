package org.uninova.mobis.pojos;

/**
 * Generic Class that is easy to be used for Mobis Server responses. 
 * The main benefit is that the client always knows the structure returned, and
 * that it can be easily used with GSon.
 * 
 * GSON Usage:
 * For example. We have MobisResponse<Route> response = new MobisResponse<Route>();
 * To be able to serialize/deserialize it, you first need  to get Type:
 * Type responseType = new TypeToken<MobisResponse<route>>(){}.getType();
 * then: gson.toJson(response, responseType);
 * 
 * To convert it back do: MobisResponse<Route> newRoute = gson.fromJson(json, responseType);
 * @author Luka Bradesko
 *
 * @param <T>
 */
public class MobisResponse<T> {
	private String error;
	private int errorCode;
	private T responseObject;

	public String getErrorMessage() {
		return error;
	}

	public void setErrorMessage(String error) {
		this.error = error;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public T getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(T responseObject) {
		this.responseObject = responseObject;
	}

}
