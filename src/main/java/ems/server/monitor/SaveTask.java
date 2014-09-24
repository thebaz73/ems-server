package ems.server.monitor;

import ems.driver.domain.common.Status;
import ems.server.business.DeviceManager;
import ems.server.domain.Device;
import ems.server.domain.Event;
import ems.server.domain.EventSeverity;
import ems.server.domain.EventType;
import ems.server.protocol.ProtocolEnquirer;
import ems.server.utils.EnumAwareConvertUtilsBean;
import ems.server.utils.EventHelper;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
* SaveTask
* Created by thebaz on 24/09/14.
*/
public class SaveTask implements Runnable {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private final BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new EnumAwareConvertUtilsBean());
    private Device device;
    private DeviceManager deviceManager;
    private ProtocolEnquirer enquirer;

    SaveTask() {
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setDeviceManager(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    public void setEnquirer(ProtocolEnquirer enquirer) {
        this.enquirer = enquirer;
    }

    @Override
    public void run() {
        List<Event> events = enquirer.getEvents();
        try {
            Status status = events.isEmpty() ? Status.OK : Status.WARN;
            for (Event event : events) {
                EventHelper.getInstance().addEvent(event);
                if(event.getEventSeverity().equals(EventSeverity.EVENT_ERROR)) {
                    status = Status.ERROR;
                }
            }
            beanUtilsBean.setProperty(device.getDriver(), "status", status);
            deviceManager.editDevice(device);
        } catch (Throwable e) {
            EventType eventType = EventType.EVENT_CONFIGURATION;
            EventSeverity eventSeverity = EventSeverity.EVENT_FATAL;
            String description = "Event of type: \'" + eventType + "\' at: " +
                    format.format(new Date(System.currentTimeMillis())) + " with severity: \'" +
                    eventSeverity + "\' for device: " + device.getName() + ". Error executing script: " + e.getMessage();
            EventHelper.getInstance().addEvent(device, eventType, eventSeverity, description);
            e.printStackTrace();
        }
    }
}
