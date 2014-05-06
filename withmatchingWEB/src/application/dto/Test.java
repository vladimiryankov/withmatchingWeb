package application.dto;

import net.minidev.json.JSONObject;
import application.util.IToJSON;

public class Test implements IToJSON{
	
	private int id;
	private String name;
	private QuestionsList questions;
	private int ownerId;
	
	public Test() {
		this.id = 0;
	}
	
	
	public int getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
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

	public QuestionsList getQuestions() {
		return questions;
	}

	public void setQuestions(QuestionsList questions) {
		this.questions = questions;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}


	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		
		json.put("id", this.getId());
		json.put("name", this.getName());
		json.put("ownerId", this.getOwnerId());
		
		return json;
	}
	
	public Test createTest(JSONObject json)
	{
		Test t = new Test();
		
		t.setId((int) json.get("id"));
		t.setName((String) json.get("name"));
		t.setOwnerId((int) json.get("ownerId"));
		
		return t;
	}
}