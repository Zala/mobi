package org.uninova.mobis.utils;

import java.sql.Connection;
import java.util.UUID;

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisUser;

public class CredentialUtilsImpl implements CredentialUtils {

	public CredentialUtilsImpl() {}
	
	public MobisResponse<Integer> handleRegister(String firstName, String lastName, String username, String password, String email, String country, String gId, String gAcessToken, String gRefreshToken) {
		DBUtilsImpl dbUtils = new DBUtilsImpl() ;
		//HTTPUtilsImpl httpUtils ;
		MobisMainDBConnector mainConnector = new MobisMainDBConnectorImpl() ;
		Connection con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;
		//String json, value, tokenString  ;
		MobisResponse<Integer> resp = new MobisResponse<>() ;
		UUID token ;
		//String prophetURL ;
		//JSONObject obj ;
		//JSONArray array ;
		
		if (gRefreshToken == null)
			gRefreshToken = "" ;
		if (gAcessToken == null)
			gAcessToken = "" ;
		
		if (!mainConnector.checkUsername(dbUtils, con, username)) {
			//httpUtils = new HTTPUtilsImpl() ;
			token = UUID.randomUUID() ;
			//prophetURL = "http://194.249.173.71:80/ProphetServer/loginRegister?loginMethod=register&email=" + email.replace("@", "%40") + "&password=" + password + "&server=http%3A%2F%2F194.249.173.71%3A80%2FProphetServer%2F&appID=Mobis+Commuting+Assistant&fullName=" + firstName + "+" + lastName + "&CCVersion=0.1.2013.09.10&InstallationId=Android72e55690-0999-46b6-86f5-83e07ccf8654&oauth_token=" + token.toString() ;
			
			//json = httpUtils.requestURLConnection(prophetURL) ;
			//if (!json.startsWith("{\"error\":")) {
			//	obj = JSONObject.fromObject(json) ;
			//	obj = obj.getJSONObject("discourse") ;
			//	array = obj.getJSONArray("discourseObjects") ;
			//	obj = array.getJSONObject(0) ;
			//	array = obj.getJSONArray("triggers") ;
			//	obj = array.getJSONObject(0) ;
			//	value = obj.getString("value") ;
			//	obj = JSONObject.fromObject(value) ;
			//	tokenString = obj.getString("prophetToken") ;
				
				resp.setResponseObject(mainConnector.createUser(dbUtils, con, firstName, lastName, username, password, email, token.toString(), country,gId, gAcessToken, gRefreshToken)) ;
			//}
				
		}
		else {
			resp.setErrorCode(NumericConstants.ERROR_DUPLICATE_USERNAME) ;
			resp.setErrorMessage(StringConstants.ERROR_DUPLICATE_USERNAME) ;
			
		}
		return resp ;
	}
	
	public MobisResponse<MobisUser> handleLogin(String username, String password) {
		DBUtilsImpl dbUtils = new DBUtilsImpl() ;
		//HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		Connection con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;
		MobisMainDBConnector mainConnector = new MobisMainDBConnectorImpl() ;
		MobisUser user = null ;
		MobisResponse<MobisUser> resp = new MobisResponse<>() ;
		//String prophetUrl, json ;
		
		long id = mainConnector.checkLoginCredentials(dbUtils, con, username, password) ;
	
		if (id >= 0) {
			user = mainConnector.getUserByUserId(dbUtils, con, id) ;
			//prophetUrl = "http://194.249.173.71:80/ProphetServer/loginRegister?loginMethod=login&email=" + user.getEmail().replace("@", "%40") + "&password=" + user.getPassword() + "&server=http%3A%2F%2F194.249.173.71%3A80%2FProphetServer%2F&appID=Mobis+Commuting+Assistant&CCVersion=0.1.2013.09.10&InstallationId=Android72e55690-0999-46b6-86f5-83e07ccf8654&oauth_token=" + user.getToken() ;
			//json = httpUtils.requestURLConnection(prophetUrl) ;
			
			//if (!json.startsWith("{\"error\":")) {
			//session.setAttribute("token", user.getToken()) ;
			//session.setAttribute("userId", id) ;
			//session.setAttribute("username", username) ;
			//session.setAttribute("gtoken", gToken) ;
			//session.setAttribute("country", user.getCountry()) ;
			
			resp.setResponseObject(user) ;
				
			//}
			
		}
		else {
			resp.setErrorCode(NumericConstants.ERROR_LOGIN_VALIDATION) ;
			resp.setErrorMessage(StringConstants.ERROR_LOGIN_VALIDATION) ;
		}
		return resp ;
	}
}
