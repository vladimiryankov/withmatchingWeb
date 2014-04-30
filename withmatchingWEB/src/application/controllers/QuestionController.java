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

	public static int addQuestion(String body, String answer, int userID) {
		try {
			Question q = new Question();
			q.setBody(body);
			q.setAnswer(answer);
			q.setOwnerId(userID);
			
			MySQLDAO dao = new MySQLDAO();
			dao.insertQuestion(q);
			
			return q.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean deleteQuestion(int questionID) {
		try {
			MySQLDAO dao = new MySQLDAO();
			dao.deleteQuestion(questionID);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateQuestion(int questionId, String questionBody,
			String questionAnswer) {
		try {
			MySQLDAO dao = new MySQLDAO();
			dao.updateQuestion(questionBody, questionAnswer, questionId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
