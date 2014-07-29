package org.uninova.mobis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class HTTPUtilsImpl
 * @author PAF@UNINOVA
 */
public class HTTPUtilsImpl implements HTTPUtils {

	/**
	 * HTTPUtilsImpl class Constructor
	 */
	public HTTPUtilsImpl() {}
	
	/**
	 * Opens a connection to the URL given by the {@link java.lang.String} input parameter <code>URLString</code>,
	 * sends a request to the specified URL and returns the response in the form of a {@link java.lang.String} object.
	 * 
	 * @param URLString		the URL to send the request
	 * @return				the response for the request or the error code, if the connection suffers any error
	 * @throws IOException
	 */
	public String requestURLConnection(String URLString) throws IOException {
		URL connectionURL = new URL(URLString) ;
		HttpURLConnection con = (HttpURLConnection) connectionURL.openConnection() ;
		int code = con.getResponseCode() ;
		BufferedReader in ;
		String inputLine, result = "";
		
		if (code == 200) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null) 
				result += inputLine ;
			in.close() ;
			con.disconnect() ;
			return result ;
		}
		else {
			con.disconnect() ;
			return "{error:" + String.valueOf(code) + "}" ;
		}
	}
}
