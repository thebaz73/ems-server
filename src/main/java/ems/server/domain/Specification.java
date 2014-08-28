package ems.server.domain;


import ems.driver.domain.Driver;
import org.springframework.data.annotation.Id;

/**
 * Specification
 * Created by thebaz on 25/08/14.
 */
public class Specification {
    @Id
    private String id;

    private String name;
    private Type type;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
