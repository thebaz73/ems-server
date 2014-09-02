package ems.server.business;


import ems.server.data.DeviceRepository;
import ems.server.data.EventRepository;
import ems.server.domain.Device;
import ems.server.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DeviceManager
 * Created by thebaz on 9/2/14.
 */
@Component
public class DeviceManager {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private EventRepository eventRepository;

    public Device findDevice(String id) {
        return deviceRepository.findOne(id);
    }

    public List<Device> findAllDevices() {
        return deviceRepository.findAll();
    }

    public void createDevice(Device device) {
        if(device.getId() != null) {
            device.setId(null);
        }
        deviceRepository.save(device);
    }

    public void editDevice(Device device) {
        if(device.getId() != null && deviceRepository.findOne(device.getId()) != null ) {
            deviceRepository.save(device);
        }
    }

    public void deleteDevice(String id) {
        deviceRepository.delete(id);
    }

    public List<Device> findDeviceBySpecification(Specification specification) {
        return  deviceRepository.findBySpecification(specification);
    }

    public Page<Device> findAllDevices(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }
}
