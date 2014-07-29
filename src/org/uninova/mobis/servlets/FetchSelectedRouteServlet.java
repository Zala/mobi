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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisRoute;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class FetchSelectedRouteServlet
 */
public class FetchSelectedRouteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FetchSelectedRouteServlet() {
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
		String segmentIds = request.getParameter("segmentIds") ;
		int segmentsCount = Integer.parseInt(request.getParameter("segmentsCount")) ;
		HttpSession session = request.getSession() ;
		String result = (String) session.getAttribute("routeOptions"), transport ;
		String token = request.getParameter("token") ;
		DBUtils dbUtils = new DBUtilsImpl() ;
		Connection con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;
		MobisMainDBConnector mainDB = new MobisMainDBConnectorImpl() ;
		ArrayList<Integer> segmentNums = new ArrayList<Integer>() ;
		Gson gson = new Gson() ;
		JSONArray routes, segments ;
		JSONObject route ;
		MobisSegment seg ;
		MobisResponse<MobisRoute> resp = new MobisResponse<>() ;
		Type responseType = new TypeToken<MobisResponse<MobisRoute>>(){}.getType();
		MobisRoute theRoute = new MobisRoute() ;
		ArrayList<MobisSegment> segs = new ArrayList<>() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		PrintWriter out ;
		Long userId ;
		
		try {
			userId = mainDB.getUserIdFromToken(dbUtils, con, token) ;
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter() ;
			if (userId != null && userId >=0) {
					
				if (result != null && !result.equals("")) {	
					session.removeAttribute("routeOptions") ;
					
					if (segmentsCount > 1) {
						for (int i = 0; i < segmentsCount - 1; i++) {
							segmentNums.add(Integer.parseInt(segmentIds.substring(0, segmentIds.indexOf(",")))) ;
							segmentIds = segmentIds.substring(segmentIds.indexOf(",") + 1) ;
						}
					}
					segmentNums.add(Integer.parseInt(segmentIds)) ; 
					routes = JSONObject.fromObject(result).getJSONArray("routes") ;
					
					for (int i = 0; i < segmentsCount; i++) {
						route = routes.getJSONObject(i) ;
						segments = route.getJSONArray(segmentNums.get(i).toString()) ;
						for (int j = 0; j < segments.size(); j++) {
							seg = gson.fromJson(segments.getString(j), MobisSegment.class) ;
							segs.add(seg) ;
						}
					}
					transport = (String) session.getAttribute("transport") ;
					
					theRoute.setCreationtime(dateFormat.format(new Date())) ;
					theRoute.setTransport(transport) ;
					if (transport.equals(StringConstants.CAR_DISPLAY_TYPE) || transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE) || transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
						theRoute.setCriteria((int) session.getAttribute("criteria")) ;
					}
					theRoute.setStarttime(segs.get(0).getStartTime()) ;
					theRoute.setEndtime(segs.get(segs.size() - 1).getEndTime()) ;
					theRoute.setSegments(segs) ;
					
					result = JSONObject.fromObject(theRoute).toString() ;
					session.setAttribute("theRoute", result) ;
					resp.setResponseObject(theRoute) ;
					out.println(gson.toJson(resp, responseType)) ;
					
				}
				else {
					resp.setErrorCode(NumericConstants.ERROR_NO_ROUTE_IN_SESSION);
					resp.setErrorMessage(StringConstants.ERROR_NO_ROUTE_IN_SESSION);
					out.println(gson.toJson(resp, responseType)) ;
				}
			}
			else {
				resp.setErrorCode(NumericConstants.ERROR_NO_LOGIN) ;
				resp.setErrorMessage(StringConstants.ERROR_NO_LOGIN) ;
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
