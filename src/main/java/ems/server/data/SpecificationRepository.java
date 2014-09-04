package ems.server.data;

import ems.driver.domain.DriverType;
import ems.server.domain.Specification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * SpecificationRepository
 * Created by thebaz on 25/08/14.
 */
public interface SpecificationRepository extends MongoRepository<Specification, String> {
    /**
     * Finds @Specification given its @DriverType
     *
     * @param driverType specification driver type
     * @return list of @Specification
     */
    List<Specification> findByDriverType(DriverType driverType);
}
