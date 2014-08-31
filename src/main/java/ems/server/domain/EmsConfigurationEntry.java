package ems.server.domain;
/*
 * Copyright
 */

import org.springframework.data.annotation.Id;

/**
 * EmsConfigurationEntry
 * <p>
 * User: mbazzoni
 * Date: 8/28/14
 * Time: 9:17 AM
 */
public class EmsConfigurationEntry {
    @Id
    private String id;
    private String key;
    private Object value;

    public EmsConfigurationEntry() {
    }

    public EmsConfigurationEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
