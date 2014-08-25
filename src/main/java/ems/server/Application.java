package ems.server;

import ems.server.data.DeviceRepository;
import ems.server.data.DeviceSpecificationRepository;
import ems.server.domain.Device;
import ems.server.domain.DeviceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Application
 * Created by thebaz on 25/08/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application implements CommandLineRunner {
    @Autowired
    DeviceSpecificationRepository specificationRepository;
    @Autowired
    DeviceRepository deviceRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(specificationRepository.count() == 0) {
            DeviceSpecification specification = new DeviceSpecification();
            specification.setName("Test001");
            specification.setType("Type001");
            specificationRepository.save(specification);

            Device d1 = new Device();
            d1.setName("Device001");
            d1.setDeviceSpecification(specification);
            deviceRepository.save(d1);

            Device d2 = new Device();
            d2.setName("Device002");
            d2.setDeviceSpecification(specification);
            deviceRepository.save(d2);
        }
    }
}
