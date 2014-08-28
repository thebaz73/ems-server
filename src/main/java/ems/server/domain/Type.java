package ems.server.domain;

/**
 * Type
 * Created by thebaz on 8/28/14.
 */
public enum Type {
    TYPE_PROBE("TYPE_PROBE"),
    TYPE_ROUTER("TYPE_ROUTER"),
    TYPE_MODULATOR("TYPE_MODULATOR");

    public static final Type[] ALL = { TYPE_PROBE, TYPE_ROUTER, TYPE_MODULATOR };

    private final String name;

    public static Type forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for type");
        }
        if (name.toUpperCase().equals("TYPE_PROBE")) {
            return TYPE_PROBE;
        } else if (name.toUpperCase().equals("TYPE_ROUTER")) {
            return TYPE_ROUTER;
        } else if (name.toUpperCase().equals("TYPE_MODULATOR")) {
            return TYPE_MODULATOR;
        }
        throw new IllegalArgumentException("Name \"" + name + "\" does not correspond to any Feature");
    }

    private Type(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
