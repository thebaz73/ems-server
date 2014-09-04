package ems.server.business;


import ems.server.data.DriverConfigurationRepository;
import ems.server.domain.DriverConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DriverConfigurationManager
 * Created by thebaz on 9/4/14.
 */
@Component
public class DriverConfigurationManager {
    @Autowired
    private DriverConfigurationRepository driverConfigurationRepository;

    public List<DriverConfiguration> findDriverConfigurationBySpecificationId(String id) {
        return driverConfigurationRepository.findBySpecificationId(id);
    }

    public void createDriverConfiguration(DriverConfiguration driverConfiguration) {
        if(driverConfiguration.getId() != null) {
            driverConfiguration.setId(null);
        }
        driverConfigurationRepository.save(driverConfiguration);
    }

    public void editDriverConfiguration(DriverConfiguration driverConfiguration) {
        if(driverConfiguration.getId() != null && driverConfigurationRepository.findOne(driverConfiguration.getId()) != null ) {
            driverConfigurationRepository.save(driverConfiguration);
        }
    }

    public void deleteDriverConfiguration(String id) {
        driverConfigurationRepository.delete(id);
    }

    public void deleteDriverConfiguration(DriverConfiguration driverConfiguration) {
        driverConfigurationRepository.delete(driverConfiguration);
    }
}
