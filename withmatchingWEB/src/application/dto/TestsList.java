package application.dto;

import java.util.ArrayList;

import net.minidev.json.JSONArray;
import application.util.IToJSONArray;

public class TestsList extends ArrayList<Test> implements IToJSONArray{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6559351577293346932L;

	@Override
	public JSONArray toJSONArray() {
		JSONArray jsonTests = new JSONArray();
		
		for (Test t : this)
		{
			jsonTests.add(t.toJSONObject());
		}
		
		return jsonTests;
	}

}
