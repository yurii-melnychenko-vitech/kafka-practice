package org.example;

import org.example.model.AuditEvent;
import java.util.List;
import java.util.Optional;

public interface AuditEventRepository {
    Number save(AuditEvent auditEvent);
    Optional<AuditEvent> findById(Number id);
    List<AuditEvent> findAll();
}
