package com.apartsman.service.oss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;



import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
/**
 * 
 * 上传文件到阿里云
 * 
 * @author jesse
 *
 */
public class AliyunOSSUtils {
	private static final String ACCESS_ID = "ACCESS_ID";
	private static final String ACCESS_KEY = "ACCESS_KEY";
	private static final String OSS_ENDPOINT = "OSS_ENDPOINT";
	private static  final  String OSS_BUCKET_NAME="OSS_BUCKET_NAME";
	private static OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
	private   String   rootPath;
	
	
	
	
	
	
	public  AliyunOSSUtils(String rootPath){
		
		this.rootPath=rootPath;
		if(rootPath==null || rootPath.isEmpty()){
			throw new RuntimeException("要服务器同步的路径不能为空");
			
			
		}
		
		
	}
	
	/**
	 * 同步ECS，OSS生成文件夹的方式就是直接文件路径。
	 * @param inputStream
	 * @param fileName
	 * @throws OSSException
	 * @throws ClientException
	 * @throws IOException
	 */
	public  void  putObject(InputStream  inputStream ,String  fileName) throws OSSException, ClientException, IOException {
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(inputStream.available());
		String ossFileName=fileName.substring(rootPath.length());
		client.putObject(OSS_BUCKET_NAME, ossFileName, inputStream, meta);
	}
}
