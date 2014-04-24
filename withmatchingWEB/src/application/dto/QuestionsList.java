package application.dto;

import java.util.ArrayList;

public class QuestionsList extends ArrayList<Question> {

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

}
