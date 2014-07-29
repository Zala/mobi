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
import javax.servlet.http.HttpSession;

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisNode;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisRoute;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class SaveRouteServlet
 */
public class SaveRouteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveRouteServlet() {
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
	
	public void processRequest(HttpServletRequest request, HttpServletResponse response) {
		DBUtils dbUtils = new DBUtilsImpl() ;
		Connection con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;
		HttpSession session = request.getSession() ;
		MobisMainDBConnector mainConnector = new MobisMainDBConnectorImpl() ;
		Type responseType = new TypeToken<MobisResponse<Integer>>(){}.getType();
		Gson gson = new Gson() ;
		MobisRoute route = gson.fromJson((String) session.getAttribute("theRoute"), MobisRoute.class) ;
		String token = request.getParameter("token") ;
		Long userId ;
		ArrayList<MobisSegment> segments ;
		ArrayList<MobisNode> nodes ;
		MobisSegment seg ;
		MobisNode node ;
		PrintWriter out ;
		MobisResponse<Integer> resp ;
		try {
			out = response.getWriter();
			resp = new MobisResponse<>() ;
			if (token != null & !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mainConnector.getUserIdFromToken(dbUtils, con, token) ;
				if (userId != null & !userId.equals("")) {
					int result = mainConnector.createRoute(dbUtils, con, route, userId) ;
					
					if (result > 0) {
						segments = route.getSegments() ;
						for (int i = 0; i < segments.size(); i++) {
							seg = segments.get(i) ;
							result = mainConnector.createSegment(dbUtils, con, seg, mainConnector.getLastRouteId(dbUtils, con)) ;
							if (result > 0) {
								nodes = seg.getNodes() ;
								for (int j = 0 ; j < nodes.size(); j++) {
									node = nodes.get(j) ;
									result = mainConnector.createNode(dbUtils, con, node, mainConnector.getLastSegmentId(dbUtils, con)) ;
									//TODO: Insert instructions and places into DB
									if (result > 0) {
										
									}
									else {
										resp.setErrorCode(NumericConstants.ERROR_NODE_INSERTION) ;
										resp.setErrorMessage(StringConstants.ERROR_NODE_INSERTION) ;
										out.println(gson.toJson(resp, responseType)) ;
										out.close() ;
										return ;
									}
								}
							}
							else {
								resp.setErrorCode(NumericConstants.ERROR_SEGMENT_INSERTION) ;
								resp.setErrorMessage(StringConstants.ERROR_SEGMENT_INSERTION) ;
								out.println(gson.toJson(resp, responseType)) ;
								out.close() ;
								return ;
							}
						}
						resp.setResponseObject(0) ;
						out.println(gson.toJson(resp, responseType)) ;
					}
					else {
						resp.setErrorCode(NumericConstants.ERROR_ROUTE_INSERTION) ;
						resp.setErrorMessage(StringConstants.ERROR_ROUTE_INSERTION) ;
						out.println(gson.toJson(resp, responseType)) ;
					}
					
				}
				else {
					resp.setErrorCode(NumericConstants.ERROR_NO_LOGIN) ;
					resp.setErrorMessage(StringConstants.ERROR_NO_LOGIN) ;
					out.println(gson.toJson(resp, responseType)) ;
				}
			}
			else {
				resp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER);
				resp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "token");
				out.println(gson.toJson(resp, responseType)) ;
			}
			con.close() ;
			out.close() ;
			return ;
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
