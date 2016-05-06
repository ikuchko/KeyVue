import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.*;
import static org.junit.Assert.*;

public class DBTest {
	
	@Test
	public void db_returnDataFromDB() {
		String query = "Select * FROM county";
		int rows = DB.requestData(query);
		assertTrue(rows > 0);
	}
}