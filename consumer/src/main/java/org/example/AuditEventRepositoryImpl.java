package org.example;

import org.example.model.AuditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AuditEventRepositoryImpl implements AuditEventRepository {
    private static final String TEXT = "text";
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public AuditEventRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("audit_event")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Number save(AuditEvent auditEvent) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("resource_type", auditEvent.getResourceType());
        parameters.put("event", auditEvent.getEvent());
        parameters.put("participant", ConsumerUtil.createSqlArray(jdbcTemplate, TEXT, auditEvent.getParticipant()));
        parameters.put("source", auditEvent.getSource());
        parameters.put("object", ConsumerUtil.createSqlArray(jdbcTemplate, TEXT, auditEvent.getObject()));

        return simpleJdbcInsert.executeAndReturnKey(parameters);
    }

    @Override
    public Optional<AuditEvent> findById(Number id) {
        String sql = """
           SELECT id, resource_type, event, participant, source, object
           FROM audit_event
           WHERE id = ?;
           """;
        return jdbcTemplate.query(sql,new AuditEventRowMapper(),id)
                .stream()
                .findFirst();
    }

    @Override
    public List<AuditEvent> findAll() {
        String sql = """
           SELECT id, resource_type, event, participant, source, object
           FROM audit_event;
           """;
        return jdbcTemplate.query(sql,new AuditEventRowMapper());
    }
}
