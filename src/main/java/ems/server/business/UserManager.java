package ems.server.business;
/*
 * Copyright
 */

import ems.server.data.EmsUserRepository;
import ems.server.domain.EmsRole;
import ems.server.domain.EmsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager
 * <p/>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 11:39 AM
 */
@Component
public class UserManager extends JdbcUserDetailsManager {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private EmsUserRepository emsUserRepository;

    @Autowired
    public UserManager(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    public PasswordEncoder getEncoder() {
        return encoder;
    }

    /**
     * Find all users
     *
     * @return user list
     */
    public List<EmsUser> findAllUsers() {
/*
        List<EmsUser> allUsers = new ArrayList<EmsUser>();
        List<String> allGroups = findAllGroups();
        for (String group : allGroups) {
            List<String> userNames = findUsersInGroup(group);
            for (String userName : userNames) {
                UserDetails userDetails = loadUserByUsername(userName);
                EmsUser user = new EmsUser();
                user.setUsername(userDetails.getUsername());
                user.setPassword(userDetails.getPassword());
                for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
                    EmsRole role = new EmsRole();
                    role.setRole(Role.forName(grantedAuthority.getAuthority()));
                    user.getRoles().add(role);
                }
                allUsers.add(user);
            }
        }

        return allUsers;
*/
        return emsUserRepository.findAll();
    }

    public boolean userCreated(String username) {
        return !emsUserRepository.findByUsername(username).isEmpty();
    }

    /**
     * Creates a new user
     *
     * @param emsUser to be created
     */
    public void createUser(EmsUser emsUser) {
        allocateUser(emsUser);

        emsUserRepository.save(emsUser);
    }

    public void allocateUser(EmsUser emsUser) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        for (EmsRole emsRole : emsUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(emsRole.getRole().getName()));
        }
        User user = new User(emsUser.getUsername(), encoder.encode(emsUser.getPassword()),
                authorities);

        createUser(user);
    }

    /**
     * Deletes a user
     *
     * @param emsUser to be deleted
     */
    public void deleteUser(EmsUser emsUser) {
        for (EmsUser user : emsUserRepository.findByUsername(emsUser.getUsername())) {
            emsUserRepository.delete(user);
        }
        deleteUser(emsUser.getUsername());
    }

    /**
     * Delete a user given its username
     *
     * @param username username
     */
    public void deleteUserByUsername(String username) {
        for (EmsUser user : emsUserRepository.findByUsername(username)) {
            emsUserRepository.delete(user);
        }
        deleteUser(username);
    }
}
