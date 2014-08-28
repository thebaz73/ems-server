package ems.server.data;

import ems.server.domain.Specification;
import ems.server.domain.Type;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * SpecificationRepository
 * Created by thebaz on 25/08/14.
 */
public interface SpecificationRepository extends MongoRepository<Specification, String> {
    List<Specification> findByType(Type type);
}
