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
    private Specification specification;

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
}
