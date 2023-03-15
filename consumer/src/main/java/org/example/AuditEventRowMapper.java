package org.example;

import org.example.model.AuditEvent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class AuditEventRowMapper implements RowMapper<AuditEvent> {
    @Override
    public AuditEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AuditEvent.newBuilder()
                .setResourceType(rs.getString("resource_type"))
                .setEvent(rs.getString("event"))
                .setParticipant(Arrays.asList((String[]) rs.getArray("participant").getArray()))
                .setSource(rs.getString("source"))
                .setObject(Arrays.asList((String[]) rs.getArray("object").getArray()))
                .build();
    }
}
