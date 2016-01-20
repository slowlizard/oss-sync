package com.apartsman.service.sync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtil {

	private static Connection conn = null;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://192.168.0.12:3306/FileLog?characterEncoding=UTF-8", "root", "xunzhi");
		} catch (ClassNotFoundException | SQLException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * true 表示尚未同步
	 * @param fileName
	 * @return
	 */
	public  static  boolean find(String fileName) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select  * from file_log where file_name = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, fileName);
			rs = ps.executeQuery();
			rs.last(); 
			int rowCount = rs.getRow();
			if (rowCount == 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					ps.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static  void  insert(String fileName) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("insert into file_log (file_name) values (?)");
			ps.setString(1, fileName);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}

	}

	public static void close() {

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
