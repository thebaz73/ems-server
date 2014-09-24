package ems.server.utils;

import ems.server.data.EventRepository;
import ems.server.domain.Device;
import ems.server.domain.Event;
import ems.server.domain.EventSeverity;
import ems.server.domain.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * EventHelper
 * Created by thebaz on 31/08/14.
 */
@Component
public class EventHelper {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private static EventHelper instance;

    @Autowired
    EventRepository eventRepository;

    private EventHelper() {
        instance = this;
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Singleton instance method to access
     * helper class
     *
     * @return class instance
     */
    public static synchronized EventHelper getInstance() {
        return instance;
    }

    /**
     * Helper method to add an event to repository
     *
     * @param device device event is related to
     */
    public void addEvents(Device device) {
        for (EventType eventType : EventType.ALL) {
            for (EventSeverity eventSeverity : EventSeverity.ALL) {
                addEvent(device, eventType, eventSeverity);
            }
        }
    }

    /**
     * Helper method to add an event to repository
     *
     * @param device device event is related to
     * @param eventType event type
     * @param eventSeverity event severity
     */
    public void addEvent(Device device, EventType eventType, EventSeverity eventSeverity) {
        String description = "Event of type: \'" + eventType + "\' at: " +
                format.format(new Date(System.currentTimeMillis())) + " with severity: \'" +
                eventSeverity + "\' for device: " + device.getName();
        addEvent(device, eventType, eventSeverity, description);
    }

    /**
     * Helper method to add an event to repository
     *
     * @param device device event is related to
     * @param eventType event type
     * @param eventSeverity event severity
     * @param description event description
     */
    public void addEvent(Device device, EventType eventType, EventSeverity eventSeverity, String description) {
        Event event = createEvent(device, eventType, eventSeverity, description);
        addEvent(event);
    }

    /**
     * Helper method to add an event to repository
     *
     * @param event event to be added
     */
    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    /**
     * Helper method to create a event
     *
     * @param device device event is related to
     * @param eventType event type
     * @param eventSeverity event severity
     * @param description event description
     * @return created event
     */
    public Event createEvent(Device device, EventType eventType, EventSeverity eventSeverity, String description) {
        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setEventType(eventType);
        event.setEventSeverity(eventSeverity);
        event.setDeviceId(device.getId());
        event.setDeviceName(device.getName());
        event.setDescription(description);
        return event;
    }
}
