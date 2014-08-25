package ems.server.domain;


import org.springframework.data.annotation.Id;

/**
 * DeviceSpecification
 * Created by thebaz on 25/08/14.
 */
public class DeviceSpecification {
    @Id
    private String id;

    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
