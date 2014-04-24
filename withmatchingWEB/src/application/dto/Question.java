package application.dto;

import java.io.IOException;

import application.util.Base64;
import javafx.beans.property.SimpleStringProperty;

public class Question {
	private SimpleStringProperty id;
	private SimpleStringProperty body;
	private SimpleStringProperty answer;
	private long ownerId;
	
	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
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
	
}
