package org.uninova.mobis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisGraphDBConnector;
import org.uninova.mobis.database.MobisGraphDBConnectorImpl;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisRoute;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class GetAStarRouteServlet
 */
public class GetAStarRouteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAStarRouteServlet() {
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
		GISUtils gisUtils = new GISUtilsImpl() ;
		DBUtils dbUtils = new DBUtilsImpl() ;
		Gson gson = new Gson() ;
		Type responseType = new TypeToken<MobisResponse<MobisRoute>>(){}.getType();
		HttpSession session = request.getSession() ;
		String country = request.getParameter("country") ;
		String token = request.getParameter("token") ;
		Connection con = null ;
		MobisGraphDBConnector mobisGraph = new MobisGraphDBConnectorImpl() ;
		MobisMainDBConnector mobisMain = new MobisMainDBConnectorImpl() ;
		boolean isCoords = Boolean.parseBoolean(request.getParameter("coords")) ;
		String waypoints = request.getParameter("waypoints") ;
		String time = request.getParameter("time") ;
		String transport = request.getParameter("transport") ;
		ArrayList<MobisSegment> segments = new ArrayList<>() ;
		ArrayList<Double> lats = new ArrayList<Double>() ;
		ArrayList<Double> lngs = new ArrayList<Double>() ;
		Coordinate coord, fromCoord, toCoord ;
		Double originLat, originLng, destLat, destLng ;
		String origin, destination, originStreet, originCity, destinationStreet, destinationCity, result ;
		String[] points = null ;
		int criteria ;
		MobisSegment segment ;
		MobisRoute route = new MobisRoute() ;
		MobisResponse<MobisRoute> resp = new MobisResponse<>() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		PrintWriter out ;
		Long userId ;
		
		try {
			
			out = response.getWriter() ;
			response.setContentType("text/html;charset=UTF-8");
			
			if (token != null & !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mobisMain.getUserIdFromToken(dbUtils, con, token) ;
				if (userId != null && userId > 0) {
					if (country != null && !country.equals("")) {
						
						if (country.equals("Sweden")) {
							con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.SWEDEN_GRAPH, StringConstants.JDBC_POSTGRES) ;
						}
						else if (country.equals("Slovenia")) {
							con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.SLOVENIA_GRAPH, StringConstants.JDBC_POSTGRES) ;
						}
						else if (country.equals("Greece")) {
							con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.GREECE_GRAPH, StringConstants.JDBC_POSTGRES) ;
						}
						
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
						
						// CALCULATE ROUTE FOR EACH PAIR OF COORDINATES
						for (int i = 0; i < lats.size() - 1; i++) {
							
							originLat = lats.get(i) ;
							originLng = lngs.get(i) ;
							
							destLat = lats.get(i + 1) ;
							destLng = lngs.get(i + 1) ;
							
							fromCoord = new Coordinate(originLat, originLng) ;
							toCoord = new Coordinate(destLat, destLng) ;
						
							// GET DIFFERENT ROUTES OPTIONS
							if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
								criteria = Integer.parseInt(request.getParameter("criteria")) ;
								route.setCriteria(criteria) ;
								segment = mobisGraph.aStarPath(dbUtils, con, fromCoord, toCoord, i, time, transport, 0.00001, criteria, "ways", true, false, "4326", i == 0 ? true : false, "") ;
								segments.add(segment) ;
								
							}
							else if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE)) {
								criteria = Integer.parseInt(request.getParameter("criteria")) ;
								route.setCriteria(criteria) ;
								segment = mobisGraph.aStarPath(dbUtils, con, fromCoord, toCoord, i, time, transport, 0.00001, criteria, "ways", true, false, "4326", i == 0 ? true : false, "") ;
								segments.add(segment) ;
							}
							else if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
								criteria = Integer.parseInt(request.getParameter("criteria")) ;
								route.setCriteria(criteria) ;
								segment = mobisGraph.aStarPath(dbUtils, con, fromCoord, toCoord, i, time, transport, 0.00001, criteria, "ways", true, false, "4326", i == 0 ? true : false, "") ;
								segments.add(segment) ;
							}
							route.setTransport(transport) ;
							route.setStartCoord(new Coordinate(lats.get(0),lngs.get(0))) ;
							route.setEndCoord(new Coordinate(lats.get(lats.size() - 1), lngs.get(lngs.size() - 1))) ;
							route.setStarttime(time) ;
							route.setEndtime(segments.get(segments.size() - 1).getEndTime());
							route.setCreationtime(dateFormat.format(new Date())) ;
							route.setSegments(segments) ;
						}
						
						result = JSONObject.fromObject(route).toString() ;
						session.setAttribute("theRoute", result) ;
						session.setAttribute("transport", transport) ;
						resp.setResponseObject(route) ;
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
			e.printStackTrace() ;
		}
	}

}
