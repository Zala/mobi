package org.uninova.mobis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisRoute;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class GetUserRoutesServlet
 */
public class GetUserRoutesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserRoutesServlet() {
        super();
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

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
		
		Gson gson = new Gson() ;
		Type responseType = new TypeToken<MobisResponse<ArrayList<MobisRoute>>>(){}.getType();
		MobisMainDBConnector mainConnector ;
		String token = request.getParameter("token") ;
		DBUtils dbUtils ;
		Connection con ;
		PrintWriter out ;
		ArrayList<MobisRoute> routes ;
		MobisResponse<ArrayList<MobisRoute>> resp = new MobisResponse<>() ;
		Long userId ;
		
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter() ;
			mainConnector = new MobisMainDBConnectorImpl() ;
			dbUtils = new DBUtilsImpl() ;
			if (token != null & !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mainConnector.getUserIdFromToken(dbUtils, con, token) ;
				if (userId != null && userId >= 0) {
					routes = mainConnector.getAllRoutesFromUser(dbUtils, con, userId) ;
					if (routes != null && !routes.isEmpty()) {
						resp.setResponseObject(routes);
						out.println(gson.toJson(resp, responseType)) ;
					}
					else {
						resp.setErrorCode(NumericConstants.ERROR_NONE_USER_ROUTE) ;
						resp.setErrorMessage(StringConstants.ERROR_NONE_USER_ROUTE) ;
						out.println(gson.toJson(resp, responseType)) ;
					}
				}
				else {
					resp.setErrorCode(NumericConstants.ERROR_NO_LOGIN) ;
					resp.setErrorMessage(StringConstants.ERROR_NO_LOGIN) ;
					out.println(gson.toJson(resp, responseType)) ;
				}
				con.close() ;
			}
			else {
				resp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER);
				resp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "token");
				out.println(gson.toJson(resp, responseType)) ;
			}
			out.close() ;
			return ;
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
