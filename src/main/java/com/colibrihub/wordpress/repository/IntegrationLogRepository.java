package com.colibrihub.wordpress.repository;

import com.colibrihub.wordpress.entity.IntegrationLog;
import com.colibrihub.wordpress.enums.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long> {
    // Spring Data JPA genera la implementación automáticamente
    // basándose en el nombre del metodo (Query Methods)
    List<IntegrationLog> findByTargetSystem(String targetSystem);
    List<IntegrationLog> findByStatus(LogStatus status);
    List<IntegrationLog> findByTargetSystemAndStatus(String system, LogStatus status);
    // Puedes agregar @Query para consultas custom:
    // @Query("SELECT l FROM IntegrationLog l WHERE l.timestamp > :date")
    // List<IntegrationLog> findRecentLogs(@Param("date") LocalDateTime date);
}
