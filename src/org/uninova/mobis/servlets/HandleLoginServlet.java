package org.uninova.mobis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uninova.mobis.pojos.MobisResponse;
import org.uninova.mobis.pojos.MobisUser;
import org.uninova.mobis.utils.CredentialUtils;
import org.uninova.mobis.utils.CredentialUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class HandleLoginServlet
 */

public class HandleLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HandleLoginServlet() {
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
		PrintWriter out;
		Type responseType = new TypeToken<MobisResponse<MobisUser>>(){}.getType();
		Gson gson = new Gson() ;
		CredentialUtils credUtils = new CredentialUtilsImpl() ;
		String username = request.getParameter("username") ;
		String password = request.getParameter("password") ;
		
		MobisResponse<MobisUser> resp ;
		
		try {
			response.setContentType("text/html;charset=UTF-8") ;
			out = response.getWriter() ;
			resp = credUtils.handleLogin(username, password) ;
			resp.setErrorCode(200);
			out.println(gson.toJson(resp, responseType)) ;
			out.close() ; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
