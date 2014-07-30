package eu.mobis.servlets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uninova.mobis.constants.NumericConstants;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.MobisMainDBConnector;
import org.uninova.mobis.database.MobisMainDBConnectorImpl;
import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;

//import cc.component.ConversationalComponent;
//import cc.component.exceptions.ReasoningEngineAccessException;
//import cc.component.types.CCUser;
//import cc.component.types.Discourse;






import cc.component.ConversationalComponent;
import cc.component.UmkoConversationalComponent;
import cc.component.exceptions.ReasoningEngineAccessException;
import cc.component.types.CCUser;
import cc.component.types.Concept;
import cc.component.types.Discourse;
import cc.component.types.Feedback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.mobis.servlets.exceptions.MobisServletException;

/**
 * Servlet handling Conversational Component API calls
 */
public class CCServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(CCServlet.class.getName());

	MobisMainDBConnector mainDB = new MobisMainDBConnectorImpl();// TODO: Join the DB Utils and
																						// MainDB
	DBUtils dbUtils = new DBUtilsImpl();
	Gson gson = new Gson();

	public enum CCMethod {
		PROFILE("PROFILE"), ANSWER ("ANSWER");

		private static HashMap<String, CCMethod> methods = new HashMap<>();
		private String name;

		static {
			for (CCMethod entry : values()) {
				methods.put(entry.name, entry);
			}
		}

		CCMethod(String _name) {
			name = _name;
		}

		@Override
		public String toString() {
			return name;
		}

		public static CCMethod parseMethod(String _methodName) {
			return methods.get(_methodName);
		}
	}// CCMethod ENUM

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CCServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) {
		Connection con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_MAIN,
				StringConstants.JDBC_POSTGRES);

		Type responseType = new TypeToken<MobisResponse<?>>() {
		}.getType();
		PrintWriter out = null;
		MobisResponse<?> resp = new MobisResponse<>();
		try {
			out = response.getWriter();
			CCUser user = loadUser(request, con);
			CCMethod method = identifyCCMethod(request);

			switch (method) {
			case PROFILE:
				responseType = new TypeToken<MobisResponse<Discourse>>() {
				}.getType();
				MobisResponse<Discourse> r = new MobisResponse<Discourse>();
				r.setResponseObject(handleProfileMethod(user));
				resp = r;
				break;
			case ANSWER:
				responseType = new TypeToken<MobisResponse<Discourse>>(){
				}.getType();
				Feedback feedback = new Feedback(user.getUserConcept(), new Concept("TripAssistanceDeviceQuestion"), "GoogleMaps"); //TODO
				MobisResponse<Discourse> f = new MobisResponse<Discourse>();
				f.setResponseObject(handleAnswer(user, feedback));
				resp = f;
				break;
			}

			out.println(gson.toJson(resp, responseType));
			con.close();
			out.close();
			return;
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} catch (ReasoningEngineAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.setErrorCode(NumericConstants.ERROR_CC);
			resp.setErrorMessage(StringConstants.ERROR_CC_REASONING_ENGINE_NOT_FOUND + ": "
					+ e.getMessage());
			out.println(gson.toJson(resp, responseType));
		} catch (MobisServletException e) {
			resp.setErrorCode(NumericConstants.ERROR_CC);
			resp.setErrorMessage(e.getMessage());
			out.println(gson.toJson(resp, responseType));
		}
	}

	private CCUser loadUser(HttpServletRequest request, Connection con) throws MobisServletException {
		String token = request.getParameter("token");
		LOGGER.info("Finding the user in the DB. Token: " + token);
		long userId = mainDB.getUserIdFromToken(dbUtils, con, token);
		if (userId == -1)
			throw new MobisServletException("Unauthenticated User. Attach proper token to the url.");
		return new CCUser(userId);
	}

	private CCMethod identifyCCMethod(HttpServletRequest request) throws MobisServletException {

		// get the method name from the request
		String sRequest = request.getPathInfo().toUpperCase();
		System.out.println("identify method in " + sRequest);
		int iHashIndex = sRequest.lastIndexOf("/");
		if (iHashIndex == -1) {
			LOGGER.severe("No API Method in the url request: " + request.toString());
			throw new MobisServletException("Expecting one of the Prophet methods in the request. "
					+ "See API documentation");
		}
		// int iHashIndex2 = sRequest.lastIndexOf("?");
		// String sProphetMethod = "unknown";
		// if (iHashIndex2 == -1)
		String sProphetMethod = sRequest.substring(++iHashIndex);
		// else
		// sProphetMethod = sRequest.substring(++iHashIndex, iHashIndex2);

		// LOGGER.finest("parsed the method " + sProphetMethod
		// + "from the request");
		System.out.println("parsed the method " + sProphetMethod + "from the request" + sRequest);

		CCMethod ccmethod = CCMethod.parseMethod(sProphetMethod);
		if (ccmethod == null)
			throw new MobisServletException("Wrong method: " + sProphetMethod);
		return ccmethod;
	}

	private Discourse handleProfileMethod(CCUser user) throws ReasoningEngineAccessException {
		String file = user.getUserConcept().toString()+"Ontology.k";
		InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
		
		ConversationalComponent testC = new UmkoConversationalComponent(user, stream);
//		if (testC.initializeForUser(user) == false) {
//			testC.createNewUser(user);
//		}

		Discourse test = testC.getDiscourseForConcept(user.getUserConcept());
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\zala\\workspaceEE_MobisServer\\MobisServerV0.1\\res\\"+user.getUserConcept().toString() + "Ontology.k", true)));
			writer.print(test.getNewKnowledge());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return test;
	}
	
	private Discourse handleAnswer(CCUser user, Feedback feedback) throws ReasoningEngineAccessException {
		String file = user.getUserConcept().toString()+"Ontology.k";
		InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
		ConversationalComponent testF = new UmkoConversationalComponent(user, stream, feedback);
		
		Discourse test = testF.getDiscourseForConcept(user.getUserConcept());
		
		return test;
	}
}// CCServlet
