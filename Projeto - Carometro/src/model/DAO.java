package model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {

	private Connection con;
	private String driver = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/bdcarometro";
	private String user = "root";
	private String passWord = "";

	public Connection conectar() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, passWord);
			return con;
		} catch (Exception e) {
			System.out.println("Exception gerada: [" + e + "]");
			return null;
		}
	}

}
