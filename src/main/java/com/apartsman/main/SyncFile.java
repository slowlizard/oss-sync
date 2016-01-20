package com.apartsman.main;

import java.io.InputStream;
import java.util.List;

import com.apartsman.service.oss.AliyunOSSUtils;
import com.apartsman.service.sync.JdbcUtil;
import com.apartsman.service.sync.SSHConnection;

public class SyncFile {
	private static String rootPath = "/aparts/img/ItemPicture/41";
	public static void main(String[] args) {
		AliyunOSSUtils ossUtil=new AliyunOSSUtils(rootPath);
		SSHConnection connection = null;
		try {
			String user = "root";
			String passwd = "123456";
			String host = "47.88.137.89";
			connection = new SSHConnection(user, passwd, host);
			List<String> filePaths = connection.getFiles(rootPath);
			for (String filePath : filePaths) {
				if (JdbcUtil.find(filePath + host)) {
					InputStream in = connection.getInputStream(filePath);
					System.out.println(filePath + "文件大小:" + in.available());
					ossUtil.putObject(in, filePath);
					JdbcUtil.insert(filePath + host);
				}
			}
		} catch (Exception e) {
		}
		
		finally {
			connection.close();

		}
	}
}
