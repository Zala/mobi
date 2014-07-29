package org.uninova.mobis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisSensorLog;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class HandleSpyLocationServlet
 */
public class HandleCollectedDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HandleCollectedDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response) ;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response) ;
	}
	
	public void processRequest(HttpServletRequest request, HttpServletResponse response) {
		DBUtils dbUtils = new DBUtilsImpl() ;
		Gson gson = new Gson() ;
		Type responseType = new TypeToken<MobisResponse<Integer>>(){}.getType();
		Connection con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;
		MobisMainDBConnector mainDB = new MobisMainDBConnectorImpl() ;
		MobisSensorLog log ;
		String token = request.getParameter("token") ;
		Long userId, routeLogId ;
		PrintWriter out ;
		int result = -1 ;
		MobisResponse<Integer> resp = new MobisResponse<>() ;
		JSONArray array ;
		JSONObject obj, content ;
		String type ;
		
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter();
			if (token != null && !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mainDB.getUserIdFromToken(dbUtils, con, token) ;

				if (userId != null && !userId.equals("")) {
					String data = request.getParameter("collectedData");
					System.out.println(data);
					
					array = JSONArray.fromObject(data) ;
					for (int i = 0; i < array.size(); i++) {
						obj = (JSONObject) array.get(i) ;
						type = obj.getString("$type") ;
						content = obj.getJSONObject("$content") ;
						
						if (type.equals("START_TRIP")) {
							mainDB.createRouteLog(dbUtils, con, userId, content.getString("timestamp")) ;
						}
						else if (type.equals("END_TRIP")) {
							routeLogId = mainDB.getLastRouteLogId(dbUtils, con) ;
							mainDB.updateRouteLog(dbUtils, con, content.getString("timestamp"), routeLogId) ;
						}
						
						log = new MobisSensorLog() ;
						log.setLogType(type) ;
						log.setTimestamp(content.getString("timestamp")) ;
						log.setRecord(content.toString()) ;
						routeLogId = mainDB.getLastRouteLogId(dbUtils, con) ;
						mainDB.createSensorLog(dbUtils, con, routeLogId, log) ;
					}
					
					resp.setResponseObject(result) ;
				}
				else {
					resp.setErrorCode(NumericConstants.ERROR_NO_LOGIN) ;
					resp.setErrorMessage(StringConstants.ERROR_NO_LOGIN) ;		
				}
			}
			else {
				resp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER) ;
				resp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "token") ;	
			}
			out.println(gson.toJson(resp, responseType)) ;
			con.close() ;
			out.close() ;
			return ;
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

}
