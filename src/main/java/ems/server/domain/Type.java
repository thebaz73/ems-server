package ems.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Type
 * Created by thebaz on 8/28/14.
 */
public enum Type {
    TYPE_PROBE("probe"),
    TYPE_ROUTER("router"),
    TYPE_MODULATOR("modulator");

    public static final Type[] ALL = {TYPE_PROBE, TYPE_ROUTER, TYPE_MODULATOR};

    private final String name;

    private Type(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static Type forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for type");
        }
        if (name.toUpperCase().equals("probe")) {
            return TYPE_PROBE;
        } else if (name.toUpperCase().equals("router")) {
            return TYPE_ROUTER;
        } else if (name.toUpperCase().equals("modulator")) {
            return TYPE_MODULATOR;
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
