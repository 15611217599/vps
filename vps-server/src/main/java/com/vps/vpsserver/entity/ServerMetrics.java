package com.vps.vpsserver.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "server_metrics")
public class ServerMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(name = "cpu_usage", nullable = false)
    private Double cpuUsage; // percent 0-100

    @Column(name = "memory_usage", nullable = false)
    private Double memoryUsage; // percent 0-100

    @Column(name = "disk_usage", nullable = false)
    private Double diskUsage; // percent 0-100 for '/'

    @Column(name = "net_in_kbps", nullable = false)
    private Double netInKbps; // kbps aggregated non-lo interfaces

    @Column(name = "net_out_kbps", nullable = false)
    private Double netOutKbps; // kbps aggregated non-lo interfaces

    @Column(name = "status", length = 10, nullable = false)
    private String status; // ONLINE / OFFLINE

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Server getServer() { return server; }
    public void setServer(Server server) { this.server = server; }
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
    public Double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(Double diskUsage) { this.diskUsage = diskUsage; }
    public Double getNetInKbps() { return netInKbps; }
    public void setNetInKbps(Double netInKbps) { this.netInKbps = netInKbps; }
    public Double getNetOutKbps() { return netOutKbps; }
    public void setNetOutKbps(Double netOutKbps) { this.netOutKbps = netOutKbps; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
