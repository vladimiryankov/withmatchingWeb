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
	
	public static int addTest(String name, int ownerId)
	{
		try {
			Test t = new Test();
			t.setName(name);
			t.setOwnerId(ownerId);
			
			MySQLDAO dao = new MySQLDAO();
			Test insertedTest = dao.insertTest(t);
			
			return insertedTest.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int deleteTest(int testId)
	{
		try {
			MySQLDAO dao = new MySQLDAO();
			int tid = dao.deleteTest(testId);
			return tid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static int updateTest(int testId, String testName)
	{
		try {
			MySQLDAO dao = new MySQLDAO();
			Test t = dao.updateTest(testId, testName);
			return t.getId();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
