package ems.server.domain;
/*
 * Copyright
 */

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * EmsUser
 * <p/>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 2:26 PM
 */
public class EmsUser {
    @Id
    private String id;

    private String name;
    private String email;
    private String username;
    private String password;
    private final List<EmsRole> roles = new ArrayList<EmsRole>();

    public EmsUser() {
    }

    public EmsUser(String username, String password, Collection<EmsRole> roles) {
        this.username = username;
        this.password = password;
        for (EmsRole role : roles) {
            this.roles.add(role);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<EmsRole> getRoles() {
        return roles;
    }
}
