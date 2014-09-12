package ems.server.domain;

import org.springframework.data.annotation.Id;

/**
 * Event
 * Created by thebaz on 31/08/14.
 */
public class Event {
    @Id
    private String id;
    private Long timestamp;
    private EventType eventType;
    private EventSeverity eventSeverity;
    private String deviceId;
    private String deviceName;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventSeverity getEventSeverity() {
        return eventSeverity;
    }

    public void setEventSeverity(EventSeverity eventSeverity) {
        this.eventSeverity = eventSeverity;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
