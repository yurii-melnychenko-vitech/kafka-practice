package org.example;

import org.example.model.AuditEvent;

public interface AuditEventRepository {
    void save(AuditEvent auditEvent);
}
