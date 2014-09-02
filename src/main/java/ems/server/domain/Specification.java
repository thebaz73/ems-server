package ems.server.domain;


import ems.driver.domain.DriverType;
import ems.protocol.domain.ProtocolType;
import org.springframework.data.annotation.Id;

/**
 * Specification
 * Created by thebaz on 25/08/14.
 */
public class Specification {
    @Id
    private String id;

    private String name;
    private DriverType driverType;
    private String driver;
    private ProtocolType protocolType;
    private String protocol;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DriverType getDriverType() {
        return driverType;
    }

    public void setDriverType(DriverType driverType) {
        this.driverType = driverType;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
