package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class FTPUtil {
    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIp =  PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser =  PropertiesUtil.getProperty("ftp.user");
    private static String ftpPassword =  PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip,int port,String user,String password){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    
    public static boolean uploadFile(List<File> listFile) throws IOException {
        System.out.println("-------------------------------------------------");
        System.out.println(ftpUser);
        System.out.println(ftpPassword);
//        InetAddress inetAddress = InetAddress.getByName(ftpIp);
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPassword);
        logger.info("开始上传文件到ftp服务器");
        boolean result = ftpUtil.uploadFile("img",listFile);
        logger.info("上传文件到ftp服务器结束，上传结果为{}",result);
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> listFile) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        if(connectServer(this.ip,this.port,this.user,this.password)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem:listFile){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("文件上传异常",e);
                e.printStackTrace();
            } finally{
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip,port);
            ftpClient.login(user,pwd);
            logger.info("ftp服务器连接成功");
            isSuccess = true;
        } catch (IOException e) {
            logger.error("连接ftp服务器失败",e);
        }
        return isSuccess;
    }

  private String ip;
    private Integer port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
