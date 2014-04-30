package application.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.minidev.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;

import application.dao.MySQLDAO;
import application.dto.User;
import application.util.PassEncript;
import application.util.TimeEncrpyt;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Message;
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
			//populate rpc objects
			req = JSONRPC2Request.parse(request.getParameter("json"));
			resp = new JSONRPC2Response(req.getID());
			
			//retrieve request information
			String method = req.getMethod();
			
			if (method.equals("login"))
			{
				//check credentials
				resp = loginUser(req, resp);
				
				//create session
				if (resp.getResult().toString().equals("success"))
				{
					HttpSession session = request.getSession();
					session.setAttribute("pass", TimeEncrpyt.TimeHash());
					session.setMaxInactiveInterval(30*60);
					Cookie pass = new Cookie("pass", TimeEncrpyt.TimeHash());
					pass.setMaxAge(30*60);
					response.addCookie(pass);
					resp.setResult("Login successsful");
				}
			}
			else if (method.equals("registration"))
			{
				//register user
				resp = registerUser(req, resp);
			}
			else if (isLoggedIn(request))
			{
				if (method.equals("logout")) {
					logoutUser(request);
					resp.setResult("Logout successful");
				}
				JSONObject json = new JSONObject();
				json.put("hi", "opa");
				resp.setResult(json.toJSONString());
			}
		} catch(Throwable t) {
			JSONRPC2Error error = new JSONRPC2Error(1, t.getMessage());
			
			resp.setError(error);
		} finally {
			//dispatch result
			response.getWriter().print(resp.toJSONObject().toJSONString());
		}
	}
	
	public JSONRPC2Response registerUser(JSONRPC2Request request, JSONRPC2Response response) {
		
		JSONRPC2ParamsType paramsType = request.getParamsType();
		Map<String,Object> params = request.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		
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
			
			return new JSONRPC2Response("User registered", request.getID());
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, request.getID());
		}
		
	}
	
	public JSONRPC2Response loginUser(JSONRPC2Request request, JSONRPC2Response response){
		//retrieve request information
		Map<String,Object> params = request.getNamedParams();
		NamedParamsRetriever np = new NamedParamsRetriever(params);
		
		try {
			//load user by email
			MySQLDAO dao = new MySQLDAO();
			User u = dao.loadUser(np.getString("email"));
			
			//check if password is correct
			String enteredHashPass = PassEncript.PassHash(np.getString("password"));
			if (u.getPassword().equals(enteredHashPass))
			{
				return new JSONRPC2Response("success", request.getID());
			}
			else
			{
				JSONRPC2Error loginError = new JSONRPC2Error(2, "Email / Password missmatch");
				response.setError(loginError);
				return new JSONRPC2Response("Email / Password missmatch!", request.getID());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, request.getID());
		}
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
}
