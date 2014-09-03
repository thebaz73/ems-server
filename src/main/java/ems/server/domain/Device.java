package ems.server.domain;

import ems.driver.domain.Driver;
import ems.protocol.domain.Protocol;
import org.springframework.data.annotation.Id;

/**
 * Device
 * Created by thebaz on 25/08/14.
 */
public class Device {
    @Id
    private String id;

    private String name;
    private String address;
    private Integer port;
    private Specification specification;
    private Driver driver;
    private Protocol protocol;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}
