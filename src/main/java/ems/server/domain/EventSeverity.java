package ems.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * EventSeverity
 * Created by thebaz on 31/08/14.
 */
public enum EventSeverity {
    EVENT_INFO("EVENT_INFO"),
    EVENT_WARN("EVENT_WARN"),
    EVENT_ERROR("EVENT_ERROR"),
    EVENT_FATAL("EVENT_FATAL");

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
        if (name.toUpperCase().equals("EVENT_INFO")) {
            return EVENT_INFO;
        } else if (name.toUpperCase().equals("EVENT_WARN")) {
            return EVENT_WARN;
        } else if (name.toUpperCase().equals("EVENT_ERROR")) {
            return EVENT_ERROR;
        } else if (name.toUpperCase().equals("EVENT_FATAL")) {
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
