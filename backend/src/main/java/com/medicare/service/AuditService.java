package com.medicare.service;

import com.medicare.dto.AuditLogDto;
import com.medicare.entity.AuditLog;
import com.medicare.entity.User;
import com.medicare.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditService {


    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String ressource, String detail, User user) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .ressource(ressource)
                .detail(detail)
                .build();
        auditLogRepository.save(log);
    }

    public void logWithIp(String action, String ressource, String detail, User user, String ip) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .ressource(ressource)
                .detail(detail)
                .ip(ip)
                .build();
        auditLogRepository.save(log);
    }

    public Page<AuditLogDto> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(AuditLogDto::new);
    }
}
