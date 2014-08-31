package ems.server.domain;
/*
 * Copyright
 */

import org.springframework.data.annotation.Id;

/**
 * EmsRole
 * <p>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 2:38 PM
 */
public class EmsRole {
    @Id
    private String id;

    private Role role;

    public EmsRole() {
    }

    public EmsRole(String name) {
        this.role = Role.forName(name);
    }

    public EmsRole(Role role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
