package org.uninova.mobis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.utils.CredentialUtils;
import org.uninova.mobis.utils.CredentialUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class HandleRegisterServlet
 */

public class HandleRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HandleRegisterServlet() {
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
		CredentialUtils credUtils = new CredentialUtilsImpl() ;
		Type responseType = new TypeToken<MobisResponse<Integer>>(){}.getType();
		PrintWriter out ;
		String firstName = request.getParameter("firstName") ;
		String lastName = request.getParameter("lastName") ;
		String username = request.getParameter("username") ;
		String password = request.getParameter("password") ;
		String email = request.getParameter("email") ;
		String country = request.getParameter("country") ;
		String gToken = request.getParameter("gtoken") ;
		String gRefreshToken = request.getParameter("grefreshtoken") ;
		String gId = request.getParameter("gId") ;
		
		MobisResponse<Integer> resp = credUtils.handleRegister(firstName, lastName, username, password, email, country, gId, gToken, gRefreshToken) ;
		
		try {
			out = response.getWriter() ;
			out.println(gson.toJson(resp, responseType)) ;
			out.close() ; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
{"discourse":
{"discourseObjects":
	[{"typeId":"unknownID",
	  "renderers":
		[{"rendererString":"Hey Luka Bradeško! Welcome back. I've been missing you.",
		  "type":"STRING_RENDERER",
		  "selectedRenderer":-1,
		  "isLink":false}],
	  "choices":
	  	[{"name":"Next",
	  	  "type":"USERS_CHOICE"}],
	  "triggers":
	  	[{"key":"update_user",
	  	  "value":
	  	  	"{\"id\":0,
	  	  	  \"authenticated\":true,
	  	  	  \"sFirstName\":\"Luka\",
	  	  	  \"sLastName\":\"Bradeško\",
	  	  	  \"points\":0,
	  	  	  \"showCycL\":true,
	  	  	  \"developer\":true,
	  	  	  \"prophetToken\":\"312b4aca-9297-4594-81fa-e017c5887978\",
	  	  	  \"email\":\"luka@curiouscat.cc\"}",
	  	  	  "canGoToServer":true}],
	  	  "points":{"totalPoints":-1,"gain":0},
	  	  "isPrivateForOtherUsers":false,
	  	  "isPrivateForCurrentUser":false,
	  	  "sentenceTypeByIntent":"NORMAL",
	  	  "sentenceType":"STATEMENT",
	  	  "sentenceTypeBySource":"CC_FROM_PROPHET",
	  	  "sentenceTypeSentential":"DECLARATIVE",
	  	  "discourseObjects":[]}]},
"meta":{"statusCode":200}}
*/
}
