package application.controllers;

import application.dao.MySQLDAO;
import application.dto.Question;
import application.dto.QuestionsList;

public class QuestionController {

	public static QuestionsList loadAllQuestions() {
		try {
			MySQLDAO dao = new MySQLDAO();
			QuestionsList qList = dao.loadAllQuestions();
			return qList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static Question addQuestion(String body, String answer, int userID) {
		try {
			Question q = new Question();
			q.setBody(body);
			q.setAnswer(answer);
			q.setOwnerId(userID);
			
			MySQLDAO dao = new MySQLDAO();
			Question insertedQ = dao.insertQuestion(q);
			
			return insertedQ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Question deleteQuestion(int questionID) {
		try {
			MySQLDAO dao = new MySQLDAO();
			Question q = dao.deleteQuestion(questionID);
			return q;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Question updateQuestion(int questionId, String questionBody,
			String questionAnswer) {
		try {
			MySQLDAO dao = new MySQLDAO();
			Question q = dao.updateQuestion(questionBody, questionAnswer, questionId);
			return q;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
