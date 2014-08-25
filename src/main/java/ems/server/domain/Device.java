package ems.server.domain;

import org.springframework.data.annotation.Id;

/**
 * Device
 * Created by thebaz on 25/08/14.
 */
public class Device {
    @Id
    private String id;

    private String name;

    private DeviceSpecification deviceSpecification;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceSpecification getDeviceSpecification() {
        return deviceSpecification;
    }

    public void setDeviceSpecification(DeviceSpecification deviceSpecification) {
        this.deviceSpecification = deviceSpecification;
    }
}
