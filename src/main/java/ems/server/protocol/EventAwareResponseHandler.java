package ems.server.protocol;


import ems.server.domain.Device;
import ems.server.domain.EventSeverity;
import ems.server.domain.EventType;
import ems.server.utils.EventHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * EventAwareResponseHandler
 * Created by thebaz on 9/15/14.
 */
public class EventAwareResponseHandler implements ResponseHandler {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private final Device device;

    public EventAwareResponseHandler(Device device) {
        this.device = device;
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void onTimeout(String variable) {
        EventHelper.getInstance().addEvent(device, EventType.EVENT_NETWORK, EventSeverity.EVENT_WARN);
    }

    @Override
    public void onSuccess(String variable) {
        //do nothing
    }

    @Override
    public void onError(String variable, int errorCode, String errorDescription) {
        EventSeverity eventSeverity = EventSeverity.EVENT_ERROR;
        EventType eventType = EventType.EVENT_PROTOCOL;
        String description = "Event of type: \'" + eventType + "\' at: " +
                format.format(new Date(System.currentTimeMillis())) + " with severity: \'" +
                eventSeverity + "\' for device: " + device.getName() + ". Error code:" +
                errorCode + ", Error description: " + errorDescription;
        EventHelper.getInstance().addEvent(device, eventType, eventSeverity, description);
    }
}
