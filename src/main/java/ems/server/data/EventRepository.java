package ems.server.data;

import ems.server.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * EventRepository
 * Created by thebaz on 31/08/14.
 */
public interface EventRepository extends MongoRepository<Event, String>, PagingAndSortingRepository<Event, String> {
    Page<Event> findByDeviceId(String deviceId, Pageable pageable);
    List<Event> findByDeviceId(String deviceId);
}
