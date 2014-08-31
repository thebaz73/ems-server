package ems.driver.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Status
 * Created by thebaz on 31/08/14.
 */
public enum Status {

    OK("ok"),
    WARN("warn"),
    ERROR("error"),
    UNKNOWN("unknown");
    private static Map<String, Status> constants = new HashMap<String, Status>();

    static {
        for (Status c : values()) {
            constants.put(c.value, c);
        }
    }

    private final String value;

    private Status(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Status fromValue(String value) {
        Status constant = constants.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

}

