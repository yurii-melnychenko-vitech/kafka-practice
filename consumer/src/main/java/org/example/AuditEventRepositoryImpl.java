package org.example;

import org.example.model.AuditEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditEventRepositoryImpl implements AuditEventRepository{
    private static final String TEXT = "text";
    private final JdbcTemplate jdbcTemplate;

    public AuditEventRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(AuditEvent auditEvent) {
        Object[] params = new Object[] {
                auditEvent.getResourceType(),
                auditEvent.getEvent(),
                ConsumerUtil.createSqlArray(jdbcTemplate, TEXT, auditEvent.getParticipant()),
                auditEvent.getSource(),
                ConsumerUtil.createSqlArray(jdbcTemplate, TEXT, auditEvent.getObject())
        };
        jdbcTemplate.update(
                "INSERT INTO audit_event (resource_type, event, participant, source, object) VALUES (?, ?, ?, ?, ?)",
                params
        );
    }
}
