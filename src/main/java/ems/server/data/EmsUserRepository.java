package ems.server.data;
/*
 * Copyright
 */

import ems.server.domain.EmsUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * EmsUserRepository
 * <p/>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 3:32 PM
 */
public interface EmsUserRepository extends MongoRepository<EmsUser, String> {
    /**
     * Finds @EmsUser given its username
     *
     * @param username user name
     * @return list of @EmsUser
     */
    List<EmsUser> findByUsername(String username);
}
