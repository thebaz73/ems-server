package ems.server.admin;
/*
 * Copyright
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * UserManager
 * <p/>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 11:39 AM
 */
@Component
public class UserManager extends JdbcUserDetailsManager {
    @Autowired
    public UserManager(DataSource dataSource) {
        super.setDataSource(dataSource);
    }
}
