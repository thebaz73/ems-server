package ems.server.utils;

import ems.server.domain.Device;
import ems.server.domain.Event;
import ems.server.domain.EventSeverity;
import ems.server.domain.EventType;

import java.util.Date;

/**
 * EventHelper
 * Created by thebaz on 31/08/14.
 */
public class EventHelper {
    public static void addEvents(Device device) {
        for (EventType eventType : EventType.ALL) {
            for (EventSeverity eventSeverity : EventSeverity.ALL) {
                Event event = new Event();
                event.setTimestamp(System.currentTimeMillis());
                event.setEventType(eventType);
                event.setEventSeverity(eventSeverity);
                event.setDevice(device);
                event.setDescription("Event of type:" + eventType + " at: " +
                        new Date(event.getTimestamp()) + " with servery: " +
                        eventSeverity + " for device: " + device.getName());
            }
        }
    }
}
