package eu.mobis.servlets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
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
import cc.component.types.Info;
import cc.component.types.InfoPacket;

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
		PROFILE("PROFILE"), ANSWER ("ANSWER"), EDIT("EDIT"), DELETE("DELETE"), CALENDAR("CALENDAR"), CHECK_STREAM("CHECK_STREAM"), USER_DATA("USER_DATA"),
		SUGGESTIONS("SUGGESTIONS");

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
				InfoPacket answer = createInfoPacket(user, request, ConversationMethod.ANSWER);
//				ArrayList<Info> l = new ArrayList<>();
//				Info info1 = new Info(new Concept("EnginePowerQuestion"), "2000");
//				l.add(info1);
//				InfoPacket answer = new InfoPacket(user.getUserConcept(), l);
				MobisResponse<Discourse> f = new MobisResponse<Discourse>();
				f.setResponseObject(handleAnswerMethod(user, answer, ConversationMethod.ANSWER));
				resp = f;
				break;
			case EDIT:
				responseType = new TypeToken<MobisResponse<Discourse>>(){
				}.getType();
				InfoPacket edit = createInfoPacket(user, request, ConversationMethod.EDIT);
				MobisResponse<Discourse> e = new MobisResponse<Discourse>();
				e.setResponseObject(handleAnswerMethod(user, edit, ConversationMethod.EDIT));
				resp = e;
				break;
			case DELETE:
				responseType = new TypeToken<MobisResponse<String>>(){}.getType();
				MobisResponse<String> delete = new MobisResponse<String>();
				delete.setResponseObject(handleDeleteMethod(user));
				resp = delete;
				break;
			case CALENDAR:
				responseType = new TypeToken<MobisResponse<Discourse>>(){
				}.getType();
				Info info = new Info(new Concept("CalendarEventLocationQuestion"), "JSI");
				ArrayList<Info> list = new ArrayList<Info>();
				list.add(info);
				InfoPacket packet = new InfoPacket(user.getUserConcept(), list, "department meeting");
				MobisResponse<Discourse> c = new MobisResponse<Discourse>();
				c.setResponseObject(handleCalendarMethod(user, packet, ConversationMethod.CALENDAR));
				resp = c;
				break;
			case CHECK_STREAM:
				responseType = new TypeToken<MobisResponse<String>>(){}.getType();
				MobisResponse<String> check = new MobisResponse<String>();
				check.setResponseObject(handleCheckStreamMethod(user));
				resp = check;
				break;
			case USER_DATA:
				responseType = new TypeToken<MobisResponse<String>>(){}.getType();
				MobisResponse<String> data = new MobisResponse<String>();
				data.setResponseObject(handleUserDataMethod(user));
				resp = data;
				break;
			case SUGGESTIONS:
				responseType = new TypeToken<MobisResponse<Discourse>>(){}.getType();
				InfoPacket venues = createInfoPacket(user, request, ConversationMethod.SUGGESTIONS);
//				InfoPacket venues = new InfoPacket(user.getUserConcept()); 
//				venues.setOnRoute(false);
//				venues.setPosition("46.042285,14.487323");
//				String token = request.getParameter("token");
//				venues.setUserToken(token);
				MobisResponse<Discourse> CC = new MobisResponse<Discourse>();				
				CC.setResponseObject(handleSuggestionsMethod(user, venues, ConversationMethod.SUGGESTIONS));
				resp = CC;
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

		String file = "ontologies/"+user.getUserConcept().toString()+"Ontology.k";
		InputStream personalOnt = CCServlet.class.getClassLoader().getResourceAsStream(file);
		file = "ontologies/"+user.getUserConcept().toString()+"Temp.k";
		InputStream temp = CCServlet.class.getClassLoader().getResourceAsStream(file);
		
		ConversationalComponent testC = new UmkoConversationalComponent(user, personalOnt, temp, null, ConversationMethod.PROFILE);
