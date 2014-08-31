package ems.server.data;
/*
 * Copyright
 */

import ems.server.domain.EmsConfigurationEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * EmsConfigurationRepository
 * <p>
 * User: mbazzoni
 * Date: 8/28/14
 * Time: 9:18 AM
 */
public interface EmsConfigurationRepository extends MongoRepository<EmsConfigurationEntry, String> {
    List<EmsConfigurationEntry> findByKey(String key);
}
