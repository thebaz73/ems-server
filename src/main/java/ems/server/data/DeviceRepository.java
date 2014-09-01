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
    List<Device> findBySpecification(Specification specification);

    List<Device> findByName(String name);
}
