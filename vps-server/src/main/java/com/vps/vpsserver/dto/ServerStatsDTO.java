package com.vps.vpsserver.dto;

public class ServerStatsDTO {
    
    private long total;
    private long online;
    private long offline;
    private long sold;
    private long available;

    // 构造函数
    public ServerStatsDTO() {}

    public ServerStatsDTO(long total, long online, long offline, long sold, long available) {
        this.total = total;
        this.online = online;
        this.offline = offline;
        this.sold = sold;
        this.available = available;
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

    public long getSold() {
        return sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "ServerStatsDTO{" +
                "total=" + total +
                ", online=" + online +
                ", offline=" + offline +
                ", sold=" + sold +
                ", available=" + available +
                '}';
    }
}
