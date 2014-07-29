package org.uninova.mobis.utils;

import java.io.IOException;

/**
 * Interface HTTPUtils
 * @author PAF@UNINOVA
 */
public interface HTTPUtils {

	/**
	 * Opens a connection to the URL given by the {@link java.lang.String} input parameter <code>URLString</code>,
	 * sends a request to the specified URL and returns the response in the form of a {@link java.lang.String} object.
	 * 
	 * @param URLString		the URL to send the request
	 * @return				the response for the request or the error code, if the connection suffers any error
	 * @throws IOException
	 */
	public String requestURLConnection(String URLString) throws IOException ;
	
}
