package ems.server.utils;

import ems.server.data.EventRepository;
import ems.server.domain.Device;
import ems.server.domain.Event;
import ems.server.domain.EventSeverity;
import ems.server.domain.EventType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * EventHelper
 * Created by thebaz on 31/08/14.
 */
public class EventHelper {
    private final static TimeZone tz = TimeZone.getTimeZone("UTC");
    private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        format.setTimeZone(tz);
    }

    public static void addEvents(EventRepository eventRepository, Device device) {
        for (EventType eventType : EventType.ALL) {
            for (EventSeverity eventSeverity : EventSeverity.ALL) {
                Event event = new Event();
                event.setTimestamp(System.currentTimeMillis());
                event.setEventType(eventType);
                event.setEventSeverity(eventSeverity);
                event.setDevice(device);
                event.setDescription("Event of type: \'" + eventType + "\' at: " +
                        format.format(new Date(event.getTimestamp())) + " with serverity: \'" +
                        eventSeverity + "\' for device: " + device.getName());
                eventRepository.save(event);
            }
        }
    }
}
