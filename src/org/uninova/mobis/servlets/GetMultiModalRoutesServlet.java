package org.uninova.mobis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.uninova.mobis.apis.swedish.ResRobotJourneyPlanner;
import org.uninova.mobis.apis.swedish.ResRobotJourneyPlannerImpl;
import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class GetMultiModalRoutesServlet
 */
public class GetMultiModalRoutesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMultiModalRoutesServlet() {
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
		GISUtils gisUtils = new GISUtilsImpl() ;
		Gson gson = new Gson() ;
		Type responseType = new TypeToken<MobisResponse<ArrayList<HashMap<String, ArrayList<MobisSegment>>>>>(){}.getType();
		HttpSession session = request.getSession() ;
		String country = request.getParameter("country") ;
		String token = request.getParameter("token") ;
 		ResRobotJourneyPlanner resRobot = new ResRobotJourneyPlannerImpl() ;
		MobisMainDBConnector mobisMain = new MobisMainDBConnectorImpl() ;
		boolean isCoords = Boolean.parseBoolean(request.getParameter("coords")) ;
		String waypoints = request.getParameter("waypoints") ;
		String time = request.getParameter("time") ;
		String transport = request.getParameter("transport") ;
		PrintWriter out ;
		ArrayList<Double> lats = new ArrayList<Double>() ;
		ArrayList<Double> lngs = new ArrayList<Double>() ;
		ArrayList<HashMap<String, ArrayList<MobisSegment>>> routes = new ArrayList<>() ;
		HashMap<String, ArrayList<MobisSegment>> segmentMap = new HashMap<>() ;
		Coordinate coord, fromCoord, toCoord ;
		Double originLat, originLng, destLat, destLng ;
		String origin, destination, originStreet, originCity, destinationStreet, destinationCity, result ;
		String[] points = null ;
		MobisResponse<ArrayList<HashMap<String, ArrayList<MobisSegment>>>> resp = new MobisResponse<>() ;
		Long userId ;
		Connection con ;
		
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter() ;
			if (token != null & !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mobisMain.getUserIdFromToken(dbUtils, con, token) ;
				con.close() ;
				if (userId != null && userId > 0) {
					if (country != null && !country.equals("")) {
						// HANDLE COORDINATES AND ADDRESSES
						if (isCoords) {
							origin = request.getParameter("origin") ;
							destination = request.getParameter("destination") ;
							coord = gisUtils.getCoordinateFromString(origin) ;
							if (coord == null) {
								resp.setErrorCode(NumericConstants.ERROR_ORIGIN_COORDINATE_NOT_FOUND) ;
								resp.setErrorMessage(StringConstants.ERROR_ORIGIN_COORDINATE_NOT_FOUND) ;
								out.println(gson.toJson(resp, responseType)) ;
								out.close() ;
								return ;
							}
							originLat = coord.getLat() ;
							originLng = coord.getLng() ;
							
							coord = gisUtils.getCoordinateFromString(destination) ;
							if (coord == null) {
								resp.setErrorCode(NumericConstants.ERROR_DESTINATION_COORDINATE_NOT_FOUND) ;
								resp.setErrorMessage(StringConstants.ERROR_DESTINATION_COORDINATE_NOT_FOUND) ;
								out.println(gson.toJson(resp, responseType)) ;
								out.close() ;
								return ;
							}
							destLat = coord.getLat() ;
							destLng = coord.getLng() ;
						}
						else {
							originStreet = request.getParameter("originstreet") ;
							originCity = request.getParameter("origincity") ;
							destinationStreet = request.getParameter("destinationstreet") ;
							destinationCity = request.getParameter("destinationcity") ;
							coord = gisUtils.getCoordinateFromAddress(originStreet, originCity) ;
							if (coord == null) {
								resp.setErrorCode(NumericConstants.ERROR_ORIGIN_ADDRESS_NOT_FOUND) ;
								resp.setErrorMessage(StringConstants.ERROR_ORIGIN_ADDRESS_NOT_FOUND) ;
								out.println(gson.toJson(resp, responseType)) ;
								out.close() ;
								return ;
							}
							originLat = coord.getLat() ;
							originLng = coord.getLng() ;
							
							coord = gisUtils.getCoordinateFromAddress(destinationStreet, destinationCity) ;
							if (coord == null) {
								resp.setErrorCode(NumericConstants.ERROR_DESTINATION_ADDRESS_NOT_FOUND) ;
								resp.setErrorMessage(StringConstants.ERROR_DESTINATION_ADDRESS_NOT_FOUND) ;
								out.println(gson.toJson(resp, responseType)) ;
								out.close() ;
								return ;
							}
							destLat = coord.getLat() ;
							destLng = coord.getLng() ;
						}
						
						if (time.contains("%20"))
							time = time.replace("%20", " ") ;
						
						lats.add(originLat) ;
						lngs.add(originLng) ;
						
						// HANDLE WAYPOINTS, IF ANY
						if (waypoints != null && !waypoints.equals("") && !waypoints.equals("null")) {
							if (waypoints.contains("!!"))
								points = waypoints.split("!!") ;
							else {
								points = new String[1] ;
								points[0] = waypoints ;
							}
							
							if (points.length > 0) {
								String point ;
								
								for (int i = 0; i < points.length; i++) {
									point = points[i] ;
									coord = gisUtils.getCoordinateFromString(point) ;
									
									lats.add(coord.getLat()) ;
									lngs.add(coord.getLng()) ;
								}
							}
						}
						
						lats.add(destLat) ;
						lngs.add(destLng) ;
						
						// CALCULATE ROUTE OPTIONS FOR EACH PAIR OF COORDINATES
						for (int i = 0; i < lats.size() - 1; i++) {
							
							originLat = lats.get(i) ;
							originLng = lngs.get(i) ;
							
							destLat = lats.get(i + 1) ;
							destLng = lngs.get(i + 1) ;
							
							fromCoord = new Coordinate(originLat, originLng) ;
							toCoord = new Coordinate(destLat, destLng) ;
						
							// GET DIFFERENT ROUTES OPTIONS
							if (country.equals("Sweden")) {
								result = resRobot.search("coords", fromCoord, toCoord, "", "", time, false, 1, null) ;
								segmentMap = resRobot.parseResRobotSegments(JSONObject.fromObject(result)) ;
							}
							
							routes.add(segmentMap) ;
						}
						
						result = JSONArray.fromObject(routes).toString() ;
						session.setAttribute("routeOptions", result) ;
						session.setAttribute("transport", transport) ;
						resp.setResponseObject(routes) ;
						out.println(gson.toJson(resp, responseType)) ;
					}
					else {
						resp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER);
						resp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "country");
						out.println(gson.toJson(resp, responseType)) ;
					}
				}
				else {
					resp.setErrorCode(NumericConstants.ERROR_NO_LOGIN);
					resp.setErrorMessage(StringConstants.ERROR_NO_LOGIN);
					out.println(gson.toJson(resp, responseType)) ;
				}
				con.close(); 
			}
			else {
				resp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER);
				resp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "token");
				out.println(gson.toJson(resp, responseType)) ;
			}
			out.close() ;
			return ;
		} catch (IOException | SQLException e) {
			e.printStackTrace() ;
		}
	}
}
