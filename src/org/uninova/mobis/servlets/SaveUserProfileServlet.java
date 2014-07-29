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

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisProfile;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisVehicle;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class SaveUserProfileServlet
 */
public class SaveUserProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveUserProfileServlet() {
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
		String token = request.getParameter("token") ;
		Gson gson = new Gson() ;
		Type responseType = new TypeToken<MobisResponse<Integer>>(){}.getType();
		MobisMainDBConnector mainDB = new MobisMainDBConnectorImpl() ;
		MobisProfile profile ;
		MobisVehicle vehicle ;
		Long profileId, userId ;
		PrintWriter out ;
		MobisResponse<Integer> resp ;
		
		try {
			response.setContentType("text/html;charset=UTF-8");
			out = response.getWriter() ;
			resp = new MobisResponse<>() ;
			if (token != null & !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;				
				userId = mainDB.getUserIdFromToken(dbUtils, con, token) ;
				if (userId != null && userId > 0) {
					profile = new MobisProfile() ;
					
					profile.setGpsDevice(Boolean.parseBoolean(request.getParameter("gps_device"))) ;
					profile.setGpsService(Boolean.parseBoolean(request.getParameter("gps_service"))) ;
					profile.setTripPlanner(Boolean.parseBoolean(request.getParameter("trip_planner"))) ;
					profile.setPaperMap(Boolean.parseBoolean(request.getParameter("paper_map"))) ;
					profile.setOtherAssistant(request.getParameter("other_trip_assistant")) ;
					
					profile.setOwnsCar(Boolean.parseBoolean(request.getParameter("owns_car"))) ;
					profile.setOwnsMotorbike(Boolean.parseBoolean(request.getParameter("owns_motorbike"))) ;
					profile.setOwnsBike(Boolean.parseBoolean(request.getParameter("owns_bike"))) ;
				
					profile.setHandicapped(Boolean.parseBoolean(request.getParameter("handicapped"))) ;
					profile.setDisableCar(Boolean.parseBoolean(request.getParameter("disable_car"))) ;
					profile.setDisableMotorbike(Boolean.parseBoolean(request.getParameter("disable_motorbike"))) ;
					profile.setDisableBike(Boolean.parseBoolean(request.getParameter("disable_bike"))) ;
					profile.setDisableTrain(Boolean.parseBoolean(request.getParameter("disable_train"))) ;
					profile.setDisableBus(Boolean.parseBoolean(request.getParameter("disable_bus"))) ;
					profile.setDisableTaxi(Boolean.parseBoolean(request.getParameter("disable_taxi"))) ;
					profile.setDisableSubway(Boolean.parseBoolean(request.getParameter("disable_subway"))) ;
					profile.setDisableWalk(Boolean.parseBoolean(request.getParameter("disable_walk"))) ;
				
					profile.setCarFrequency(Integer.parseInt(request.getParameter("car_freq"))) ;
					profile.setMotorbikeFrequency(Integer.parseInt(request.getParameter("motorbike_freq"))) ;
					profile.setBikeFrequency(Integer.parseInt(request.getParameter("bike_freq"))) ;
					profile.setTrainFrequency(Integer.parseInt(request.getParameter("train_freq"))) ;
					profile.setBusFrequency(Integer.parseInt(request.getParameter("bus_freq"))) ;
					profile.setTaxiFrequency(Integer.parseInt(request.getParameter("taxi_freq"))) ;
					profile.setSubwayFrequency(Integer.parseInt(request.getParameter("subway_freq"))) ;
					profile.setWalkFrequency(Integer.parseInt(request.getParameter("walk_freq"))) ;
					
					profile.setStressLevel(Integer.parseInt(request.getParameter("stress"))) ;
					
					profile.setTrafficDisturbanceLevel(Integer.parseInt(request.getParameter("traffic_disturbance"))) ;
					
					profileId = mainDB.createProfile(dbUtils, con, userId, profile) ;
					
					if (profileId > 0) {
						if (profile.isOwnsCar()) {
							vehicle = new MobisVehicle() ;
							vehicle.setVehicleType(StringConstants.CAR_DISPLAY_TYPE) ;
							vehicle.setCarType(Integer.parseInt(request.getParameter("car_type"))) ;
							vehicle.setVehicleEngine(Double.parseDouble(request.getParameter("car_engine_type"))) ;
							vehicle.setFuelType(Integer.parseInt(request.getParameter("car_fuel_type"))) ;
							
						}
						if (profile.isOwnsMotorbike()) {
							vehicle = new MobisVehicle() ;
							vehicle.setVehicleType(StringConstants.MOTORBYKE_DISPLAY_TYPE) ;
							vehicle.setVehicleEngine(Double.parseDouble(request.getParameter("moto_engine_type"))) ;
							vehicle.setFuelType(Integer.parseInt(request.getParameter("moto_fuel_type"))) ;
						}
						resp.setResponseObject(0);
						out.println(gson.toJson(resp, responseType)) ;
					}
					else {
						resp.setErrorCode(NumericConstants.ERROR_PROFILE_INSERTION) ;
						resp.setErrorMessage(StringConstants.ERROR_PROFILE_INSERTION);
						out.println(gson.toJson(resp, responseType)) ;
					}
				}
				else {
					resp.setErrorCode(NumericConstants.ERROR_NO_LOGIN) ;
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
			e.printStackTrace();
			return ;
		}
	}

}
