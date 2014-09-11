package ems.server.data;

import ems.server.domain.Device;
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
    Page<Event> findByDevice(Device device, Pageable pageable);
    List<Event> findByDevice(Device device);
}
