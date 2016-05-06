import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


public class DB {

	public static MysqlDataSource getMySQLDataSource() {
		Properties properties = new Properties();
		FileInputStream fileInputStream = null;
		MysqlDataSource mysqlDS = null;
		try {
			fileInputStream = new FileInputStream("./resources/mySQL.properties");
			properties.load(fileInputStream);
			mysqlDS = new MysqlDataSource();
			mysqlDS.setUrl(properties.getProperty("MYSQL_DB_URL"));
			mysqlDS.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
			mysqlDS.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("USER_NAME: " + properties.getProperty("MYSQL_DB_USERNAME"));
		}
		return mysqlDS;
	}
	
	public static int requestData(String query) {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		int rowsAmount = 0;
		MysqlDataSource dataSource = getMySQLDataSource();
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			resultSet.last();
			rowsAmount = resultSet.getRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
//				if(resultSet != null) resultSet.close();
				if(statement != null) statement.close();
				if(connection != null) connection.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return rowsAmount;
	}
}
