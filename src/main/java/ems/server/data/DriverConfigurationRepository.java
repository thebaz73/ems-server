package ems.server.data;


import ems.server.domain.DriverConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * DriverConfigurationRepository
 * Created by thebaz on 9/4/14.
 */
public interface DriverConfigurationRepository extends MongoRepository<DriverConfiguration, String> {
    /**
     * Finds all saved @DriverConfiguration given its @DriverConfiguration#specificationId
     *
     * @param specificationId specification id
     * @return list of @DriverConfiguration
     */
    List<DriverConfiguration> findBySpecificationId(String specificationId);
    /**
     * Deletes all saved @DriverConfiguration given its @DriverConfiguration#specificationId
     *
     * @param specificationId specification id
     * @return list of @DriverConfiguration
     */
    List<DriverConfiguration> deleteBySpecificationId(String specificationId);
}
