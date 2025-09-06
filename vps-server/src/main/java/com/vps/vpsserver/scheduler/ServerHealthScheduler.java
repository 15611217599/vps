package com.vps.vpsserver.scheduler;

import com.vps.vpsserver.entity.Server;
import com.vps.vpsserver.entity.Server.ServerStatus;
import com.vps.vpsserver.entity.ServerMetrics;
import com.vps.vpsserver.repository.ServerMetricsRepository;
import com.vps.vpsserver.repository.ServerRepository;
import com.vps.vpsserver.service.SSHService;
import com.vps.vpsserver.service.SSHService.SSHResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerHealthScheduler {

    private final ServerRepository serverRepository;
    private final ServerMetricsRepository metricsRepository;
    private final SSHService sshService;

    // 每分钟执行一次
    //@Scheduled(fixedRate = 60_000)
    public void collectMetricsTask() {
        List<Server> servers = serverRepository.findAll();
        for (Server server : servers) {
            try {
                collectAndSave(server);
            } catch (Exception e) {
                log.warn("采集服务器 {} 失败: {}", server.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    protected void collectAndSave(Server server) throws Exception {
        String host = server.getIp();
        int port = server.getPort() != null ? server.getPort() : 22;
        String username = server.getUsername();
        String password = server.getPassword();

        // 基础校验
        if (username == null || password == null) {
            markOffline(server, "缺少SSH凭据");
            return;
        }

        String cmd = buildMetricsCommand();
        SSHResult result;
        try {
            result = sshService.executeCommand(host, port, username, password, cmd)
                    .get(25, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            markOffline(server, e.getMessage());
            return;
        }

        if (!result.isSuccess()) {
            markOffline(server, result.getError());
            return;
        }

        String out = result.getOutput().trim();
        // 期望格式: cpu|mem|disk|rx|tx (保留两位小数)
        String[] parts = out.split("\\|");
        if (parts.length < 5) {
            markOffline(server, "输出解析失败: " + out);
            return;
        }

        double cpu = parseDouble(parts[0]);
        double mem = parseDouble(parts[1]);
        double disk = parseDouble(parts[2]);
        double rx = parseDouble(parts[3]);
        double tx = parseDouble(parts[4]);

        // 保存指标
        ServerMetrics m = new ServerMetrics();
        m.setServer(server);
        m.setCpuUsage(clamp(cpu));
        m.setMemoryUsage(clamp(mem));
        m.setDiskUsage(clamp(disk));
        m.setNetInKbps(nonNegative(rx));
        m.setNetOutKbps(nonNegative(tx));
        m.setStatus(ServerStatus.ONLINE.name());
        m.setCreatedAt(LocalDateTime.now());
        metricsRepository.save(m);

        // 更新服务器状态
        server.setStatus(ServerStatus.ONLINE);
        server.setLastUpdate(LocalDateTime.now());
        serverRepository.save(server);

        log.info("[健康检测] 服务器 {} 在线 CPU:{}% MEM:{}% DISK:{}% RX:{}kbps TX:{}kbps",
                server.getId(), cpu, mem, disk, rx, tx);
    }

    private void markOffline(Server server, String reason) {
        try {
            ServerMetrics m = new ServerMetrics();
            m.setServer(server);
            m.setCpuUsage(0d);
            m.setMemoryUsage(0d);
            m.setDiskUsage(0d);
            m.setNetInKbps(0d);
            m.setNetOutKbps(0d);
            m.setStatus(ServerStatus.OFFLINE.name());
            m.setCreatedAt(LocalDateTime.now());
            metricsRepository.save(m);
        } catch (Exception ignored) {
        }
        server.setStatus(ServerStatus.OFFLINE);
        server.setLastUpdate(LocalDateTime.now());
        serverRepository.save(server);
        log.info("[健康检测] 服务器 {} 离线: {}", server.getId(), reason);
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0d; }
    }

    private double clamp(double v) {
        if (v < 0) return 0d; if (v > 100) return 100d; return v;
    }

    private double nonNegative(double v) { return v < 0 ? 0d : v; }

    // 生成跨发行版的指标采集脚本
    private String buildMetricsCommand() {
        // 说明：
        // - CPU：通过两次读取/proc/stat 计算使用率
        // - MEM：通过free -m 计算(used/total)*100
        // - DISK：df -P / 的使用百分比
        // - NET：聚合非lo接口，采样1秒计算kbps
        String script =
            "set -e; " +
            // CPU first sample
            "t1=$(awk '/^cpu /{print $2+$3+$4+$5+$6+$7+$8}' /proc/stat); " +
            "i1=$(awk '/^cpu /{print $5}' /proc/stat); " +
            // NET first sample
            "rx1=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){rx+=$3}} END{print rx+0}' /proc/net/dev); " +
            "tx1=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){tx+=$11}} END{print tx+0}' /proc/net/dev); " +
            // sleep 1 second
            "sleep 1; " +
            // CPU second sample
            "t2=$(awk '/^cpu /{print $2+$3+$4+$5+$6+$7+$8}' /proc/stat); " +
            "i2=$(awk '/^cpu /{print $5}' /proc/stat); " +
            // NET second sample
            "rx2=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){rx+=$3}} END{print rx+0}' /proc/net/dev); " +
            "tx2=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){tx+=$11}} END{print tx+0}' /proc/net/dev); " +
            // CPU usage calc
            "dt=$((t2 - t1)); di=$((i2 - i1)); cpu=0; if [ $dt -gt 0 ]; then cpu=$(awk -v dt=$dt -v di=$di 'BEGIN{print (dt-di)/dt*100}'); fi; " +
            // MEM usage
            "mem=$(free -m 2>/dev/null | awk '/^Mem:/{if($2>0) print $3/$2*100; else print 0}'); if [ -z \"$mem\" ]; then mem=$(awk '/MemTotal/{mt=$2} /MemAvailable/{ma=$2} END{if(mt>0) print (mt-ma)/mt*100; else print 0}' /proc/meminfo); fi; " +
            // DISK usage for /
            "disk=$(df -P / 2>/dev/null | awk 'NR==2{gsub(\"%\",\"\",$5); print $5+0}'); if [ -z \"$disk\" ]; then disk=0; fi; " +
            // NET rates kbps
            "rxk=$(awk -v r1=$rx1 -v r2=$rx2 'BEGIN{print (r2-r1)/1024*8}'); txk=$(awk -v t1=$tx1 -v t2=$tx2 'BEGIN{print (t2-t1)/1024*8}'); " +
            // print values with 2 decimals and '|' delimiter
            "printf '%.2f|%.2f|%.2f|%.2f|%.2f\\n' \"$cpu\" \"$mem\" \"$disk\" \"$rxk\" \"$txk\"";
        return script;
    }
}
