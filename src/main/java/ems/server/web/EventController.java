package ems.server.web;

import ems.server.data.EventRepository;
import ems.server.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * InventoryController
 * Created by thebaz on 25/08/14.
 */
@RestController
public class EventController {
    @Autowired
    EventRepository eventRepository;

    @RequestMapping(value = "/inventory/events", method = GET)
    List<Event> devices() {
        return eventRepository.findAll();
    }
}
