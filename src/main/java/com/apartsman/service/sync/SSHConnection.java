package com.apartsman.service.sync;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSHConnection {
	private String charset = "UTF-8"; // 设置编码格式
	private String user; // 用户名
	private String passwd; // 登录密码
	private String host; // 主机IP
	private JSch jsch;
	private Session session;
	private ChannelSftp channel = null;

	/**
	 * 
	 * @param user用户名
	 * @param passwd密码
	 * @param host主机IP
	 */
	public SSHConnection(String user, String passwd, String host) {
		this.user = user;
		this.passwd = passwd;
		this.host = host;
	}

	/**
	 * 连接到指定的IP
	 * 
	 * @throws JSchException
	 */
	public void connect() throws JSchException {
		jsch = new JSch();
		session = jsch.getSession(user, host, 22);
		session.setPassword(passwd);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();

	}

	/**
	 * 执行相关的命令
	 */
	public void execCmd() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String command = "";
		BufferedReader reader = null;
		Channel channel = null;

		try {
			while ((command = br.readLine()) != null) {
				channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				channel.setInputStream(null);
				((ChannelExec) channel).setErrStream(System.err);

				channel.connect();
				InputStream in = channel.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in,
						Charset.forName(charset)));
				String buf = null;
				while ((buf = reader.readLine()) != null) {
					System.out.println(buf);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			channel.disconnect();
			session.disconnect();
		}
	}

	/**
	 * http://www.linuxidc.com/Linux/2014-05/101840.htm
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 */
	public void file() throws Exception {

	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public JSch getJsch() {
		return jsch;
	}

	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * 
	 * @param channel
	 * @param path
	 * @param files
	 * @throws SftpException
	 */
	private void listFile(ChannelSftp channel, String path, List<String> files)
			throws SftpException {
		Vector<LsEntry> list = channel.ls(path);
		for (LsEntry lsEntry : list) {
			String longname = lsEntry.getLongname();
			String fileName = lsEntry.getFilename();
			if (longname.toCharArray()[0] == 'd') {
				if (!fileName.trim().contains(".")
						&& !fileName.trim().contains("..")) {
					  System.out.println(fileName);
					  listFile(channel, path + "/" + fileName, files);
				}
			} else {
				files.add(path + "/" + fileName);
				System.out.println(path + "/" + fileName);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		SSHConnection connection = null;
		ChannelSftp channel = null;
		Session session = null;
		try {
			String user = "root";
			String passwd = "Apartsman2015";
			String host = "120.55.181.223";
			connection = new SSHConnection(user, passwd, host);
			connection.connect();
			session = connection.getSession();
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(5000);
			// channel.cd("/aparts/img");
			// FileOutputStream dst = new FileOutputStream(new
			// File("E:\120.55.181.233"));
			// channel.get("/aparts/img", dst);
			String rootPath = "/aparts/img";
			List<String> files = new ArrayList<String>();
			connection.listFile(channel, rootPath, files);
			// for (String string : files) {
			// System.out.println(string);
			// }

		} catch (Exception e) {

		} finally {

			channel.disconnect();
			session.disconnect();
		}

	}

	/**
	 * 
	 * String rootPath = "/aparts/img"; // channel.cd("/aparts/img"); //
	 * FileOutputStream dst = new FileOutputStream(new //
	 * File("E:\120.55.181.233")); // channel.get("/aparts/img", dst);
	 * 
	 * @return
	 */
	public List<String> getFiles(String remotePath) {
		List<String> files = new ArrayList<String>();
		try {
			this.connect();
			session = this.getSession();
			this.channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(5000);
			this.listFile(channel, remotePath, files);
			return files;
		} catch (Exception e) {

		} finally {

		}
		return files;
	}

	public ChannelSftp getChannel() {
		return channel;
	}

	public void setChannel(ChannelSftp channel) {
		this.channel = channel;
	}

	/**
	 * 
	 * 从服务器下载文件
	 * 
	 * @param filePath
	 * @return
	 * @throws SftpException
	 */
	@SuppressWarnings("deprecation")
	public InputStream getInputStream(String filePath) throws SftpException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.channel.get(filePath, out);
		return new ByteArrayInputStream(out.toByteArray());
	}

	/**
	 * 关闭各种资源
	 */
	public void close() {
		channel.disconnect();
		session.disconnect();

	}
}
