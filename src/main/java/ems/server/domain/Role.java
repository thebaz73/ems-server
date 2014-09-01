package ems.server.domain;
/*
 * Copyright
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Role
 * <p/>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 2:22 PM
 */
public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MANAGER("ROLE_MANAGER");

    public static final Role[] ALL = {ROLE_USER, ROLE_ADMIN, ROLE_MANAGER};

    private final String name;

    private Role(final String name) {
        this.name = name;
    }

    @JsonCreator
    public static Role forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for role");
        }
        if (name.toUpperCase().equals("ROLE_USER")) {
            return ROLE_USER;
        } else if (name.toUpperCase().equals("ROLE_ADMIN")) {
            return ROLE_ADMIN;
        } else if (name.toUpperCase().equals("ROLE_MANAGER")) {
            return ROLE_MANAGER;
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
