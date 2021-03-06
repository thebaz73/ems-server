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
    /**
     * Find a slice of events give device id, page number, size and sort
     *
     * @param deviceId device id
     * @param pageable page number, size and sort
     * @return events
     */
    Page<Event> findByDeviceId(String deviceId, Pageable pageable);

    /**
     * Find a slice of events give device id
     *
     * @param deviceId device id
     * @return events
     */
    List<Event> findByDeviceId(String deviceId);
}
