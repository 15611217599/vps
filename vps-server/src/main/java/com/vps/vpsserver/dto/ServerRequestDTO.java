package com.vps.vpsserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ServerRequestDTO {
    
    @NotBlank(message = "IP地址不能为空")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", 
             message = "IP地址格式不正确")
    private String ip;

    private Integer port;

    @NotNull(message = "状态不能为空")
    private String status;

    private String cpuCores;
    private String memory;
    private String diskSpace;
    private String diskType;
    private String networkSpeed;
    private String operatingSystem;
    private String username;
    private String password;

    // 分组ID
    private Long groupId;

    // 构造函数
    public ServerRequestDTO() {}

    public ServerRequestDTO(String ip, Integer port, String status) {
        this.ip = ip;
        this.port = port;
        this.status = status;
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(String cpuCores) {
        this.cpuCores = cpuCores;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(String diskSpace) {
        this.diskSpace = diskSpace;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public String getNetworkSpeed() {
        return networkSpeed;
    }

    public void setNetworkSpeed(String networkSpeed) {
        this.networkSpeed = networkSpeed;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ServerRequestDTO{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", status='" + status + '\'' +
                ", cpuCores='" + cpuCores + '\'' +
                ", memory='" + memory + '\'' +
                ", diskSpace='" + diskSpace + '\'' +
                ", diskType='" + diskType + '\'' +
                ", networkSpeed='" + networkSpeed + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", username='" + username + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
