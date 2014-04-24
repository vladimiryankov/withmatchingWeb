package application.dto;

public class Test {
	
	private int id;
	private String name;
	private QuestionsList questions;
	private long ownerId;
	
	public Test() {
		this.id = 0;
	}
	
	
	public long getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(long ownerId) {
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
}