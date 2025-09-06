package com.vps.vpsserver.dto;

import java.time.LocalDateTime;
import com.vps.vpsserver.entity.Server;

public class ServerResponseDTO {
    
    private Long id;
    private String ip;
    private Integer port;
    private String status;
    private String cpuCores;
    private String memory;
    private String diskSpace;
    private String diskType;
    private String networkSpeed;
    private String operatingSystem;
    private String username;
    private String password;
    private Boolean isSold;
    
    // 安装状态信息
    private String installStatus;
    private Integer installProgress;
    private String installStep;
    private String installError;
    private LocalDateTime installStartedAt;
    
    // 分组信息
    private Long groupId;
    private String groupName;
    
    private LocalDateTime createTime;
    private LocalDateTime lastUpdate;

    // 构造函数
    public ServerResponseDTO() {}

    public ServerResponseDTO(Server server) {
        this.id = server.getId();
        this.ip = server.getIp();
        this.port = server.getPort();
        this.status = server.getStatus().getValue();
        this.cpuCores = server.getCpuCores();
        this.memory = server.getMemory();
        this.diskSpace = server.getDiskSpace();
        this.diskType = server.getDiskType();
        this.networkSpeed = server.getNetworkSpeed();
        this.operatingSystem = server.getOperatingSystem();
        this.username = server.getUsername();
        this.password = server.getPassword();
        this.isSold = server.getIsSold();
        // 设置分组信息
        if (server.getGroup() != null) {
            this.groupId = server.getGroup().getId();
            this.groupName = server.getGroup().getName();
        }
        
        this.createTime = server.getCreateTime();
        this.lastUpdate = server.getLastUpdate();
    }

    // 静态工厂方法
    public static ServerResponseDTO fromEntity(Server server) {
        return new ServerResponseDTO(server);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getIsSold() {
        return isSold;
    }

    public void setIsSold(Boolean isSold) {
        this.isSold = isSold;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(String installStatus) {
        this.installStatus = installStatus;
    }

    public Integer getInstallProgress() {
        return installProgress;
    }

    public void setInstallProgress(Integer installProgress) {
        this.installProgress = installProgress;
    }

    public String getInstallStep() {
        return installStep;
    }

    public void setInstallStep(String installStep) {
        this.installStep = installStep;
    }

    public String getInstallError() {
        return installError;
    }

    public void setInstallError(String installError) {
        this.installError = installError;
    }

    public LocalDateTime getInstallStartedAt() {
        return installStartedAt;
    }

    public void setInstallStartedAt(LocalDateTime installStartedAt) {
        this.installStartedAt = installStartedAt;
    }

    @Override
    public String toString() {
        return "ServerResponseDTO{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", status='" + status + '\'' +
                ", cpuCores='" + cpuCores + '\'' +
                ", memory='" + memory + '\'' +
                ", diskSpace='" + diskSpace + '\'' +
                ", diskType='" + diskType + '\'' +
                ", networkSpeed='" + networkSpeed + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", username='" + username + '\'' +
                ", isSold=" + isSold +
                ", groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", createTime=" + createTime +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
