package eu.mobis.servlets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
import cc.component.UmkoConversationalComponent.ConversationMethod;
import cc.component.exceptions.ReasoningEngineAccessException;
import cc.component.types.CCUser;
import cc.component.types.Concept;
import cc.component.types.Discourse;
import cc.component.types.Feedback;

import com.google.api.client.http.HttpRequest;
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
		PROFILE("PROFILE"), ANSWER ("ANSWER"), EDIT("EDIT"), DELETE("DELETE");

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

	/**
	 * A method that finds the user in Mobis database, identifies CCMethod and creates a GSON response corresponding to each method
	 * @param request
	 * @param response
	 */
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
				Feedback feedback = createFeedback(user, request);
				MobisResponse<Discourse> f = new MobisResponse<Discourse>();
				f.setResponseObject(handleAnswerMethod(user, feedback, ConversationMethod.ANSWER));
				resp = f;
				break;
			case EDIT:
				responseType = new TypeToken<MobisResponse<Discourse>>(){
				}.getType();
				Feedback edit = createFeedback(user, request);
				MobisResponse<Discourse> e = new MobisResponse<Discourse>();
				e.setResponseObject(handleAnswerMethod(user, edit, ConversationMethod.EDIT));
				resp = e;
				break;
			case DELETE:
				handleDeleteMethod(user);
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

	/**
	 * A method that initializes Umko in case method=PROFILE and returns initial Discourse 
	 * @param user
	 * @return
	 * @throws ReasoningEngineAccessException
	 * @throws URISyntaxException 
	 */
	private Discourse handleProfileMethod(CCUser user) throws ReasoningEngineAccessException {
		String file = user.getUserConcept().toString()+"Ontology.k";
		InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
		
		ConversationalComponent testC = new UmkoConversationalComponent(user, stream);
//		if (testC.initializeForUser(user) == false) {
//			testC.createNewUser(user);
//		}

		Discourse test = testC.getDiscourseForConcept(user.getUserConcept());
		try {
			URL resource = CCServlet.class.getClassLoader().getResource(user.getUserConcept().toString() + "Ontology.k");
			String res= Paths.get(resource.toURI()).toFile().toString();
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(res+user.getUserConcept().toString() + "Ontology.k", true)));
//			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\zala\\workspaceEE_MobisServer\\MobisServerV0.1\\res\\"+user.getUserConcept().toString() + "Ontology.k", true))); 
			writer.print(test.getNewKnowledge());
			writer.close();
		} catch (IOException e) {
			LOGGER.severe("Could not write to personal ontology file.");
		} catch (URISyntaxException e) {
			LOGGER.severe("Could not get URL to ontology file");
		} catch	(NullPointerException e) {
			try {
				URL resource = CCServlet.class.getClassLoader().getResource("/");
				String res;
				res = Paths.get(resource.toURI()).toFile().toString();
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(res+user.getUserConcept().toString() + "Ontology.k", true))); //TODO not working as it should
				writer.print(test.getNewKnowledge());
				writer.close();
			} catch (URISyntaxException e1) {
				LOGGER.severe("Could not get URL to ontology folder");
				e1.printStackTrace();
			} catch (IOException e1) {
				LOGGER.severe("Could not write to personal ontology file.");
			}
		}	
		return test;
	}
	
	/**
	 * A method that asserts user's answer to Umko and returns new Discourse object
	 * @param user
	 * @param feedback
	 * @param method
	 * @return
	 * @throws ReasoningEngineAccessException
	 */
	private Discourse handleAnswerMethod(CCUser user, Feedback feedback, ConversationMethod method) throws ReasoningEngineAccessException {
		String file = user.getUserConcept().toString()+"Ontology.k";
		InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
		ConversationalComponent testF = new UmkoConversationalComponent(user, stream, feedback, method);
		
		Discourse test = testF.getDiscourseForConcept(user.getUserConcept());
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\zala\\workspaceEE_MobisServer\\MobisServerV0.1\\res\\"+user.getUserConcept().toString() + "Ontology.k", true)));
			writer.print(test.getNewKnowledge());
			writer.close();
		} catch (IOException e) {
			LOGGER.severe("Could not write to personal ontology file.");
		}	
		return test;
	}
	
	private void handleDeleteMethod(CCUser user) {
		try {
			File file = new File("C:\\Users\\zala\\workspaceEE_MobisServer\\MobisServerV0.1\\res\\"+user.getUserConcept().toString() + "Ontology.k");
			boolean success = file.delete();
			if (!success) {
				LOGGER.severe("Could not delete personal ontology file.");
			}
		} catch (Exception e) {
			LOGGER.severe("Did not delete personal ontology file.");
		}
		
		
	}
	
	/**
	 * A methods that creater a feedback object from http request parameters
	 * @param user
	 * @param _sentenceId
	 * @param _answer
	 * @return
	 */
	private Feedback createFeedback(CCUser user, HttpServletRequest request) {
		ArrayList<String> list = new ArrayList<String>();
	    list.addAll(Arrays.asList((request.getParameterValues("answer"))));
	    String answer = list.get(0);
	    
	    list = new ArrayList<String>();
	    list.addAll(Arrays.asList(request.getParameterValues("sentenceId")));
	    String sententeId = list.get(0);
	    
//	    String[] token = request.getParameterValues("token"); 
	    
		Feedback feedback = new Feedback(user.getUserConcept(), new Concept(sententeId), answer);
		return feedback;
	}
	
	
}// CCServlet
