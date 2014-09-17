package ems.server.web;

import ems.server.data.DeviceRepository;
import ems.server.data.SpecificationRepository;
import ems.server.domain.Device;
import ems.server.domain.Specification;
import ems.server.monitor.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * InventoryController
 * Created by thebaz on 25/08/14.
 */
@RestController
public class InventoryController {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SpecificationRepository specificationRepository;
    @Autowired
    private MonitorService monitorService;

    @RequestMapping(value = "/inventory/device/{id}", method = GET)
    Device device(@PathVariable("id") String id) {
        return deviceRepository.findOne(id);
    }

    @RequestMapping(value = "/inventory/devices", method = GET)
    List<Device> devices() {
        return deviceRepository.findAll();
    }

    @RequestMapping(value = "/inventory/devices", params = {"page", "pageSize"}, method = GET)
    Page<Device> devices(@RequestParam(value = "page") int page, @RequestParam(value = "pageSize") int pageSize) {
        Pageable pageable = new PageRequest(page, pageSize);
        return deviceRepository.findAll(pageable);
    }

    @RequestMapping(value = "/inventory/specifications", method = GET)
    List<Specification> specifications() {
        return specificationRepository.findAll();
    }

    @RequestMapping(value = "/monitor/start", method = GET)
    public Map<String, Object> startMonitoring() {
        Map<String, Object> response = new HashMap<String, Object>();
        monitorService.startMonitoring();
        response.put("result", true);
        return response;
    }

    @RequestMapping(value = "/monitor/stop", method = GET)
    public Map<String, Object> stopMonitoring() {
        Map<String, Object> response = new HashMap<String, Object>();
        monitorService.stopMonitoring();
        response.put("result", true);
        return response;
    }
}
