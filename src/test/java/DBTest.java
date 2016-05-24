import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;

public class DBTest {
	
	@Test
	public void db_returnDataFromDBUsers() {
		String query = "Select * FROM users";
		int rows = DB.countResult(query, DB.DB_FTPUSER);
		assertTrue(rows > 0);
	}
	
	@Test 
	public void db_verifyUserSuccesfully() {
		assertTrue(DB.verifyUser("lps_dane", "password"));
	}
}