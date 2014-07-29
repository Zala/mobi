package org.uninova.mobis.servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisUser;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;
import org.uninova.mobis.utils.HTTPUtils;
import org.uninova.mobis.utils.HTTPUtilsImpl;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class HandleLoginServlet
 */

public class HandleGSignInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// google data
	private static final String SERVER_CLIENT_ID = "57506068082-v0v44meqlhcido44ilshaa1v5638m7so.apps.googleusercontent.com";
	private static final String SERVER_SECRET = "V5kOCEAZUy7aeDr5zSU-PrTn";

	private static final String INSTALLED_APP_CLIENT_ID = "57506068082.apps.googleusercontent.com";
	private static final String INSTALLED_APP_REDIRECT = "urn:ietf:wg:oauth:2.0:oob";

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new GsonFactory();

	private Gson gson = new Gson();

	DBUtils dbUtils = new DBUtilsImpl();
	HTTPUtils httpUtils = new HTTPUtilsImpl();
	MobisMainDBConnector mainConnector = new MobisMainDBConnectorImpl();
	HttpSession session;
	Connection con;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HandleGSignInServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
		session = request.getSession();

		MobisResponse<MobisUser> mr = new MobisResponse<>();

		con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN,
				StringConstants.JDBC_POSTGRES);

		String username = request.getParameter("email");
		String gToken = request.getParameter("gToken");
		String gCode = request.getParameter("gCode");
		String gid = request.getParameter("gId");

		try {
			if (gToken != null) {
				// verify and de-serialize Google JWT Token
				String[] clinetidS = new String[] { INSTALLED_APP_CLIENT_ID };
				Checker checker = new Checker(clinetidS, SERVER_CLIENT_ID);
				JsonWebToken.Payload p = checker.check(gToken);
				String id = p.getSubject();
				// if the id of the user from the client doesn't match the JWT token's ID
				if (!id.equals(gid)) {
					mr.setErrorCode(403);
					mr.setResponseObject(null);
					mr.setErrorMessage("Possible authorization fraud");
					writeResponse(mr, response);
					return;
				}

				MobisUser user = this.mainConnector.getUserByGoogleUserId(dbUtils, con, id);
				// if everything is fine with the user
				// TODO:(here we have to check thevalidity of the refresh token as well
				if (user != null && user.hasOfflineGoogleAccess()) {
					mr.setErrorCode(200);
					mr.setResponseObject(user);
					writeResponse(mr, response);
					return;
				}

				// tell the client to send tokenCode for the offline access
				mr.setErrorCode(401);
				mr.setResponseObject(null);
				writeResponse(mr, response);
				return;
			} else {
				// Upgrade the authorization code into an access and refresh token.
				GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
						HTTP_TRANSPORT, JSON_FACTORY, SERVER_CLIENT_ID, SERVER_SECRET, gCode,
						INSTALLED_APP_REDIRECT).execute();

				// Create a credential representation of the token data.
				GoogleCredential credential = new GoogleCredential.Builder()
						.setJsonFactory(JSON_FACTORY).setTransport(HTTP_TRANSPORT)
						.setClientSecrets(SERVER_CLIENT_ID, SERVER_SECRET).build()
						.setFromTokenResponse(tokenResponse);

				// Check that the token is valid.
				Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
				Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken())
						.execute();
				// If there was an error in the token info, abort.
				if (tokenInfo.containsKey("error")) {
					mr.setErrorCode(401);
					mr.setErrorMessage("Error negotiating with Google");
					writeResponse(mr, response);
					return;
				}
				// Make sure the token we got is for the intended user.
				if (!tokenInfo.getUserId().equals(gid)) {
					mr.setErrorCode(401);
					mr.setErrorMessage("Token's user ID doesn't match given user ID.");
					writeResponse(mr, response);
					return;
				}
				// Make sure the token we got is for our app.
				if (!tokenInfo.getIssuedTo().equals(SERVER_CLIENT_ID)) {
					mr.setErrorCode(401);
					mr.setErrorMessage("Token's client ID does not match app's.");
					writeResponse(mr, response);
					return;
				}

				// Set up the main Google+ class
				Plus plus = new Plus(HTTP_TRANSPORT, JSON_FACTORY, credential);
				// Make a request to access your profile and display it to console
				Person profile = plus.people().get("me").execute();

				
				String refreshtoken = credential.getRefreshToken(); //tokenResponse.getRefreshToken();
				String mobisToken = UUID.randomUUID().toString();
				String accesstoken = credential.getAccessToken();//tokenResponse.getAccessToken();
				String firstName = profile.getName().getGivenName();
				String lastName = profile.getName().getFamilyName();
				String password = UUID.randomUUID().toString();
				String email = username;
				String country = profile.getCurrentLocation();

				                         
				int result = this.mainConnector.createUser(dbUtils, con, firstName, lastName, username,
						password, email, gid, mobisToken, country, accesstoken, refreshtoken);
				if (result == -1) {
					mr.setErrorCode(500);
					mr.setErrorMessage("Cannot register this user");
					writeResponse(mr, response);
					return;
				}

				MobisUser user = this.mainConnector.getUserByGoogleUserId(dbUtils, con, gid);
				if (user != null && user.hasOfflineGoogleAccess()) {
					mr.setErrorCode(200);
					mr.setResponseObject(user);
					writeResponse(mr, response);
					return;
				}

				// tell the client to send tokenCode for the offline access
				mr.setErrorCode(500);
				mr.setResponseObject(null);
				mr.setErrorMessage("Problems while creating the user");
				writeResponse(mr, response);
				return;
			}
		} catch (Exception ex) {
		// tell the client to send tokenCode for the offline access
			mr.setErrorCode(500);
			mr.setResponseObject(null);
			mr.setErrorMessage(ex.getMessage());
			writeResponse(mr, response);
			ex.printStackTrace();
		}
	}

	private void writeResponse(MobisResponse<MobisUser> mr, HttpServletResponse response) {
		try {

			response.setStatus(mr.getErrorCode());
			Type responseType = new TypeToken<MobisResponse<MobisUser>>() {
			}.getType();
			response.getWriter().print(gson.toJson(mr, responseType));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public String deserializeJWTToken(String tokenString) {
		String[] pieces = splitTokenString(tokenString);
		String jwtPayloadSegment = pieces[1];
		JsonParser parser = new JsonParser();
		JsonElement payload = parser.parse(StringUtils.newStringUtf8(Base64
				.decodeBase64(jwtPayloadSegment)));
		return payload.toString();
	}

	/**
	 * @param tokenString
	 *           The original encoded representation of a JWT
	 * @return Three components of the JWT as an array of strings
	 */
	private String[] splitTokenString(String tokenString) {
		String[] pieces = tokenString.split(Pattern.quote("."));
		if (pieces.length != 3) {
			throw new IllegalStateException("Expected JWT to have 3 segments separated by '" + "."
					+ "', but it has " + pieces.length + " segments");
		}
		return pieces;
	}

	public class Checker {

		private final List mClientIDs;
		private final String mAudience;
		private final GoogleIdTokenVerifier mVerifier;
		private final JsonFactory mJFactory;
		private String mProblem = "Verification failed. (Time-out?)";

		public Checker(String[] clientIDs, String audience) {
			mClientIDs = Arrays.asList(clientIDs);
			mAudience = audience;
			NetHttpTransport transport = new NetHttpTransport();
			mJFactory = new GsonFactory();
			mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
		}

		public GoogleIdToken.Payload check(String tokenString) {
			GoogleIdToken.Payload payload = null;
			try {
				GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
				if (mVerifier.verify(token)) {
					GoogleIdToken.Payload tempPayload = token.getPayload();
					if (!tempPayload.getAudience().equals(mAudience))
						mProblem = "Audience mismatch";
					else if (!mClientIDs.contains(tempPayload.getIssuee()))
						mProblem = "Client ID mismatch";
					else
						payload = tempPayload;
				}
			} catch (GeneralSecurityException e) {
				mProblem = "Security issue: " + e.getLocalizedMessage();
			} catch (IOException e) {
				mProblem = "Network problem: " + e.getLocalizedMessage();
			}
			return payload;
		}

		public String problem() {
			return mProblem;
		}
	}
}
