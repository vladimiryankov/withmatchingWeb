package application.dto;

import java.io.Serializable;

import net.minidev.json.JSONObject;
import application.util.IToJSON;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.DataFormat;

public class CheckableQuestion extends Question implements Serializable, IToJSON{
	


	public static final DataFormat CheckableQuestion_DATA_FORMAT = new DataFormat("application.dto.CheckableQuestion");
	
	private transient BooleanProperty checked;
	private boolean bound;
	private String ans;
	private transient SimpleStringProperty guess;
	
	public CheckableQuestion(Question q) {
		checked = new BooleanProperty() {
			
			@Override
			public void set(boolean arg0) {
				// TODO Auto-generated method stub
				bound = arg0;
			}
			
			@Override
			public boolean get() {
				// TODO Auto-generated method stub
				return bound;
			}
			
			@Override
			public void removeListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeListener(ChangeListener<? super Boolean> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ChangeListener<? super Boolean> arg0) {
				
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getBean() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void unbind() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isBound() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void bind(ObservableValue<? extends Boolean> arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		
		this.setId(q.getId());
		this.setBody(q.getBody());
		this.setAnswer(q.getAnswer());
		this.setChecked(false);
		this.setGuess("empty");
	}

	public BooleanProperty isChecked() {
		return checked;
	}
	
	public BooleanProperty checkedProperty() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked.set(checked);
	}
	
	@Override
	public void setAnswer(String answer) {
		super.setAnswer(answer);
		this.ans = answer;
	}
	
	@Override
	public String getAnswer() {
		return this.ans;
	}
	
	public String getGuess() {
		return guess.get();
	}

	public void setGuess(String guess) {
		if (this.guess == null)
		{
			this.guess = new SimpleStringProperty();
		}
		this.guess.set(guess);
	}
	
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		
		json.put("id", this.getId());
		json.put("body", this.getBody());
		json.put("answer", this.getAnswer());
		json.put("ownerId", this.getOwnerId());
		json.put("checked", this.checked.get());
		
		return json;
	}
	
	public CheckableQuestion createQuestion(JSONObject json) {
		Question q = createQuestion(json);
		
		CheckableQuestion cq = new CheckableQuestion(q);
		
		cq.setChecked((boolean) json.get("checked"));
		
		return cq;
	}
}

