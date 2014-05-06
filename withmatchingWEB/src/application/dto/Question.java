package application.dto;

import java.io.IOException;

import net.minidev.json.JSONObject;
import application.util.Base64;
import application.util.IToJSON;
import javafx.beans.property.SimpleStringProperty;

public class Question implements IToJSON{
	private SimpleStringProperty id;
	private SimpleStringProperty body;
	private SimpleStringProperty answer;
	private int ownerId;
	
	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public Question() {
		this.id = new SimpleStringProperty();
		this.body = new SimpleStringProperty();
		this.answer = new SimpleStringProperty();
		this.ownerId = 0;
	}
	
	public Question(Question question) {
		this.id = question.id;
		this.body = question.body;
		this.ownerId = question.ownerId;
		this.answer = question.answer;
	}

	public int getId() {
		return Integer.parseInt(id.get());
	}

	public void setId(int id) {
		this.id.set(Integer.toString(id));
	}

	public String getBody() {
		return body.get();
	}

	public void setBody(String body) {
		this.body.set(body);
	}

	public String getAnswer() {
		return this.answer.get();
	}

	public void setAnswer(String answer) {

		this.answer.set(answer);
	}
	
	public static String generateHash(int qid, int tid) throws Exception {
		try {
			return Base64.encodeObject(tid+"q"+qid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		
		json.put("id", this.getId());
		json.put("body", this.getBody());
		json.put("answer", this.getAnswer());
		json.put("ownerId", this.getOwnerId());
		
		return json;
	}
	
	public Question createQuestion(JSONObject json) {
		Question q = new Question();
		
		if	(json.containsValue("id"))
		{
			q.setId((int) json.get("id"));
			q.setAnswer((String) json.get("answer"));
			q.setBody((String) json.get("body"));
			q.setOwnerId((int) json.get("ownerId"));
		}
		
		return q;
	}
	
}
