package application.servlets;


import java.io.BufferedReader;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import application.controllers.QuestionController;
import application.controllers.TestController;
import application.dao.MySQLDAO;
import application.dto.CheckableQuestion;
import application.dto.Question;
import application.dto.QuestionsList;
import application.dto.Test;
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
				//resp.setResult(jsonResult.toJSONString());
				resp.setResult(jsonResult);
			}
			else if (method.equals("register"))
			{
				//register user
				//TODO duplicates!
				jsonResult = registerUser(req, resp);
				System.out.println("json result to string: " + jsonResult.toString());
				resp.setResult(jsonResult);
			}
			else if (isLoggedIn(request))
			{
				if (method.equals("logout")) {
					//logout user
					jsonResult = logoutUser(request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if (method.equals("loadAllQuestions"))
				{
					//return all questions as QuestionsList
					QuestionsList questions = QuestionController.loadAllQuestions();
					if (questions != null)
					{
						JSONObject jsonQuestions = new JSONObject();
						JSONArray allQuestions = questions.toJSONArray();
						jsonQuestions.put("allQuestions", allQuestions);
						System.out.println("jsonQuestions: " + jsonQuestions.toString());
						resp.setResult(jsonQuestions);
					}
					else
					{
						resp.setError(JSONRPC2Error.INTERNAL_ERROR);
					}
				}
				else if (method.equals("loadQuestion"))
				{
					jsonResult = loadQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if (method.equals("addQuestion"))
				{
					//add question
					jsonResult = addQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("deleteQuestion"))
				{
					//delete question
					jsonResult = deleteQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("udpateQuestion"))
				{
					//update question
					jsonResult = updateQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if (method.equals("loadTestAndAllQuestions"))
				{
					//return the test and all questions from the database
					jsonResult = loadTestAndAllQuestions(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if (method.equals("loadAllTests"))
				{
					//return all questions as QuestionsList
					TestsList tests = TestController.loadAllTests();
					if (tests != null)
					{
						JSONObject jsonTests = new JSONObject();
						JSONArray allTests = tests.toJSONArray();
						jsonTests.put("allTests", allTests);
						System.out.println("jsonTests: " + jsonTests.toString());
						resp.setResult(jsonTests);
						
					}
					else
					{
						resp.setError(JSONRPC2Error.INTERNAL_ERROR);
					}
				}
				else if (method.equals("loadQuestionsByTest"))
				{
					JSONArray testQuestions = new JSONArray();
					testQuestions = loadQuestionsByTest(req, request);
					jsonResult.put("testQuestions", testQuestions);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("addTest"))
				{
					//add question
					jsonResult = addTest(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("deleteTest"))
				{
					//delete question
					jsonResult = deleteTest(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("updateTest"))
				{
					//update question
					jsonResult = updateTest(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("addTestQuestion"))
				{
					//bind question to test
					jsonResult = addTestQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("removeTestQuestion"))
				{
					//delete question from test
					jsonResult = removeTestQuestion(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
				}
				else if(method.equals("checkTest"))
				{
					//check if test is asnwered correctly
					jsonResult = checkTest(req, request);
					System.out.println("json result: " + jsonResult.toString());
					resp.setResult(jsonResult);
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

	public JSONObject loadQuestion(JSONRPC2Request req,
			HttpServletRequest request) throws Exception {
		JSONObject jsonResult = new JSONObject();
		JSONObject jsonQuestion = new JSONObject();
		
		//set retrievers
		Map<String, Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> qParams = np.getMap("question");
		NamedParamsRetriever questNp = new NamedParamsRetriever(qParams);
		int qid = questNp.getInt("id");
		
		//load question
		MySQLDAO dao = new MySQLDAO();
		Question q = dao.loadQuestion(qid);
		
		//set result
		jsonQuestion = q.toJSONObject();
		jsonResult.put("question", jsonQuestion);
		
		return jsonResult;
	}

	public JSONObject loadTestAndAllQuestions(JSONRPC2Request req,
			HttpServletRequest request) throws Exception {
		JSONObject jsonResult = new JSONObject();
		JSONArray jsonAllQuestions = new JSONArray();
		JSONObject jsonTest = new JSONObject();
		
		//retrievers
		Map<String, Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> tParams = np.getMap("test");
		NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
		int tid = testNp.getInt("id");
		
		//get data from DB
		MySQLDAO dao = new MySQLDAO();
		Test t = dao.loadTest(tid);
		QuestionsList allQuestions = QuestionController.loadAllQuestions();
		
		//set result
		jsonTest = t.toJSONObject();
		jsonAllQuestions = allQuestions.toJSONArray();
		jsonResult.put("test", jsonTest);
		jsonResult.put("questions", jsonAllQuestions);
		
		return jsonResult;
	}

	public JSONObject checkTest(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error, Exception {
		JSONObject jsonResult = new JSONObject();
		JSONArray jsonWrongQuestions = new JSONArray();
		QuestionsList wrongQuestions = new QuestionsList();
		
		//retrievers
		Map<String, Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> tParams = np.getMap("test");
		NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
		
		//get guessed questions values
		QuestionsList guessQuestions = new QuestionsList();
		List<Object> questions = testNp.getList("questions");
		for (Object q: questions)
		{
			//set new retriever for each question
			NamedParamsRetriever questNp = new NamedParamsRetriever((Map<String, Object>) q);
			//retrieve values
			String answer = questNp.getString("answer");
			String body = questNp.getString("body");
			int id = questNp.getInt("id");
			//create question with the values and add it to the guessed questions
			Question quest = new Question();
			quest.setId(id);
			quest.setBody(body);
			quest.setAnswer(answer);
			guessQuestions.add(quest);
		}
		
		//get test
		int tid = testNp.getInt("id");
		MySQLDAO dao = new MySQLDAO();
		Test t = dao.loadTest(tid);
		
		//get true questions
		MySQLDAO questionsDao = new MySQLDAO();
		QuestionsList trueQuestions = questionsDao.loadQuestionsByTest(tid);
		
		//cycle through questions and find these with same id
		for (Question tq: trueQuestions)
		{
			for (Question gq: guessQuestions)
			{
				if (tq.getId() == gq.getId())
				{
					//if answers are different, than there is mistake
					System.out.println("id match!");
					if (!tq.getAnswer().equals(gq.getAnswer()))
					{
						System.out.println("Wrong question!");
						wrongQuestions.add(gq);
					}
				}
			}
		}
		
		jsonWrongQuestions = wrongQuestions.toJSONArray();
		jsonResult.put("test", t);
		jsonResult.put("wrongQuestions", jsonWrongQuestions);
		
		return jsonResult;
	}

	public JSONObject removeTestQuestion(JSONRPC2Request req,
			HttpServletRequest request) throws Exception, SQLException {
		JSONObject jsonResult = new JSONObject();
		
		//set retrievers
		Map<String, Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> tParams = np.getMap("test");
		NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
		Map<String, Object> qParams = np.getMap("question");
		NamedParamsRetriever questNp = new NamedParamsRetriever(qParams);
		
		MySQLDAO dao = new MySQLDAO();
		Test t = dao.loadTest(testNp.getInt(("id")));
		dao = new MySQLDAO();
		Question q = dao.loadQuestion(questNp.getInt("id"));
		
		User u = getCurrentUser(request);
		
		if (u.getId() == t.getOwnerId())
		{
			dao = new MySQLDAO();
			t = dao.deleteTestQuestion(t, q);
		}
		else
		{
			throw new Exception("no privileges : you do not own this test");
		}
		
		JSONObject jsonTest = t.toJSONObject();
		JSONObject jsonQuestion = q.toJSONObject();
		
		jsonResult.put("test", jsonTest);
		jsonResult.put("question", jsonQuestion);
		
		return jsonResult;
	}

	public JSONObject addTestQuestion(JSONRPC2Request req,
			HttpServletRequest request) throws Exception, SQLException {
		JSONObject jsonResult = new JSONObject();
		
		//set retrievers
		Map<String, Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> tParams = np.getMap("test");
		NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
		Map<String, Object> qParams = np.getMap("question");
		NamedParamsRetriever questNp = new NamedParamsRetriever(qParams);
		int tid = testNp.getInt(("id"));
		int qid = questNp.getInt(("id"));
		
		//load test and question
		MySQLDAO dao = new MySQLDAO();
		Test t = dao.loadTest(tid);
		dao = new MySQLDAO();
		Question q = dao.loadQuestion(qid);
		
		//load current user
		User u = getCurrentUser(request);
		
		if (u.getId() == t.getOwnerId())
		{
			//check for duplicates
			boolean duplicate = false;
			for (Question quest: t.getQuestions())
			{
				if (quest.getId() == qid)
				{
					duplicate = true;
				}
			}
			if (!duplicate)
			{
				dao = new MySQLDAO();
				t = dao.saveTestQuestion(t, q);
			}
		}
		else
		{
			throw new Exception("no privileges : you do not own this test!");
		}
		
		//set result
		JSONObject jsonTest = t.toJSONObject();
		JSONObject jsonQuestion = q.toJSONObject();
		
		jsonResult.put("test", jsonTest);
		jsonResult.put("question", jsonQuestion);
		
		return jsonResult;
	}

	public JSONArray loadQuestionsByTest(JSONRPC2Request req,
			HttpServletRequest request) throws Exception, JSONRPC2Error {
		JSONArray jsonTestQuestions = new JSONArray();
		
		//get testId
		Map<String, Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> tParams = np.getMap("test");
		NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
		int testId = testNp.getInt("id");
		
		//get test questions
		MySQLDAO dao = new MySQLDAO();
		QuestionsList qByTest = dao.loadQuestionsByTest(testId);
		
		//set result
		jsonTestQuestions = qByTest.toJSONArray();
		
		return jsonTestQuestions;
	}

	public JSONObject updateTest(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error, Exception {
		JSONObject jsonUpdateTest = new JSONObject();
		
		//get question
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> tParams = np.getMap("test");
		NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
		int testId = testNp.getInt("id");
		String testName = testNp.getString("name");
		int tOwnerId = testNp.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check for privileges
		if	(u.getId() == tOwnerId)
		{
			//update question in database
			Test testUpdated = TestController.updateTest(testId, testName);
			
			//return result
			jsonUpdateTest.put("updatedTest", testUpdated.toJSONObject());
			
			return jsonUpdateTest;
		}
		else
		{
			throw new Exception("no privileges");
		}
	}

	public JSONObject deleteTest(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error, Exception {
		//create json object for the result
		JSONObject jsonDeleteTest = new JSONObject();
		
		//get question id
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> tParams = np.getMap("test");
		NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
		int testId = testNp.getInt("id");
		int tOwnerId = testNp.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check for privileges
		if	(u.getId() == tOwnerId)
		{

			//remove question from database
			TestController.deleteTest(testId);
			
			//send result
			return jsonDeleteTest;
		}
		else
		{
			throw new Exception("no privileges");
		}
	}

	public JSONObject addTest(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error {
				//json object for the result
				JSONObject jsonAddTest = new JSONObject();
				
				//get test
				Map<String,Object> params = req.getNamedParams();
				NamedParamsRetriever np = new NamedParamsRetriever(params);
				Map<String, Object> tParams = np.getMap("test");
				NamedParamsRetriever testNp = new NamedParamsRetriever(tParams);
				String name = testNp.getString("name");
				
				//get user
				User u = getCurrentUser(request);
				int userID = (int) u.getId();
				
				//return test if added successfully
				Test t = TestController.addTest(name, userID);
				System.out.println("add test: " + t.toJSONObject().toString());
				jsonAddTest.put("test", t.toJSONObject());
				return jsonAddTest;
	}

	public JSONObject registerUser(JSONRPC2Request request, JSONRPC2Response response) throws Exception {
		
		@SuppressWarnings("unused")
		JSONRPC2ParamsType paramsType = request.getParamsType();
		Map<String,Object> params = request.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		JSONObject jsonRegistration = new JSONObject();
			//create new user
			User u = new User();
			
			//set params
			u.setName(np.getString("name"));
			u.setEmail(np.getString("email"));
			u.setPassword(PassEncript.PassHash(np.getString("password")));
			
			//add user
			MySQLDAO dao = new MySQLDAO();
			dao.insertUser(u);
			
			jsonRegistration.put("user", u.toJSONObject());
			
			return jsonRegistration;
	}
	
	public JSONObject loginUser(HttpServletRequest request, HttpServletResponse response, JSONRPC2Request jsonReq) throws Exception
	{
		//define new json for the result
		JSONObject jsonLogin = new JSONObject();
		//get user info
		Map<String,Object> params = jsonReq.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
	
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
				throw new Exception("incorrect email");
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
					jsonLogin.put("user", u.toJSONObject());
					return jsonLogin;
				}
				else
				{
					throw new Exception("password missmatch");
				}
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

		User u = getCurrentUser(request);
		JSONObject jsonLogout = new JSONObject();
		jsonLogout.put("user", u.toJSONObject());
		return jsonLogout;
	}
	
	public JSONObject addQuestion(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error
	{
		//json object for the result
		JSONObject jsonAddQuestion = new JSONObject();
		
		//get question
		System.out.println(req.toString());
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> qParams = np.getMap("question");
		NamedParamsRetriever questionNp = new NamedParamsRetriever(qParams);
		String body = questionNp.getString("body");
		String answer = questionNp.getString("answer");
		
		//get user
		User u = getCurrentUser(request);
		int userID = (int) u.getId();
		System.out.println("current user id" + userID);
		
		//return ID of Question if added successfully
		Question q = QuestionController.addQuestion(body, answer, userID);
		
		jsonAddQuestion.put("question", q.toJSONObject());
		
		return jsonAddQuestion;
	}
	
	public JSONObject deleteQuestion(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error, Exception
	{
		//create json object for the result
		JSONObject jsonDeleteQuestion = new JSONObject();
		
		//get question id
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> qParams = np.getMap("question");
		NamedParamsRetriever questionNp = new NamedParamsRetriever(qParams);
		int questionId = questionNp.getInt("id");
		int qOwnwerId = questionNp.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check for user privilleges
		if	(u.getId() == qOwnwerId)
		{
			//remove question from database
			
			QuestionController.deleteQuestion(questionId);
			
			//send result
			return jsonDeleteQuestion;
		}
		else
		{

			System.out.println("current user id: " + u.getId());
			System.out.println("question owner id: " + qOwnwerId);
			throw new Exception("no privileges");
		}
	}
	
	public JSONObject updateQuestion(JSONRPC2Request req, HttpServletRequest request) throws JSONRPC2Error, Exception
	{
		JSONObject jsonUpdateQuestion = new JSONObject();
		
		//get question
		Map<String,Object> params = req.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		Map<String, Object> qParams = np.getMap("question");
		NamedParamsRetriever questionNp = new NamedParamsRetriever(qParams);
		int questionId = questionNp.getInt("id");
		String questionBody = questionNp.getString("body");
		String questionAnswer = questionNp.getString("answer");
		
		int qOwnerId = questionNp.getInt("ownerId");
		
		User u = getCurrentUser(request);
		
		//check if this is the owner of the question
		if (u.getId() == qOwnerId)
		{
			//update question in database
			Question qUpdated = QuestionController.updateQuestion(questionId, questionBody, questionAnswer);
			
			//return result
			jsonUpdateQuestion.put("question", qUpdated.toJSONObject());
			return jsonUpdateQuestion;
		}
		else
		{
			throw new Exception("no privileges");
		}
	}
}
