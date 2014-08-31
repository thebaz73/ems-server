package ems.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * EventType
 * Created by thebaz on 31/08/14.
 */
public enum EventType {
    EVENT_NETWORK("EVENT_NETWORK"),
    EVENT_CONFIGURATION("EVENT_CONFIGURATION"),
    EVENT_PROTOCOL("EVENT_PROTOCOL");

    public static final EventType[] ALL = {EVENT_NETWORK, EVENT_CONFIGURATION, EVENT_PROTOCOL};

    private final String name;

    private EventType(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static EventType forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for type");
        }
        if (name.toUpperCase().equals("EVENT_NETWORK")) {
            return EVENT_NETWORK;
        } else if (name.toUpperCase().equals("EVENT_CONFIGURATION")) {
            return EVENT_CONFIGURATION;
        } else if (name.toUpperCase().equals("EVENT_PROTOCOL")) {
            return EVENT_PROTOCOL;
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
