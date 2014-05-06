package application.dto;

import net.minidev.json.JSONObject;
import application.util.IToJSON;

public class User implements IToJSON{

	private int id;
	private String name;
	private String email;
	private String password;
	
	public User()
	{
		this.id = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		
		json.put("id", this.getId());
		json.put("name", this.getName());
		json.put("email", this.getEmail());
		json.put("password", this.getPassword());
		
		return json;
	}
	
	public User createUser(JSONObject json)
	{
		User u = new User();
		
		u.setId((int) json.get("id"));
		u.setName((String) json.get("name"));
		u.setEmail((String) json.get("email"));
		u.setPassword((String) json.get("password"));
		
		return u;
	}
	
}
