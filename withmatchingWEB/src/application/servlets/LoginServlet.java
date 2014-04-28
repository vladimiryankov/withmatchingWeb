package application.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;

import application.dao.MySQLDAO;
import application.dto.User;
import application.util.PassEncript;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParamsType;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.util.NamedParamsRetriever;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet implements PassEncript {
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
			
			req = JSONRPC2Request.parse(request.getParameter("json"));
			resp = new JSONRPC2Response(req.getID());
			
			//retrieve request information
			String method = req.getMethod();
			
			if (method.equals("login"))
			{
				//to do
				//JSONRPC2Response resp = loginUser(request, response);
			}
			else if (method.equals("registration"))
			{
				
				//JSONRPC2Response resp = registerUser(request, response);
				//return response?
			}
			else
			{
				JSONObject json = new JSONObject();
				json.put("hi", "opa");
				resp.setResult(json.toJSONString());
			}
		} catch(Throwable t) {
			JSONRPC2Error error = new JSONRPC2Error(1, t.getMessage());
			
			resp.setError(error);
		} finally {
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
			u.setPassword(PassHash(np.getString("password")));
			
			//add user
			MySQLDAO dao = new MySQLDAO();
			dao.insertUser(u);
			
			return new JSONRPC2Response("OK", request.getID());
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
			String enteredHashPass = PassHash(np.getString("password"));
			if (u.getPassword().equals(enteredHashPass))
			{
				//create session
				//no http session in jsonrpc2request?
			}
			else
			{
				return new JSONRPC2Response("Email / Password missmatch!", request.getID());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR, request.getID());
		}
		
		return new JSONRPC2Response("OK", request.getID());
	}

	public String PassHash(String password) {
		String hashedPass;
		hashedPass = DigestUtils.sha256Hex(password);
		return hashedPass;
	}

}
