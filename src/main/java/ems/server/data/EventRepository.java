package ems.server.data;

import ems.server.domain.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * EventRepository
 * Created by thebaz on 31/08/14.
 */
public interface EventRepository extends MongoRepository<Event, String>, PagingAndSortingRepository<Event, String> {
}
