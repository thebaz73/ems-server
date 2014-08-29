package ems.server.web;

import ems.server.data.DeviceRepository;
import ems.server.data.SpecificationRepository;
import ems.server.domain.Device;
import ems.server.domain.Specification;
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
public class InventoryController {
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    SpecificationRepository specificationRepository;

    @RequestMapping(value = "/inventory/devices", method = GET)
    List<Device> devices() {
        return deviceRepository.findAll();
    }

    @RequestMapping(value = "/inventory/specifications", method = GET)
    List<Specification> specifications() {
        return specificationRepository.findAll();
    }
}
