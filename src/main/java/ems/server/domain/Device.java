package ems.server.domain;

import ems.driver.common.Location;
import ems.driver.common.Status;
import org.springframework.data.annotation.Id;

/**
 * Device
 * Created by thebaz on 25/08/14.
 */
public class Device {
    @Id
    private String id;

    private String name;
    private Specification specification;
    private Status status;
    private Location location;

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

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
