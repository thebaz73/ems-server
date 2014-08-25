package ems.server.data;

import ems.server.domain.DeviceSpecification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * DeviceSpecificationRepository
 * Created by thebaz on 25/08/14.
 */
public interface DeviceSpecificationRepository extends MongoRepository<DeviceSpecification, String> {
    List<DeviceSpecification> findByType(String type);
}
