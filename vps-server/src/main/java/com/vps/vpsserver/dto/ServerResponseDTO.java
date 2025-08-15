package com.vps.vpsserver.dto;

import java.time.LocalDateTime;
import com.vps.vpsserver.entity.Server;

public class ServerResponseDTO {
    
    private Long id;
    private String name;
    private String ip;
    private String type;
    private String location;
    private String status;
    private String provider;
    private Integer port;
    private String username;
    private String password;
    
    // 硬件配置字段
    private String cpuCores;
    private String memory;
    private String diskSpace;
    private String diskType;
    private String networkSpeed;
    private String operatingSystem;
    
    // 分组信息
    private Long groupId;
    private String groupName;
    
    private LocalDateTime createTime;
    private LocalDateTime lastUpdate;

    // 构造函数
    public ServerResponseDTO() {}

    public ServerResponseDTO(Server server) {
        this.id = server.getId();
        this.name = server.getName();
        this.ip = server.getIp();
        this.type = server.getType();
        this.location = server.getLocation();
        this.status = server.getStatus().getValue();
        this.provider = server.getProvider();
        this.port = server.getPort();
        this.username = server.getUsername();
        this.password = server.getPassword();
        this.cpuCores = server.getCpuCores();
        this.memory = server.getMemory();
        this.diskSpace = server.getDiskSpace();
        this.diskType = server.getDiskType();
        this.networkSpeed = server.getNetworkSpeed();
        this.operatingSystem = server.getOperatingSystem();
        
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

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    @Override
    public String toString() {
        return "ServerResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", provider='" + provider + '\'' +
                ", port=" + port +
                ", createTime=" + createTime +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
