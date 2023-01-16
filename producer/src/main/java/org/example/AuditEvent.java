package org.example;

import java.util.List;

record AuditEvent(
        String resourceType,
        String id,
        String event,
        List<String> participant,
        String source,
        List<String> object
) {}