//		if (testC.initializeForUser(user) == false) {
//			testC.createNewUser(user);
//		}

		Discourse test = testC.getDiscourseForConcept(user.getUserConcept());
		writeToPersonalOnt(user, test.getNewKnowledge());
		writeToTemp(user, test.getTemp());
		
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
	private Discourse handleAnswerMethod(CCUser user, InfoPacket info, ConversationMethod method) throws ReasoningEngineAccessException {
		String file = "ontologies/"+user.getUserConcept().toString()+"Ontology.k";
		InputStream personalOnt = CCServlet.class.getClassLoader().getResourceAsStream(file);
		file = "ontologies/"+user.getUserConcept().toString()+"Temp.k";
		InputStream temp = CCServlet.class.getClassLoader().getResourceAsStream(file);
		ConversationalComponent testF = new UmkoConversationalComponent(user, personalOnt, temp, info, method);
		
		Discourse test = testF.getDiscourseForConcept(user.getUserConcept());
		writeToPersonalOnt(user, test.getNewKnowledge());
		writeToTemp(user, test.getTemp());
		return test;
	}
	
	/**
	 * A method that deletes user's personal ontology file.
	 * @param user
	 */
	private String handleDeleteMethod(CCUser user) {
		try {
			URL resource = CCServlet.class.getClassLoader().getResource("ontologies/"+user.getUserConcept().toString() + "Ontology.k");
			String res= Paths.get(resource.toURI()).toFile().toString();
			File file = new File(res);
			boolean success = file.delete();
			if (!success) {
				LOGGER.severe("Could not delete personal ontology file.");
				return "Could not delete personal ontology file.";
			}
		} catch(NullPointerException e) {
			LOGGER.severe("Could not find personal ontology file or it doesn't exist.");
			return "Could not find personal ontology file or it doesn't exist.";
		} catch (Exception e) {
			LOGGER.severe("Did not delete personal ontology file.");
			return "Did not delete personal ontology file.";
		}
		LOGGER.info("Personal file was deleted.");
		return "Profile was successfully deleted";
	}
	
	/**
	 * A method that handles calendar call - check for the missing data and returns a corresponding Discourse object
	 * @param user
	 * @param _packet
	 * @param method
	 * @return
	 * @throws ReasoningEngineAccessException
	 */
	private Discourse handleCalendarMethod(CCUser user, InfoPacket _packet, ConversationMethod method) throws ReasoningEngineAccessException {		
		String file = "ontologies/"+user.getUserConcept().toString()+"Ontology.k";
		InputStream personalOnt = CCServlet.class.getClassLoader().getResourceAsStream(file);
		file = "ontologies/"+user.getUserConcept().toString()+"Temp.k";
		InputStream temp = CCServlet.class.getClassLoader().getResourceAsStream(file);
		ConversationalComponent testF = new UmkoConversationalComponent(user, personalOnt, temp, _packet, method);
		
		Discourse test = testF.getDiscourseForConcept(user.getUserConcept());
		
		writeToPersonalOnt(user, test.getNewKnowledge());
		writeToTemp(user, test.getTemp());
		return test;
	}
	
	/**
	 * A method that checks if the data is written to user's ontology files - first, it checks if the data can be read, second, it checks if the data can be written.
	 * It makes sense to use a token of a user that has been active before, otherwise there is no data to be read from the file. 
	 * @param user
	 * @return
	 */
	private String handleCheckStreamMethod(CCUser user) {
		String file = "ontologies/"+user.getUserConcept().toString()+"Ontology.k";
		InputStream stream = CCServlet.class.getClassLoader().getResourceAsStream(file);
		if (stream != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));
				String line = br.readLine();
				br.close();				
				
				String write = writeToPersonalOnt(user, line + "\n");
				return "Can read the file. First line of the file: "+ line + ". Trying to write in file... ... " + write; 	
			} catch (IOException e) {
				LOGGER.severe("Could not read the file for some reason. But some data is there.");
				e.printStackTrace();
				String write = writeToPersonalOnt(user, "add concept "+ user.getUserConcept().toString() + "\n");
				return "Could not read the file for some reason. But some data is there."+ ". Trying to write in file... ... " + write;
			}
		}
		else {
			String write = writeToPersonalOnt(user, "add concept "+ user.getUserConcept().toString() + "\n");
			return "the file doesn't exist or is empty."+ ". Trying to write in file... ... " + write;
		}
	}
	
	/**
	 * A method that returns all of the user's (corresponding to the token) existing personal ontology
	 * @param user
	 * @return
	 */
	private String handleUserDataMethod(CCUser user) {
		String file = "ontologies/"+user.getUserConcept().toString()+"Ontology.k";
		InputStream stream = CCServlet.class.getClassLoader().getResourceAsStream(file);
		if (stream != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));
				String line = br.readLine();
				String lines = "";
				while (line != null) {
					lines += " " +line;
					line = br.readLine();
				}
				br.close();		
				return "Can read the file. Data: " + lines;
			} catch (IOException e) {
				LOGGER.severe("Could not read the file for some reason. But some data is there.");
				e.printStackTrace();
				return "Could not read the file for some reason. But some data is there.";
			}
		}
		else {
			return "The file doesn't exist or is empty.";
		}
	}
	
	
	private Discourse handleSuggestionsMethod(CCUser user, InfoPacket info, ConversationMethod method) throws ReasoningEngineAccessException {
		String file = "ontologies/"+user.getUserConcept().toString()+"Ontology.k";
		InputStream personalOnt = CCServlet.class.getClassLoader().getResourceAsStream(file);
		file = "ontologies/"+user.getUserConcept().toString()+"Temp.k";
		InputStream temp = CCServlet.class.getClassLoader().getResourceAsStream(file);
		ConversationalComponent dis = new UmkoConversationalComponent(user, personalOnt, temp, info, method);
		//when to make that method? Scenic route, long journey?
		
		Discourse d = dis.getDiscourseForConcept(user.getUserConcept());
		writeToPersonalOnt(user, d.getNewKnowledge());
		writeToTemp(user, d.getTemp());
		return d;
	}
	
	/**
	 * A method that packs the given information into a InfoPacket object
	 * @param user
	 * @param request
	 * @param method
	 * @return
	 */
	private InfoPacket createInfoPacket(CCUser user, HttpServletRequest request, ConversationMethod method) {
			InfoPacket packet = new InfoPacket(user.getUserConcept());
			ArrayList<Info> infos = new ArrayList<Info>();
			ArrayList<String> IDs = new ArrayList<String>();
			ArrayList<String> answers = new ArrayList<String>();
			Boolean onRoute;
			
			try {
				answers.addAll(Arrays.asList((request.getParameterValues("answer"))));
			} catch (NullPointerException e) {
				LOGGER.info("No answers were sent.");
				try {
					IDs.addAll(Arrays.asList(request.getParameterValues("sentenceId")));	
					Info info = new Info(new Concept(IDs.get(0)));
					infos.add(info);
					} catch (NullPointerException e1) {
						LOGGER.severe("No sentence ID was sent");
					}
				}
		    try {
		    	IDs.addAll(Arrays.asList(request.getParameterValues("sentenceId")));		    
		    } catch(NullPointerException e) {
				LOGGER.severe("No sentence ID was sent");
		    }
	//	    String[] token = request.getParameterValues("token"); 

		    for (String answer : answers) {
		    	Info info = new Info(new Concept(IDs.get(0)), answer); //loop through IDs when answers are from various questions
				infos.add(info);
		    }
			switch(method) {
			case ANSWER: case EDIT: 
				packet.setInformation(infos);
				break;
			case CALENDAR:
				packet.setInformation(infos);
				packet.setEventID(request.getParameterValues("eventID").toString());
				break;
			case SUGGESTIONS:
				onRoute = Boolean.valueOf(request.getParameterValues("onRoute").toString());
				if (onRoute) {
					packet.setOnRoute(onRoute);
					
					ArrayList<String> list = new ArrayList<String>();
			        list.addAll(Arrays.asList(request.getParameterValues("routeJSON")));
					packet.setRouteJSON(list.get(0));
					
					list = new ArrayList<String>();			
			        list.addAll(Arrays.asList(request.getParameterValues("token")));	
					packet.setUserToken(list.get(0));								
				} else {
					packet.setOnRoute(onRoute);
					
					ArrayList<String> list = new ArrayList<String>();
			        list.addAll(Arrays.asList(request.getParameterValues("position")));
					packet.setPosition(list.get(0));
					
					list = new ArrayList<String>();			
			        list.addAll(Arrays.asList(request.getParameterValues("token")));	
					packet.setUserToken(list.get(0));	
				}
				try {
					packet.setInformation(infos); //if there are any
				} catch (NullPointerException e) {
					LOGGER.log(Level.INFO, "No info is sent");
				} try{
					ArrayList<String> list = new ArrayList<String>();
			        list.addAll(Arrays.asList(request.getParameterValues("eventID")));
					packet.setEventID(list.get(0));
					
					list = new ArrayList<String>();			
			        list.addAll(Arrays.asList(request.getParameterValues("topic")));	
					packet.setConvTopic(new Concept(list.get(0)));	
					
				} catch (NullPointerException e) {
					LOGGER.log(Level.INFO, "No eventId or topic is sent");
				}
			default:
				break;
			}
			
			return packet;
	}
	
	/**
	 * A method that writes new knowledge that was obtained through conversation to user's personal ontology file
	 * @param user
	 * @param newKnowledge
	 */
	private String writeToPersonalOnt(CCUser user, String newKnowledge) {
		try {
			URL resource = CCServlet.class.getClassLoader().getResource("ontologies/"+user.getUserConcept().toString() + "Ontology.k");
			String res= Paths.get(resource.toURI()).toFile().toString();
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(res, true)));
			writer.print(newKnowledge);
			writer.close();
			return "Successfully written.";
		} catch (IOException e) {
			LOGGER.severe("Could not write to personal ontology file. Either the ontologies folder doesn't exists or the user is new and doesn't have a file yet.");
			return "Couldn't write to ontology folder";
		} catch (URISyntaxException e) {
			LOGGER.severe("Could not get URL to ontology file. Either the ontologies folder doesn't exists or the user is new and doesn't have a file yet.");
			return "Couldn't write to ontology folder";
		} catch	(NullPointerException e) {
			try {
				URL resource = CCServlet.class.getClassLoader().getResource("/ontologies/");
				String res = Paths.get(resource.toURI()).toFile().toString();
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(res+"/"+user.getUserConcept().toString() + "Ontology.k", true)));
				writer.print(newKnowledge);
				writer.close();
				return "Successfully written.";
			} catch (NullPointerException e1) {
				try {
					LOGGER.severe("Folder doesn't exist (on that path). Check the "
							+ "PATH-TO-WORKSPACE\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\MobisServerV0.2\\WEB-INF\\classes\\"
							+ "and create a folder called \"ontologies\" in it.");		
					URL resource = CCServlet.class.getClassLoader().getResource("/eu/");
					String res = Paths.get(resource.toURI()).toFile().toString();
					String path = res.replaceAll("eu","ontologies");
					Boolean success = (new File(path)).mkdirs();
					if (!success) {
						LOGGER.severe("Folder creation failed. Try to create it manually in:" + res);
						return "Ontologies folder doesn't exist.";
					}
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path+"/"+user.getUserConcept().toString() + "Ontology.k", true)));
					writer.print(newKnowledge);
					writer.close();
					return "Successfully written.";
				} catch (URISyntaxException e2) {
					return "Couldn't write to ontology folder";
				} catch (IOException e2) {
					return "Couldn't write to ontology folder";
				}
			} catch (URISyntaxException e1) {
				LOGGER.severe("Could not get URL to ontology folder.");
				e1.printStackTrace();
				return "Couldn't write to ontology folder";
			} catch (IOException e1) {
				LOGGER.severe("Could not write to personal ontology file.");
				return "Couldn't write to ontology folder";
			}
		}	
	}
	
	/**
	 * A method that writes knowledge that was obtained through conversation to user's personal temp file
	 * @param user
	 * @param newKnowledge
	 */
	private String writeToTemp(CCUser user, String newKnowledge) {
		try {
			URL resource = CCServlet.class.getClassLoader().getResource("ontologies/"+user.getUserConcept().toString() + "Temp.k");
			String res= Paths.get(resource.toURI()).toFile().toString();
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(res, true)));
			writer.print(newKnowledge);
			writer.close();
			return "Successfully written.";
		} catch (IOException e) {
			LOGGER.severe("Could not write to personal ontology file. Either the ontologies folder doesn't exists or the user is new and doesn't have a file yet.");
			return "Couldn't write to ontology folder";
		} catch (URISyntaxException e) {
			LOGGER.severe("Could not get URL to ontology file. Either the ontologies folder doesn't exists or the user is new and doesn't have a file yet.");
			return "Couldn't write to ontology folder";
		} catch	(NullPointerException e) {
			try {
				URL resource = CCServlet.class.getClassLoader().getResource("/ontologies/");
				String res = Paths.get(resource.toURI()).toFile().toString();
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(res+"/"+user.getUserConcept().toString() + "Temp.k", true)));
				writer.print(newKnowledge);
				writer.close();
				return "Successfully written.";
			} catch (NullPointerException e1) {
				try {
					LOGGER.severe("Folder doesn't exist (on that path). Check the "
							+ "PATH-TO-WORKSPACE\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\MobisServerV0.2\\WEB-INF\\classes\\"
							+ "and create a folder called \"ontologies\" in it.");		
					URL resource = CCServlet.class.getClassLoader().getResource("/eu/");
					String res = Paths.get(resource.toURI()).toFile().toString();
					String path = res.replaceAll("eu","ontologies");
					Boolean success = (new File(path)).mkdirs();
					if (!success) {
						LOGGER.severe("Folder creation failed. Try to create it manually in:" + res);
						return "Ontologies folder doesn't exist.";
					}
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path+"/"+user.getUserConcept().toString() + "Temp.k", true)));
					writer.print(newKnowledge);
					writer.close();
					return "Successfully written.";
				} catch (URISyntaxException e2) {
					return "Couldn't write to ontology folder";
				} catch (IOException e2) {
					return "Couldn't write to ontology folder";
				}
			} catch (URISyntaxException e1) {
				LOGGER.severe("Could not get URL to ontology folder.");
				e1.printStackTrace();
				return "Couldn't write to ontology folder";
			} catch (IOException e1) {
				LOGGER.severe("Could not write to personal ontology file.");
				return "Couldn't write to ontology folder";
			}
		}	
	}
	
}// CCServlet
