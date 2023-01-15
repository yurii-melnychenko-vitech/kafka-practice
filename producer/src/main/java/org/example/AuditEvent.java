package org.example;

import java.util.List;

public class AuditEvent {
    private String resourceType;
    private String id;
    private String event;
    private List<String> participant;
    private String source;
    private List<String> object;

    public AuditEvent() {
    }

    public AuditEvent(String resourceType, String id, String event, List<String> participant, String source, List<String> object) {
        this.resourceType = resourceType;
        this.id = id;
        this.event = event;
        this.participant = participant;
        this.source = source;
        this.object = object;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<String> getParticipant() {
        return participant;
    }

    public void setParticipant(List<String> participant) {
        this.participant = participant;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getObject() {
        return object;
    }

    public void setObject(List<String> object) {
        this.object = object;
    }
}
