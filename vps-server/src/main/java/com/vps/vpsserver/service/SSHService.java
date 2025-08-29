package com.vps.vpsserver.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class SSHService {
    
    private static final int CONNECTION_TIMEOUT = 30000; // 30秒
    private static final int COMMAND_TIMEOUT = 300000;   // 5分钟
    
    /**
     * 执行SSH命令
     */
    public CompletableFuture<SSHResult> executeCommand(String host, int port, String username, 
                                                      String password, String command) {
        return CompletableFuture.supplyAsync(() -> {
            JSch jsch = new JSch();
            Session session = null;
            ChannelExec channel = null;
            
            try {
                log.info("连接到服务器: {}:{} 用户: {}", host, port, username);
                
                session = jsch.getSession(username, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setTimeout(CONNECTION_TIMEOUT);
                session.connect();
                
                log.info("SSH连接成功，执行命令: {}", command);
                
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                
                channel.setOutputStream(outputStream);
                channel.setErrStream(errorStream);
                
                InputStream inputStream = channel.getInputStream();
                channel.connect();
                
                // 读取输出
                StringBuilder output = new StringBuilder();
                byte[] buffer = new byte[1024];
                
                while (true) {
                    while (inputStream.available() > 0) {
                        int i = inputStream.read(buffer, 0, 1024);
                        if (i < 0) break;
                        output.append(new String(buffer, 0, i));
                    }
                    
                    if (channel.isClosed()) {
                        if (inputStream.available() > 0) continue;
                        break;
                    }
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                int exitCode = channel.getExitStatus();
                String outputStr = output.toString();
                String errorStr = errorStream.toString();
                
                log.info("命令执行完成，退出码: {}", exitCode);
                
                return new SSHResult(exitCode == 0, exitCode, outputStr, errorStr);
                
            } catch (Exception e) {
                log.error("SSH命令执行失败: {}", e.getMessage(), e);
                return new SSHResult(false, -1, "", e.getMessage());
            } finally {
                if (channel != null && channel.isConnected()) {
                    channel.disconnect();
                }
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
            }
        });
    }
    
    /**
     * 测试SSH连接
     */
    public boolean testConnection(String host, int port, String username, String password) {
        JSch jsch = new JSch();
        Session session = null;
        
        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(CONNECTION_TIMEOUT);
            session.connect();
            
            return session.isConnected();
            
        } catch (Exception e) {
            log.error("SSH连接测试失败: {}", e.getMessage());
            return false;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    /**
     * SSH执行结果
     */
    public static class SSHResult {
        private final boolean success;
        private final int exitCode;
        private final String output;
        private final String error;
        
        public SSHResult(boolean success, int exitCode, String output, String error) {
            this.success = success;
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }
        
        public boolean isSuccess() { return success; }
        public int getExitCode() { return exitCode; }
        public String getOutput() { return output; }
        public String getError() { return error; }
    }
}