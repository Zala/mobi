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
import org.uninova.mobis.database.MobisPrometDBConnector;
import org.uninova.mobis.database.MobisPrometDBConnectorImpl;
import org.uninova.mobis.database.MobisTrafficDBConnector;
import org.uninova.mobis.database.MobisTrafficDBConnectorImpl;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisNode;
import org.uninova.mobis.pojos.MobisPrometEvent;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisRoute;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.pojos.MobisTrafficSituation;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class GetTrafficSituationsServlet
 */
public class GetTrafficSituationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTrafficSituationsServlet() {
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
		DBUtils dbUtils = new DBUtilsImpl() ;
		GISUtils gisUtils = new GISUtilsImpl() ;
		Gson gson = new Gson() ;
		Connection con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_TRAFFIC, StringConstants.JDBC_POSTGRES) ;
		MobisTrafficDBConnector trafficConnector = new MobisTrafficDBConnectorImpl() ;
		MobisPrometDBConnector prometConnector = new MobisPrometDBConnectorImpl() ;
		MobisMainDBConnector mobisMain = new MobisMainDBConnectorImpl() ;
		PrintWriter out ;
		Type responseType ; 
		String token = request.getParameter("token") ;
		String types = request.getParameter("types") ;
		boolean getRelated = Boolean.parseBoolean(request.getParameter("getRelated")) ;
		boolean onRoute = Boolean.parseBoolean(request.getParameter("onRoute")) ;
		boolean isPoint = Boolean.parseBoolean(request.getParameter("isPoint")) ;
		String coord = request.getParameter("coord") ;
		String country = request.getParameter("country") ;
		String startDateTime = request.getParameter("startTime") ;
		String jsonRoute = request.getParameter("jsonRoute") ;
		Long userId ;
		MobisRoute route = gson.fromJson(jsonRoute, MobisRoute.class) ;
		MobisSegment seg ;
		MobisNode node ;
		MobisResponse<ArrayList<MobisTrafficSituation>> seResp ;
		MobisResponse<ArrayList<MobisPrometEvent>> slResp ;
		ArrayList<Coordinate> coords ;
		ArrayList<MobisTrafficSituation> situations = null ;
		ArrayList<MobisPrometEvent> events = null ;
		
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter() ;
			if (token != null & !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mobisMain.getUserIdFromToken(dbUtils, con, token) ;
				if (userId != null && userId > 0) {
					if (country != null && !country.equals("")) {
						if (country.equals("Slovenia")) {
							slResp = new MobisResponse<>() ;
							responseType = new TypeToken<MobisResponse<ArrayList<MobisPrometEvent>>>(){}.getType(); 
							if (types != null && !types.equals("")) {
								if (!onRoute) {
									if (!isPoint)
										events = prometConnector.getEventsByCategory(dbUtils, con, types) ;
									else {
										coords = new ArrayList<>() ;
										coords.add(gisUtils.getCoordinateFromString(coord)) ;
										events = prometConnector.getLocalizedEventsByCategory(dbUtils, con, types, coords, startDateTime) ;
									}
								}
								else {
									if (route != null) {
										coords = new ArrayList<>() ;
										for (int i = 0; i < route.getSegments().size(); i++) {
											seg = route.getSegments().get(i) ;
											for (int j = 0; j < seg.getNodes().size(); j++) {
												node = seg.getNodes().get(j) ;
												coords.add(new Coordinate(node.getLat(), node.getLng())) ;
											}
										}
										events = prometConnector.getLocalizedEventsByCategory(dbUtils, con, types, coords, startDateTime) ;
									}
									else {
										slResp.setErrorCode(NumericConstants.ERROR_NULL_ROUTE) ;
										slResp.setErrorMessage(StringConstants.ERROR_NULL_ROUTE) ;
										out.println(gson.toJson(slResp, responseType)) ;
										out.close() ;
										return ;
									}
								}
							}
							else {
								if (!onRoute) {
									if (!isPoint)
										events = prometConnector.getAllEvents(dbUtils, con) ;
									else {
										coords = new ArrayList<>() ;
										coords.add(gisUtils.getCoordinateFromString(coord)) ;
										events = prometConnector.getLocalizedEventsByCategory(dbUtils, con, "", coords, startDateTime) ;
									}
									
								}
								else {
									coords = new ArrayList<>() ;
									for (int i = 0; i < route.getSegments().size(); i++) {
										seg = route.getSegments().get(i) ;
										for (int j = 0; j < seg.getNodes().size(); j++) {
											node = seg.getNodes().get(j) ;
											coords.add(new Coordinate(node.getLat(), node.getLng())) ;
										}
									}
									events = prometConnector.getLocalizedEventsByCategory(dbUtils, con, "", coords, startDateTime) ;
								}
							}
							if (events != null) {
								slResp.setResponseObject(events) ;
								out.println(gson.toJson(slResp, responseType)) ;
								out.close() ;
								return ;
							}
							else {
								slResp.setErrorCode(NumericConstants.ERROR_NONE_TRAFFIC_EVENT_FOUND) ;
								slResp.setErrorMessage(StringConstants.ERROR_NONE_TRAFFIC_EVENT_FOUND) ;
								out.println(gson.toJson(slResp, responseType)) ;
								out.close() ;
								return ;
							}
						}
						else {
							seResp = new MobisResponse<>() ;
							responseType = new TypeToken<MobisResponse<ArrayList<MobisTrafficSituation>>>(){}.getType(); 
							if (types != null && !types.equals("")) {
								if (!onRoute) {
									if (!isPoint)
										situations = trafficConnector.getSituationsByCategory(dbUtils, con, types, getRelated, country) ;
									else {
										coords = new ArrayList<>() ;
										coords.add(gisUtils.getCoordinateFromString(coord)) ;
										situations = trafficConnector.getLocalizedSituationsByCategory(dbUtils, con, types, getRelated, coords, startDateTime) ;
									}
								}
								else {
									if (route != null) {
										coords = new ArrayList<>() ;
										for (int i = 0; i < route.getSegments().size(); i++) {
											seg = route.getSegments().get(i) ;
											for (int j = 0; j < seg.getNodes().size(); j++) {
												node = seg.getNodes().get(j) ;
												coords.add(new Coordinate(node.getLat(), node.getLng())) ;
											}
										}
										situations = trafficConnector.getLocalizedSituationsByCategory(dbUtils, con, types, getRelated, coords, startDateTime) ;
									}
									else {
										seResp.setErrorCode(NumericConstants.ERROR_NULL_ROUTE) ;
										seResp.setErrorMessage(StringConstants.ERROR_NULL_ROUTE) ;
										out.println(gson.toJson(seResp, responseType)) ;
										out.close() ;
										return ;
									}
								}
							}
							else {
								if (!onRoute) {
									if (!isPoint)
										situations = trafficConnector.getAllSituations(dbUtils, con, country) ;
									else {
										coords = new ArrayList<>() ;
										coords.add(gisUtils.getCoordinateFromString(coord)) ;
										situations = trafficConnector.getLocalizedSituationsByCategory(dbUtils, con, "", false, coords, startDateTime) ;
									}
									
								}
								else {
									coords = new ArrayList<>() ;
									for (int i = 0; i < route.getSegments().size(); i++) {
										seg = route.getSegments().get(i) ;
										for (int j = 0; j < seg.getNodes().size(); j++) {
											node = seg.getNodes().get(j) ;
											coords.add(new Coordinate(node.getLat(), node.getLng())) ;
										}
									}
									situations = trafficConnector.getLocalizedSituationsByCategory(dbUtils, con, "", false, coords, startDateTime) ;
								}
							}
							if (situations != null) {
								seResp.setResponseObject(situations) ;
								out.println(gson.toJson(seResp, responseType)) ;
							}
							else {
								seResp.setErrorCode(NumericConstants.ERROR_NONE_TRAFFIC_EVENT_FOUND) ;
								seResp.setErrorMessage(StringConstants.ERROR_NONE_TRAFFIC_EVENT_FOUND) ;
								out.println(gson.toJson(seResp, responseType)) ;
							}
						}
					}
					
					else {
						seResp = new MobisResponse<>() ;
						responseType = new TypeToken<MobisResponse<ArrayList<MobisTrafficSituation>>>(){}.getType();
						seResp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER);
						seResp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "country");
						out.println(gson.toJson(seResp, responseType)) ;
					}
				}
				else {
					seResp = new MobisResponse<>() ;
					responseType = new TypeToken<MobisResponse<ArrayList<MobisTrafficSituation>>>(){}.getType();
					seResp.setErrorCode(NumericConstants.ERROR_NO_LOGIN);
					seResp.setErrorMessage(StringConstants.ERROR_NO_LOGIN);
					out.println(gson.toJson(seResp, responseType)) ;
				}
			}
			else {
				seResp = new MobisResponse<>() ;
				responseType = new TypeToken<MobisResponse<ArrayList<MobisTrafficSituation>>>(){}.getType();
				seResp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER);
				seResp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "token");
				out.println(gson.toJson(seResp, responseType)) ;
			}
			con.close() ;
			out.close() ;
			return ;
		} catch (IOException | SQLException e) {
			e.printStackTrace() ;
		}
	}

}
