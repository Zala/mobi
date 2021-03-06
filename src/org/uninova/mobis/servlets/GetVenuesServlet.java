package org.uninova.mobis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.uninova.mobis.apis.venues.FoursquareAPI;
import org.uninova.mobis.apis.venues.FoursquareAPIImpl;
import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisNode;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisRoute;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.pojos.MobisVenue;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class GetVenuesServlet
 */
public class GetVenuesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetVenuesServlet() {
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

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
		Gson gson = new Gson() ;
		DBUtils dbUtils = new DBUtilsImpl() ;
		Connection con ;
		HttpSession session = request.getSession() ;
		MobisMainDBConnector mobisMain = new MobisMainDBConnectorImpl() ;
		Type type = new TypeToken<MobisRoute>(){}.getType() ;
		Type responseType = new TypeToken<MobisResponse<ArrayList<MobisVenue>>>(){}.getType();
		boolean onRoute = Boolean.parseBoolean(request.getParameter("onRoute")) ;
		String categoriesStr = request.getParameter("categories") ;
		String token = request.getParameter("token") ;
		boolean inSession = Boolean.parseBoolean(request.getParameter("inSession")) ;
		String position ;
		String radiusStr ;
		String jsonRoute ;
		ArrayList<MobisVenue> venuesList = new ArrayList<MobisVenue>() ;
		FoursquareAPI foursquare = new FoursquareAPIImpl() ;
		MobisRoute route ;
		MobisSegment seg ;
		MobisNode node ;
		PrintWriter out ;
		Long userId ;
		MobisResponse<ArrayList<MobisVenue>> resp = new MobisResponse<>() ;
		
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter() ;
			if (token != null & !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mobisMain.getUserIdFromToken(dbUtils, con, token) ;
				if (userId != null && userId >= 0) {
					if (onRoute) {
						if (inSession) {
							route = gson.fromJson((String) session.getAttribute("theRoute"), type) ;
						}
						else {
							Part part = request.getPart("theRoute") ;
							StringWriter writer = new StringWriter();
							IOUtils.copy(part.getInputStream(), writer, "UTF-8");
							route = gson.fromJson((String)  writer.toString(), type) ;
						}
						if (route != null) {
							for (int i = 0; i < route.getSegments().size(); i++) {
								seg = route.getSegments().get(i) ;
								if (seg.getNodes().size() <= 15) {
									for (int j = 0; j < seg.getNodes().size(); j++) {
										node = seg.getNodes().get(j) ;
										if (foursquare.getFSVenues(categoriesStr, "1000", (node.getLat() + "," + node.getLng()), "10")!=null)
											venuesList.addAll(foursquare.getFSVenues(categoriesStr, "1000", (node.getLat() + "," + node.getLng()), "10")) ;
									}
								} 
								else {
									for (int j = 0; j < seg.getNodes().size()/15; j++) {
										node = seg.getNodes().get(j*15) ;
										if (foursquare.getFSVenues(categoriesStr, "1000", (node.getLat() + "," + node.getLng()), "10")!=null)
											venuesList.addAll(foursquare.getFSVenues(categoriesStr, "1000", (node.getLat() + "," + node.getLng()), "10")) ;
									}
								}
							}
						}
						else {
							resp.setErrorCode(NumericConstants.ERROR_NULL_ROUTE) ;
							resp.setErrorMessage(StringConstants.ERROR_NULL_ROUTE) ;
							out.println(gson.toJson(resp, responseType)) ;
						}
					}
					else {
						position = request.getParameter("position") ;
						radiusStr = request.getParameter("radius") ;
						if (radiusStr == null || radiusStr.equals("")) {
							radiusStr = "1000" ;
						}
						venuesList = foursquare.getFSVenues(categoriesStr, radiusStr, position, "50") ;
					}
					
					if (venuesList != null && !venuesList.isEmpty()) {
						resp.setResponseObject(venuesList) ;
						resp.setErrorCode(200);
						out.println(gson.toJson(resp, responseType)) ;
					}
					else {
						resp.setErrorCode(NumericConstants.ERROR_NONE_VENUE_NEARBY) ;
						resp.setErrorMessage(StringConstants.ERROR_NONE_VENUE_NEARBY) ;
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
				resp.setErrorCode(NumericConstants.ERROR_BAD_REQUEST_PARAMETER) ;
				resp.setErrorMessage(StringConstants.ERROR_BAD_REQUEST_PARAMETER + "token") ;
				out.println(gson.toJson(resp, responseType)) ;
			}
			out.close() ;
			return ;
		} catch (IOException | SQLException | IllegalStateException | ServletException e) {
			e.printStackTrace();
		}
	}
}