package ems.server.utils;

import ems.server.data.EventRepository;
import ems.server.domain.Device;
import ems.server.domain.Event;
import ems.server.domain.EventSeverity;
import ems.server.domain.EventType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private final Log logger = LogFactory.getLog(EventHelper.class);
    private final TimeZone tz = TimeZone.getTimeZone("UTC");
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private static EventHelper instance;

    @Autowired
    EventRepository eventRepository;

    private EventHelper() {
        instance = this;
        format.setTimeZone(tz);
    }

    public static EventHelper getInstance() {
        return instance;
    }

    public void addEvents(Device device) {
        for (EventType eventType : EventType.ALL) {
            for (EventSeverity eventSeverity : EventSeverity.ALL) {
                addEvent(device, eventType, eventSeverity);
            }
        }
    }

    public void addEvent(Device device, EventType eventType, EventSeverity eventSeverity) {
        String description = "Event of type: \'" + eventType + "\' at: " +
                format.format(new Date(System.currentTimeMillis())) + " with severity: \'" +
                eventSeverity + "\' for device: " + device.getName();
        addEvent(device, eventType, eventSeverity, description);
    }

    public void addEvent(Device device, EventType eventType, EventSeverity eventSeverity, String description) {
        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setEventType(eventType);
        event.setEventSeverity(eventSeverity);
        event.setDeviceId(device.getId());
        event.setDeviceName(device.getName());
        event.setDescription(description);
        eventRepository.save(event);
    }
}
