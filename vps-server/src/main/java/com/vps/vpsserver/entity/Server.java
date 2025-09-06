package com.vps.vpsserver.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "IP地址不能为空")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", 
             message = "IP地址格式不正确")
    @Column(nullable = false, unique = true, length = 15)
    private String ip;

    @Column(name = "port")
    private Integer port;

    @NotNull(message = "状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerStatus status;

    @Column(name = "cpu_cores")
    private String cpuCores;

    @Column(name = "memory")
    private String memory;

    @Column(name = "disk_space")
    private String diskSpace;

    @Column(name = "disk_type", length = 50)
    private String diskType;

    @Column(name = "network_speed")
    private String networkSpeed;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(length = 50)
    private String username;

    @Column(length = 255)
    private String password;

    @Column(name = "is_sold", nullable = false)
    private Boolean isSold = false;



    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    // 多对一关系：多个服务器属于一个分组
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ServerGroup group;

    // 构造函数
    public Server() {}
    
    public Server(String ip, Integer port, ServerStatus status) {
        this.ip = ip;
        this.port = port;
        this.status = status;
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

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
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
    
    public ServerGroup getGroup() {
        return group;
    }
    
    public void setGroup(ServerGroup group) {
        this.group = group;
    }

    public Boolean getIsSold() {
        return isSold;
    }

    public void setIsSold(Boolean isSold) {
        this.isSold = isSold;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", status=" + status +
                ", cpuCores='" + cpuCores + '\'' +
                ", memory='" + memory + '\'' +
                ", diskSpace='" + diskSpace + '\'' +
                ", diskType='" + diskType + '\'' +
                ", networkSpeed='" + networkSpeed + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", username='" + username + '\'' +
                ", createTime=" + createTime +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    // 获取服务器名称（使用IP作为名称）
    public String getName() {
        return this.ip;
    }



    // 服务器状态枚举
    public enum ServerStatus {
        ONLINE("online"),
        OFFLINE("offline");

        private final String value;

        ServerStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ServerStatus fromValue(String value) {
            for (ServerStatus status : ServerStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }


}
