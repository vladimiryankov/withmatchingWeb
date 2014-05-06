package application.dto;

import java.util.ArrayList;

import net.minidev.json.JSONArray;
import application.util.IToJSONArray;

public class QuestionsList extends ArrayList<Question> implements IToJSONArray {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8576165119516651967L;
	
	public ArrayList<CheckableQuestion> convertToCheckableQuestions () {
		ArrayList<CheckableQuestion> cqList = new ArrayList<CheckableQuestion>();
		
		for (Question q: this) {
			cqList.add(new CheckableQuestion(q));
		}
		
		return cqList;
	}

	@Override
	public JSONArray toJSONArray() {
		JSONArray jsonQuestions = new JSONArray();
		
		for (Question q: this) {
			jsonQuestions.add(q.toJSONObject());
		}
		
		return jsonQuestions;
	}

}
