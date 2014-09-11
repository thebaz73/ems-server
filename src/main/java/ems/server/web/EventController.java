package ems.server.web;

import ems.server.data.DeviceRepository;
import ems.server.data.EventRepository;
import ems.server.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * InventoryController
 * Created by thebaz on 25/08/14.
 */
@RestController
public class EventController {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @RequestMapping(value = "/inventory/events", params = {"page", "pageSize"}, method = GET)
    Page<Event> events(@RequestParam(value = "page") int page, @RequestParam(value = "pageSize") int pageSize) {
        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.DESC, "timestamp");
        return eventRepository.findAll(pageable);
    }

    @RequestMapping(value = "/inventory/events/{id}", params = {"page", "pageSize"}, method = GET)
    Page<Event> eventsByDevice(@PathVariable("id") String id, @RequestParam(value = "page") int page, @RequestParam(value = "pageSize") int pageSize) {
        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.DESC, "timestamp");
        return eventRepository.findByDevice(deviceRepository.findOne(id), pageable);
    }
}
