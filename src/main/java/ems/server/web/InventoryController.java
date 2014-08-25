package ems.server.web;

import ems.server.data.DeviceRepository;
import ems.server.data.DeviceSpecificationRepository;
import ems.server.domain.Device;
import ems.server.domain.DeviceSpecification;
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
    DeviceSpecificationRepository deviceSpecificationRepository;

    @RequestMapping(value = "devices", method = GET, produces = "application/json")
    List<Device> devices() {
        return deviceRepository.findAll();
    }

    @RequestMapping(value = "specifications", method = GET, produces = "application/json")
    List<DeviceSpecification> specifications() {
        return deviceSpecificationRepository.findAll();
    }
}
