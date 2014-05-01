package application.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.minidev.json.JSONObject;
import application.controllers.QuestionController;
import application.dao.MySQLDAO;
import application.dto.QuestionsList;
import application.dto.User;
import application.util.PassEncript;
import application.util.TimeEncrpyt;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParamsType;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.util.NamedParamsRetriever;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.getWriter().print("Hallo!");
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//declare rpc objects
		JSONRPC2Request req = null;
		JSONRPC2Response resp = null;
		
		try {
			/*
			 * Had to change this, since after switching to the JS json-rpc lib
			 * the request has no params ("json=" in our case) and the whole payload is the request data itself
			 */
			StringBuffer jb = new StringBuffer();
			String line = null;
			//Getting the request reader in order to load the whole request date into a single string
			BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null) jb.append(line);
			
			//populate rpc objects
			req = JSONRPC2Request.parse(jb.toString());
			resp = new JSONRPC2Response(req.getID());
			
			//define jsonResult
			JSONObject jsonResult = new JSONObject();
			
			//retrieve request information
			String method = req.getMethod();
			System.out.println(req.getMethod());
			
			//find the requested method
			if (method.equals("login"))
			{
				//login user
				jsonResult = loginUser(request, response, req);
				System.out.println(jsonResult.toString());
				resp.setResult(jsonResult.toJSONString());
			}
			else if (method.equals("register"))
			{
				//register user
				//TODO duplicates!
				jsonResult = registerUser(req, resp);
				System.out.println(jsonResult.toString());
				resp.setResult(jsonResult.toJSONString());
			}
			else if (isLoggedIn(request))
			{
				if (method.equals("logout")) {
					//logout user
					logoutUser(request);
					resp.setResult("Logout successful");
				}
				else if (method.equals("loadAllQuestions"))
				{
					//return all questions as QuestionsList
					QuestionsList questions = QuestionController.loadAllQuestions();
					if (questions != null)
					{
						JSONObject jsonQuestions = new JSONObject();
						jsonQuestions.put("allQuestions", questions);
						resp.setResult(jsonQuestions.toJSONString());
					}
					else
					{
						resp.setError(JSONRPC2Error.INTERNAL_ERROR);
					}
				}
				else if(method.equals("addQuestion"))
				{
					//add question
					resp = addQuestion(req, request);
				}
				else if(method.equals("deleteQuestion"))
				{
					//delete question
					resp = deleteQuestion(req);
				}
				else if(method.equals("udpateQuestion"))
				{
					//update question
					resp = updateQuestion(req);
				}
				/*
				JSONObject json = new JSONObject();
				json.put("hi", "opa");
				resp.setResult(json.toJSONString());
				*/
			}
		} catch(Throwable t) {
			JSONRPC2Error error = new JSONRPC2Error(1, t.getMessage());
			
			resp.setError(error);
		} finally {
			//dispatch result
			response.getWriter().print(resp.toJSONObject().toJSONString());
		}
	}

	public JSONObject registerUser(JSONRPC2Request request, JSONRPC2Response response) {
		
		JSONRPC2ParamsType paramsType = request.getParamsType();
		Map<String,Object> params = request.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		JSONObject jsonRegistration = new JSONObject();
		try {
			//create new user
			User u = new User();
			
			//set params
			u.setName(np.getString("name"));
			u.setEmail(np.getString("email"));
			u.setPassword(PassEncript.PassHash(np.getString("password")));
			
			//add user
			MySQLDAO dao = new MySQLDAO();
			dao.insertUser(u);
			
			
			jsonRegistration.put("name", u.getName());
			jsonRegistration.put("email", u.getEmail());
			jsonRegistration.put("userId", u.getId());
			return jsonRegistration;
		} catch (Exception e) {
			e.printStackTrace();
			jsonRegistration.put("message", "error occured");
			return jsonRegistration;
		}
		
	}
	
	public JSONObject loginUser(HttpServletRequest request, HttpServletResponse response, JSONRPC2Request jsonReq) throws JSONRPC2Error
	{
		//define new json for the result
		JSONObject jsonLogin = new JSONObject();
		//get user info
		Map<String,Object> params = jsonReq.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		
		try {

			String password = PassEncript.PassHash(np.getString("password"));
			System.out.println("input pass " + password);
			
			String email = np.getString("email");
			
			//establish connection
		
			MySQLDAO dao = new MySQLDAO();
			
			//get user by email
			User u = new User();
			u = dao.loadUser(email);
			
			if (!(u.getEmail().equals(email)))
			{
				jsonLogin.put("message", "user doesn't exist");
				return jsonLogin;
			}
			else
			{
				//check if password is correct
				if (u.getPassword().equals(password))
				{
					//create sesssion and cookies
					HttpSession session = request.getSession();
					session.setAttribute("pass", TimeEncrpyt.TimeHash());
					session.setAttribute("user", email);
					session.setMaxInactiveInterval(30*60);
					Cookie pass = new Cookie("pass", TimeEncrpyt.TimeHash());
					pass.setMaxAge(30*60);
					response.addCookie(pass);
					
					//return result
					jsonLogin.put("userId", u.getId());
					jsonLogin.put("name", u.getName());
					jsonLogin.put("email", email);
					return jsonLogin;
				}
				else
				{
					jsonLogin.put("message", "incorrect password");
					return jsonLogin;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject jsonError = new JSONObject();
			jsonError.put("error", "error occured");
			return jsonError;
		}
		
	}
	
	public User getCurrentUser(HttpServletRequest request)
	{
		//get session user
		HttpSession session = request.getSession();
		String userMail = (String) session.getAttribute("user");
		try {
			MySQLDAO dao = new MySQLDAO();
			User u = new User();
			u = dao.loadUser(userMail);
			return u;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isLoggedIn(HttpServletRequest request)
	{
		//get session time stamp
		HttpSession session = request.getSession();
		String sessionPass = (String) session.getAttribute("pass");
		//get cookies
		Cookie[] cookies = request.getCookies();
		//search cookies for match
		if(cookies != null)
		{
			for(Cookie cookie : cookies)
			{
				if(cookie.getValue().equals(sessionPass))
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			return false;
		}
	}
	
	public void logoutUser(HttpServletRequest request)
	{
		//get session time stamp
		HttpSession session = request.getSession();
		String sessionPass = (String) session.getAttribute("pass");
		//get cookies
		Cookie[] cookies = request.getCookies();
		//search cookies for match and delete cookie if found
		if(cookies != null)
			{
				for(Cookie cookie : cookies)
				{
					if(cookie.getValue().equals(sessionPass))
					{
						cookie.setMaxAge(0);
					}
				}
			}
		//invalidate session
		request.getSession().invalidate();
	}
	
	public JSONRPC2Response addQuestion(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error
	{
		//get question
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		String body = np.getString("body");
		String answer = np.getString("answer");
		
		//get user
		User u = getCurrentUser(request);
		int userID = (int) u.getId();
		
		//return ID of Question if added successfully
		int qid = QuestionController.addQuestion(body, answer, userID);
		if (qid < 0)
		{
			return new JSONRPC2Response("Question added");
		}
		else
		{
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, req.getID());
		}
	}
	
	public JSONRPC2Response deleteQuestion(JSONRPC2Request request) throws JSONRPC2Error
	{
		//get question id
		Map<String,Object> params = request.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		int questionId = np.getInt("id");
		
		//remove question from database
		boolean questionDeleted = false;
		questionDeleted = QuestionController.deleteQuestion(questionId);
		
		//send result
		if(questionDeleted)
		{
			return new JSONRPC2Response("Question deleted");
		}
		else
		{
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, request.getID());
		}
	}
	
	public JSONRPC2Response updateQuestion(JSONRPC2Request request) throws JSONRPC2Error
	{
		//get question
		Map<String,Object> params = request.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		int questionId = np.getInt("id");
		String questionBody = np.getString("body");
		String questionAnswer = np.getString("answer");
		
		//update question in database
		boolean questionUpdated = false;
		questionUpdated = QuestionController.updateQuestion(questionId, questionBody, questionAnswer);
		
		//return result
		if(questionUpdated)
		{
			return new JSONRPC2Response("Question updated");
		}
		else
		{
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, request.getID());
		}
	}
}
