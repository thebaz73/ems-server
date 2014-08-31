package ems.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * EventSeverity
 * Created by thebaz on 31/08/14.
 */
public enum EventSeverity {
    EVENT_INFO("info"),
    EVENT_WARN("warn"),
    EVENT_ERROR("error"),
    EVENT_FATAL("fatal");

    public static final EventSeverity[] ALL = {EVENT_INFO, EVENT_WARN, EVENT_ERROR, EVENT_FATAL};

    private final String name;

    private EventSeverity(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static EventSeverity forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for type");
        }
        if (name.toUpperCase().equals("info")) {
            return EVENT_INFO;
        } else if (name.toUpperCase().equals("warn")) {
            return EVENT_WARN;
        } else if (name.toUpperCase().equals("error")) {
            return EVENT_ERROR;
        } else if (name.toUpperCase().equals("fatal")) {
            return EVENT_FATAL;
        }
        throw new IllegalArgumentException("Name \"" + name + "\" does not correspond to any Feature");
    }

    public String getName() {
        return this.name;
    }

    @JsonValue
    @Override
    public String toString() {
        return getName();
    }
}
