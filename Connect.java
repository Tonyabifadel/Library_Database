package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

	private Connection connecting = null;

	public Connection registerDriver() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");										//replace your own password in mysql in order to connect to your database	
			connecting = DriverManager.getConnection("jdbc:mysql://localhost/project", "root", "");
		
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return connecting;
	}
}
