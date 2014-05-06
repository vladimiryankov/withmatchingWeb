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
import application.controllers.TestController;
import application.dao.MySQLDAO;
import application.dto.QuestionsList;
import application.dto.TestsList;
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
				System.out.println("json result: " + jsonResult.toString());
				resp.setResult(jsonResult.toJSONString());
			}
			else if (method.equals("register"))
			{
				//register user
				//TODO duplicates!
				jsonResult = registerUser(req, resp);
				System.out.println("json result to string: " + jsonResult.toString());
				resp.setResult(jsonResult.toJSONString());
			}
			else if (isLoggedIn(request))
			{
				if (method.equals("logout")) {
					//logout user
					jsonResult = logoutUser(request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult.toJSONString());
				}
				else if (method.equals("loadAllQuestions"))
				{
					//return all questions as QuestionsList
					QuestionsList questions = QuestionController.loadAllQuestions();
					if (questions != null)
					{
						JSONObject jsonQuestions = new JSONObject();
						jsonQuestions.put("allQuestions", questions);
						System.out.println("jsonQuestions: " + jsonQuestions.toString());
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
					jsonResult = addQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult.toJSONString());
				}
				else if(method.equals("deleteQuestion"))
				{
					//delete question
					jsonResult = deleteQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult.toJSONString());
				}
				else if(method.equals("udpateQuestion"))
				{
					//update question
					jsonResult = updateQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult.toJSONString());
				}
				else if (method.equals("loadAllTests"))
				{
					//return all questions as QuestionsList
					TestsList tests = TestController.loadAllTests();
					if (tests != null)
					{
						JSONObject jsonTests = new JSONObject();
						jsonTests.put("allTests", tests);
						System.out.println("jsonTests: " + jsonTests.toString());
						resp.setResult(jsonTests.toJSONString());
					}
					else
					{
						resp.setError(JSONRPC2Error.INTERNAL_ERROR);
					}
				}
				else if(method.equals("addTest"))
				{
					//add question
					jsonResult = addTest(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult.toJSONString());
				}
				else if(method.equals("deleteTest"))
				{
					//delete question
					jsonResult = deleteTest(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult.toJSONString());
				}
				else if(method.equals("updateTest"))
				{
					//update question
					jsonResult = updateTest(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult.toJSONString());
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
			System.out.println("final result: " + resp.toJSONObject().toJSONString());
		}
	}

	private JSONObject updateTest(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error {
		JSONObject jsonUpdateTest = new JSONObject();
		
		//get question
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		int testId = np.getInt("id");
		String testName = np.getString("name");
		int tOwnerId = np.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check for privilleges
		if	(u.getId() == tOwnerId)
		{
			//update question in database
			int tidUpdated = TestController.updateTest(testId, testName);
			
			//return result
			if(tidUpdated > -1 && tidUpdated == testId)
			{
				jsonUpdateTest.put("message", "test updated");
				jsonUpdateTest.put("id", tidUpdated);
				jsonUpdateTest.put("name", testName);
				return jsonUpdateTest;
			}
			else
			{
				jsonUpdateTest.put("error", "error occured updating test");
				return jsonUpdateTest;
			}
		}
		else
		{
			jsonUpdateTest.put("error", "the user has no privilleges");
			return jsonUpdateTest;
		}
		
	}

	private JSONObject deleteTest(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error {
		//create json object for the result
		JSONObject jsonDeleteTest = new JSONObject();
		
		//get question id
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		int testId = np.getInt("id");
		int tOwnerId = np.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check for privilleges
		if	(u.getId() == tOwnerId)
		{

			//remove question from database
			int testDeletedId;
			testDeletedId = TestController.deleteTest(testId);
			
			//send result
			if(testDeletedId == testId)
			{
				jsonDeleteTest.put("message", "test deleted");
				jsonDeleteTest.put("testId", testId);
				return jsonDeleteTest;
			}
			else
			{
				jsonDeleteTest.put("error", "error occured deleting test");
				return jsonDeleteTest;
			}
		}
		else
		{
			jsonDeleteTest.put("error", "user has no privilleges");
			return jsonDeleteTest;
		}
	}

	private JSONObject addTest(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error {
				//json object for the result
				JSONObject jsonAddTest = new JSONObject();
				
				//get question
				Map<String,Object> params = req.getNamedParams();
				NamedParamsRetriever np = new NamedParamsRetriever(params);
				String name = np.getString("name");
				
				//get user
				User u = getCurrentUser(request);
				int userID = (int) u.getId();
				
				//return ID of Question if added successfully
				int tid = TestController.addTest(name, userID);
				if (tid > -1)
				{
					jsonAddTest.put("id", tid);
					jsonAddTest.put("body", name);
					return jsonAddTest;
				}
				else
				{
					jsonAddTest.put("error", "error occured adding the test");
					return jsonAddTest;
				}
	}

	public JSONObject registerUser(JSONRPC2Request request, JSONRPC2Response response) {
		
		@SuppressWarnings("unused")
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
			
			//it is a good practice to wrap the result object in a named property
			//in our case a user property
			JSONObject userJSON = new JSONObject();
			//instead of doing this every time this way
			//it would be much cleaner to have a toJSONObject method in the user class
			userJSON.put("name", u.getName());
			userJSON.put("email", u.getEmail());
			userJSON.put("userId", u.getId());
			//put the whole user json as user property in the result
			jsonRegistration.put("user", userJSON);
			
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
					System.out.println("session pass: " + session.getAttribute("pass").toString());
					session.setAttribute("user", email);
					System.out.println("session user: " + session.getAttribute("user").toString());
					session.setMaxInactiveInterval(30*60);
					Cookie pass = new Cookie("pass", TimeEncrpyt.TimeHash());
					System.out.println("cookie pass: " + pass.getValue().toString());
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
	
	public JSONObject logoutUser(HttpServletRequest request)
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
		
		//send result
		JSONObject jsonLogout = new JSONObject();
		jsonLogout.put("sessionOver", session);
		return jsonLogout;
	}
	
	public JSONObject addQuestion(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error
	{
		//json object for the result
		JSONObject jsonAddQuestion = new JSONObject();
		
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
		if (qid > -1)
		{
			jsonAddQuestion.put("id", qid);
			jsonAddQuestion.put("body", body);
			jsonAddQuestion.put("answer", answer);
			return jsonAddQuestion;
		}
		else
		{
			jsonAddQuestion.put("error", "error occured adding the question");
			return jsonAddQuestion;
		}
	}
	
	public JSONObject deleteQuestion(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error
	{
		//create json object for the result
		JSONObject jsonDeleteQuestion = new JSONObject();
		
		//get question id
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		int questionId = np.getInt("id");
		int qOwnwerId = np.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check for user privilleges
		if	(u.getId() == qOwnwerId)
		{

			//remove question from database
			boolean questionDeleted = false;
			questionDeleted = QuestionController.deleteQuestion(questionId);
			
			//send result
			if(questionDeleted)
			{
				jsonDeleteQuestion.put("message", "question deleted");
				jsonDeleteQuestion.put("questionId", questionId);
				return jsonDeleteQuestion;
			}
			else
			{
				jsonDeleteQuestion.put("error", "error occured deleting question");
				return jsonDeleteQuestion;
			}
		}
		else
		{
			jsonDeleteQuestion.put("error", "user has no privilleges");
			return jsonDeleteQuestion;
		}
	}
	
	public JSONObject updateQuestion(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error
	{
		JSONObject jsonUpdateQuestion = new JSONObject();
		
		//get question
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		int questionId = np.getInt("id");
		String questionBody = np.getString("body");
		String questionAnswer = np.getString("answer");
		int qOwnerId = np.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check if this is the owner of the question
		if (u.getId() == qOwnerId)
		{
			//update question in database
			int qidUpdated = QuestionController.updateQuestion(questionId, questionBody, questionAnswer);
			
			//return result
			if(qidUpdated > -1 && qidUpdated == questionId)
			{
				jsonUpdateQuestion.put("message", "question updated");
				jsonUpdateQuestion.put("id", qidUpdated);
				jsonUpdateQuestion.put("body", questionBody);
				jsonUpdateQuestion.put("answer", questionAnswer);
				return jsonUpdateQuestion;
			}
			else
			{
				jsonUpdateQuestion.put("error", "error occured updating question");
				return jsonUpdateQuestion;
			}
		}
		else
		{
			jsonUpdateQuestion.put("error", "the user has no privilleges");
			return jsonUpdateQuestion;
		}
	}
}
