package application.controllers;

import application.dao.MySQLDAO;
import application.dto.Test;
import application.dto.TestsList;

public class TestController {

	public static TestsList loadAllTests()
	{
		try {
			MySQLDAO dao = new MySQLDAO();
			TestsList allTests = dao.loadAllTests();
			return allTests;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Test addTest(String name, int ownerId)
	{
		try {
			Test t = new Test();
			t.setName(name);
			t.setOwnerId(ownerId);
			
			MySQLDAO dao = new MySQLDAO();
			Test insertedTest = dao.insertTest(t);
			
			return insertedTest;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Test deleteTest(int testId)
	{
		try {
			MySQLDAO dao = new MySQLDAO();
			Test t = dao.deleteTest(testId);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Test updateTest(int testId, String testName)
	{
		try {
			MySQLDAO dao = new MySQLDAO();
			Test t = dao.updateTest(testId, testName);
			return t;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
