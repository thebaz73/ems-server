package ems.server.data;


import ems.server.domain.TaskConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * TaskConfigurationRepository
 * Created by thebaz on 9/16/14.
 */
public interface TaskConfigurationRepository extends MongoRepository<TaskConfiguration, String>, PagingAndSortingRepository<TaskConfiguration, String> {
    /**
     * Find a slice of events give device id, page number, size and sort
     *
     * @param deviceId device id
     * @param pageable page number, size and sort
     * @return task configuration
     */
    Page<TaskConfiguration> findByDeviceId(String deviceId, Pageable pageable);

    /**
     * Find a slice of events give device id
     *
     * @param deviceId device id
     * @return task configuration
     */
    List<TaskConfiguration> findByDeviceId(String deviceId);
}
