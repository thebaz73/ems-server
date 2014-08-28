package ems.server.domain;

import org.springframework.data.annotation.Id;

/**
 * Protocol
 * Created by thebaz on 8/28/14.
 */
public class Protocol {
    @Id
    private String id;

    private String name;

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
}
