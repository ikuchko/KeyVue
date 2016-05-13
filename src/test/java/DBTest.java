import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;

public class DBTest {
	
	@Test
	public void db_returnDataFromDBDocuments() {
		String query = "Select * FROM county";
		int rows = DB.requestData(query, DB.DB_DOCUMENTS);
		assertTrue(rows > 0);
	}
	
	@Test
	public void db_returnDataFromDBUsers() {
		String query = "Select * FROM users";
		int rows = DB.requestData(query, DB.DB_FTPUSER);
		assertTrue(rows > 0);
	}
	
	@Test 
	public void db_verifyUserSuccesfully() {
		assertTrue(DB.verifyUser("lps_chespa", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F"));
	}
}