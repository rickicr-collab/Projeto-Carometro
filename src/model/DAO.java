package model;

import java.sql.Connection;
import java.sql.DriverManager;

// TODO: Auto-generated Javadoc
/**
 * The Class DAO.
 */
public class DAO {

	/** The con. */
	private Connection con;
	
	/** The driver. */
	private String driver = "com.mysql.cj.jdbc.Driver";
	
	/** The url. */
	private String url = "jdbc:mysql://localhost:3306/bdcarometro";
	
	/** The user. */
	private String user = "root";
	
	/** The pass word. */
	private String passWord = "";

	/**
	 * Conectar.
	 *
	 * @return the connection
	 */
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
