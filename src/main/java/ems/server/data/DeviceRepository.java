package ems.server.data;

import ems.server.domain.Device;
import ems.server.domain.Specification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * DeviceRepository
 * Created by thebaz on 25/08/14.
 */
public interface DeviceRepository extends MongoRepository<Device, String>, PagingAndSortingRepository<Device, String> {
    /**
     * Finds @Device given its @Specification
     *
     * @param specification device specification
     * @return list of @Device
     */
    List<Device> findBySpecification(Specification specification);
    /**
     * Finds @Device given its name
     *
     * @param name device name
     * @return list of @Device
     */
    List<Device> findByName(String name);
}
