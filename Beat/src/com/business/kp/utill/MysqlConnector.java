package com.business.kp.utill;

import java.sql.Connection;
import java.sql.DriverManager;

public class MysqlConnector {
	
	public static Connection getConnection(){
		
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		    conn =DriverManager.getConnection("jdbc:mysql://localhost/kp","root","");

		    	  
		} catch (Exception ex) {
			
			
		}
		return conn;
		
	}

}
