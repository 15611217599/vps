package com.vps.vpsserver.repository;

import com.vps.vpsserver.entity.ServerMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerMetricsRepository extends JpaRepository<ServerMetrics, Long> {
}
