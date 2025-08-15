package com.vps.vpsserver.dto;

public class ServerStatsDTO {
    
    private long total;
    private long online;
    private long offline;
    private long warning;

    // 构造函数
    public ServerStatsDTO() {}

    public ServerStatsDTO(long total, long online, long offline, long warning) {
        this.total = total;
        this.online = online;
        this.offline = offline;
        this.warning = warning;
    }

    // Getters and Setters
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getOnline() {
        return online;
    }

    public void setOnline(long online) {
        this.online = online;
    }

    public long getOffline() {
        return offline;
    }

    public void setOffline(long offline) {
        this.offline = offline;
    }

    public long getWarning() {
        return warning;
    }

    public void setWarning(long warning) {
        this.warning = warning;
    }

    @Override
    public String toString() {
        return "ServerStatsDTO{" +
                "total=" + total +
                ", online=" + online +
                ", offline=" + offline +
                ", warning=" + warning +
                '}';
    }
}
