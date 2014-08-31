package ems.server.data;

import ems.server.domain.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * EventRepository
 * Created by thebaz on 31/08/14.
 */
public interface EventRepository extends MongoRepository<Event, String> {
}
