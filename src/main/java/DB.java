import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


public class DB {
	public static final Integer DB_FTPUSER = 1;
	public static final Integer DB_DOCUMENTS = 2;

	public static MysqlDataSource getMySQLDataSource(Integer db) {
		Properties properties = new Properties();
		FileInputStream fileInputStream = null;
		MysqlDataSource mysqlDS = null;
		try {
			fileInputStream = new FileInputStream("./resources/mySQL.properties");
			properties.load(fileInputStream);
			mysqlDS = new MysqlDataSource();
			if (db == 1) {
				mysqlDS.setUrl(properties.getProperty("MYSQL_DB_FTPUSERS_URL"));
			} else if (db == 2) {
				mysqlDS.setUrl(properties.getProperty("MYSQL_DB_DOCUMENTS_URL"));
			} else return null;
			mysqlDS.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
			mysqlDS.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("USER_NAME: " + properties.getProperty("MYSQL_DB_USERNAME"));
		}
		return mysqlDS;
	}

	public static int countResult(String query, Integer db) {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		int rowsAmount = 0;
		MysqlDataSource dataSource = getMySQLDataSource(db);
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

	public static List<HashMap<String, String>> requestData(String query, Integer db) {
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		MysqlDataSource dataSource = getMySQLDataSource(db);
		ResultSetMetaData resultSetMetaData;
		List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			resultSetMetaData = resultSet.getMetaData();
//			resultSet.first();
			while (resultSet.next()) {
				HashMap<String, String> hashMap = new HashMap<>();
				for (int i=1; i<=resultSetMetaData.getColumnCount(); i++) {
					hashMap.put(resultSetMetaData.getColumnLabel(i), resultSet.getString(i));
				}
				result.add(hashMap);
			}
//			result = resultSet.getString(columnName);
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
		return result;
	}

	public static Boolean verifyUser(String login, String password) {
//		String query = String.format("SELECT * FROM keyvue_users JOIN users ON keyvue_users.users_id = users.id WHERE users.userid = '%s' AND keyvue_users.keyvue_passwd = MD5('%s')", login, password);
		String query = String.format("SELECT * FROM keyvue_users WHERE keyvue_login = '%s' AND keyvue_passwd = MD5('%s')", login, password);
		return (countResult(query, DB_FTPUSER) > 0);
	}

//	public static String getFTPPasswd(String login, String password) {
//		String query = String.format("SELECT * FROM users WHERE userid = '%s' AND keyvue_passwd = '%s'", login, password);
//		return (requestData(query, DB_FTPUSER) > 0);
//	}
}
