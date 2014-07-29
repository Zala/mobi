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
import org.uninova.mobis.pojos.MobisProfile;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisVehicle;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class HandleProfileAnswerServlet
 */

public class HandleProfileAnswerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final String PARAM_GPS_DEVICE = "gps_device";
    private static final String PARAM_GPS_SERVICE = "gps_service" ;
    private static final String PARAM_TRIP_PLANNER = "trip_planner" ;
    private static final String PARAM_PAPER_MAP = "paper_map" ;
    private static final String PARAM_OTHER_ASSISTANT = "other_trip_assistant" ;
    private static final String PARAM_OWNS_CAR = "owns_car" ;
    private static final String PARAM_OWNS_MOTORBIKE = "owns_motorbike" ;
    private static final String PARAM_OWNS_BIKE = "owns_bike" ;
    private static final String PARAM_HANDICAPPED = "handicapped" ;
    private static final String PARAM_DISABLE_CAR = "disable_car" ;
    private static final String PARAM_DISABLE_MOTORBIKE = "disable_motorbike" ;
    private static final String PARAM_DISABLE_BIKE = "disable_bike" ;
    private static final String PARAM_DISABLE_TRAIN = "disable_train" ;
    private static final String PARAM_DISABLE_BUS = "disable_bus" ;
    private static final String PARAM_DISABLE_TAXI = "disable_taxi" ;
    private static final String PARAM_DISABLE_SUBWAY = "disable_subway" ;
    private static final String PARAM_DISABLE_WALK = "disable_walk" ;
    private static final String PARAM_CAR_FREQ = "car_freq" ;
    private static final String PARAM_MOTORBIKE_FREQ = "motorbike_freq" ;
    private static final String PARAM_BIKE_FREQ = "bike_freq" ;
    private static final String PARAM_TRAIN_FREQ = "train_freq" ;
    private static final String PARAM_BUS_FREQ = "bus_freq" ;
    private static final String PARAM_TAXI_FREQ = "taxi_freq" ;
    private static final String PARAM_SUBWAY_FREQ = "subway_freq" ;
    private static final String PARAM_WALK_FREQ = "walk_freq" ;
    private static final String PARAM_STRESS = "stress" ;
    private static final String PARAM_TRAFFIC_DISTURBANCE = "traffic_disturbance" ;
    private static final String PARAM_CAR_TYPE = "car_type" ;
    private static final String PARAM_CAR_ENGINE_TYPE = "car_engine_type" ;
    private static final String PARAM_CAR_FUEL_TYPE = "car_fuel_type" ;
    private static final String PARAM_MOTO_ENGINE_TYPE = "moto_engine_type" ;
    private static final String PARAM_MOTO_FUEL_TYPE = "moto_fuel_type" ;
    private static final String PARAM_QUESTION_ID = "question_id" ;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HandleProfileAnswerServlet() {
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
		int questionId = Integer.parseInt(request.getParameter(PARAM_QUESTION_ID)) ;
		String token = request.getParameter("token") ;
		HttpSession session = request.getSession() ;
		Gson gson = new Gson() ;
		Type responseType = new TypeToken<MobisResponse<MobisProfile>>(){}.getType();
		MobisResponse<MobisProfile> resp = new MobisResponse<>() ;
		MobisProfile profile = (MobisProfile) session.getAttribute("lastProfile") ;
		MobisMainDBConnector mainConnector = new MobisMainDBConnectorImpl() ;
		DBUtils dbUtils = new DBUtilsImpl() ;
		Connection con ;
		Long userId ;
		PrintWriter out ;
		MobisVehicle vehicle = null ;
		ArrayList<MobisVehicle> vehicles ;
		
		try {
			out = response.getWriter() ;
			response.setContentType("text/html;charset=UTF-8");
			
			if (token != null && !token.equals("")) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN, StringConstants.JDBC_POSTGRES) ;
				userId = mainConnector.getUserIdFromToken(dbUtils, con, token) ;
				if (userId != null && userId > 0) {
					switch (questionId) {
					case 1: 
						if (profile == null) {
							profile = new MobisProfile() ;
						}
						profile.setGpsDevice(Boolean.parseBoolean(request.getParameter(PARAM_GPS_DEVICE))) ;
						profile.setGpsService(Boolean.parseBoolean(request.getParameter(PARAM_GPS_SERVICE))) ;
						profile.setTripPlanner(Boolean.parseBoolean(request.getParameter(PARAM_TRIP_PLANNER))) ;
						profile.setPaperMap(Boolean.parseBoolean(request.getParameter(PARAM_PAPER_MAP))) ;
						profile.setOtherAssistant((request.getParameter(PARAM_OTHER_ASSISTANT) != null && !request.getParameter(PARAM_OTHER_ASSISTANT).equals("")) ? request.getParameter(PARAM_OTHER_ASSISTANT) : "") ;
						break ;
					case 2: 
						profile.setOwnsCar(Boolean.parseBoolean(request.getParameter(PARAM_OWNS_CAR))) ;
						profile.setOwnsMotorbike(Boolean.parseBoolean(request.getParameter(PARAM_OWNS_MOTORBIKE))) ;
						profile.setOwnsBike(Boolean.parseBoolean(request.getParameter(PARAM_OWNS_BIKE))) ;
						break ;
					case 3: 
						profile.setHandicapped(Boolean.parseBoolean(request.getParameter(PARAM_HANDICAPPED))) ;
						profile.setDisableCar(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_CAR))) ;
						profile.setDisableMotorbike(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_MOTORBIKE))) ;
						profile.setDisableBike(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_BIKE))) ;
						profile.setDisableTrain(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_TRAIN))) ;
						profile.setDisableBus(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_BUS))) ;
						profile.setDisableTaxi(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_TAXI))) ;
						profile.setDisableSubway(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_SUBWAY))) ; 
						profile.setDisableWalk(Boolean.parseBoolean(request.getParameter(PARAM_DISABLE_WALK))) ;
						break ;
					case 4: 
						profile.setCarFrequency(Integer.parseInt(request.getParameter(PARAM_CAR_FREQ))) ;
						profile.setMotorbikeFrequency(Integer.parseInt(request.getParameter(PARAM_MOTORBIKE_FREQ))) ;
						profile.setBikeFrequency(Integer.parseInt(request.getParameter(PARAM_BIKE_FREQ))) ;
						profile.setTrainFrequency(Integer.parseInt(request.getParameter(PARAM_TRAIN_FREQ))) ;
						profile.setBusFrequency(Integer.parseInt(request.getParameter(PARAM_BUS_FREQ))) ;
						profile.setTaxiFrequency(Integer.parseInt(request.getParameter(PARAM_TAXI_FREQ))) ;
						profile.setSubwayFrequency(Integer.parseInt(request.getParameter(PARAM_SUBWAY_FREQ))) ;
						profile.setWalkFrequency(Integer.parseInt(request.getParameter(PARAM_WALK_FREQ))) ;
						break ;
					case 5: 
						profile.setStressLevel(Integer.parseInt(request.getParameter(PARAM_STRESS))) ;
						break ;
					case 6: 
						profile.setTrafficDisturbanceLevel(Integer.parseInt(request.getParameter(PARAM_TRAFFIC_DISTURBANCE))) ;
						break ;
					case 7: 
						vehicle = new MobisVehicle() ;
						vehicle.setVehicleType(StringConstants.CAR_DISPLAY_TYPE) ;
						vehicle.setCarType(Integer.parseInt(request.getParameter(PARAM_CAR_TYPE))) ;
						vehicles = new ArrayList<>() ;
						vehicles.add(vehicle) ;
						profile.setVehicles(vehicles) ;
						break ;
					case 8: 
						vehicles = profile.getVehicles() ;
						for (MobisVehicle _vehicle : vehicles) {
							if (_vehicle.getVehicleType().equals(StringConstants.CAR_DISPLAY_TYPE)) {
								vehicle = _vehicle ;
							}
						}
						vehicle.setVehicleEngine(Double.parseDouble(request.getParameter(PARAM_CAR_ENGINE_TYPE))) ;
						vehicles = new ArrayList<>() ;
						vehicles.add(vehicle) ;
						profile.setVehicles(vehicles) ;
						break ;
					case 9: 
						vehicles = profile.getVehicles() ;
						for (MobisVehicle _vehicle : vehicles) {
							if (_vehicle.getVehicleType().equals(StringConstants.CAR_DISPLAY_TYPE)) {
								vehicle = _vehicle ;
							}
						}
						vehicle.setFuelType(Integer.parseInt(request.getParameter(PARAM_CAR_FUEL_TYPE))) ;
						vehicles = new ArrayList<>() ;
						vehicles.add(vehicle) ;
						profile.setVehicles(vehicles) ;
						break ;
					case 10: 
						vehicle = new MobisVehicle() ;
						vehicle.setVehicleType(StringConstants.MOTORBYKE_DISPLAY_TYPE) ;
						vehicle.setVehicleEngine(Double.parseDouble(request.getParameter(PARAM_MOTO_ENGINE_TYPE))) ;
						vehicles = profile.getVehicles() ;
						vehicles.add(vehicle) ;
						profile.setVehicles(vehicles) ;
						break ;
					case 11: 
						vehicles = profile.getVehicles() ;
						for (MobisVehicle _vehicle : vehicles) {
							if (_vehicle.getVehicleType().equals(StringConstants.MOTORBYKE_DISPLAY_TYPE)) {
								vehicle = _vehicle ;
							}
						}
						vehicles.remove(vehicle) ;
						vehicle.setFuelType(Integer.parseInt(request.getParameter(PARAM_MOTO_FUEL_TYPE))) ;
						vehicles.add(vehicle) ;
						profile.setVehicles(vehicles); 
						break ;
					}
					profile = mainConnector.insertProfileQuestion(dbUtils, con, userId, questionId, profile) ;
					session.removeAttribute("lastProfile") ;
					session.setAttribute("lastProfile", profile) ;
					resp.setResponseObject(profile) ;
					out.println(gson.toJson(resp, responseType)) ;
				}
				else {
					resp.setErrorCode(NumericConstants.ERROR_NO_LOGIN);
					resp.setErrorMessage(StringConstants.ERROR_NO_LOGIN);
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
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		return ;
	}
}
