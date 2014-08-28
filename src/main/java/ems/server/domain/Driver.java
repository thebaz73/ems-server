package ems.server.domain;

import org.springframework.data.annotation.Id;

/**
 * Driver
 * Created by thebaz on 8/28/14.
 */
public class Driver {
    @Id
    private String id;

    private String name;
    private Type type;
    private String resource;

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
}
