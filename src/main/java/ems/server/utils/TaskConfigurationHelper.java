package ems.server.utils;

import ems.server.business.TaskConfigurationManager;
import ems.server.data.TaskConfigurationRepository;
import ems.server.domain.Device;
import ems.server.domain.TaskConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * TaskConfigurationHelper
 * Created by thebaz on 17/09/14.
 */
@Component
public class TaskConfigurationHelper {
    private static TaskConfigurationHelper instance;
    @Autowired
    TaskConfigurationManager taskConfigurationManager;

    private TaskConfigurationHelper() {
        instance = this;
    }

    public static TaskConfigurationHelper getInstance() {
        return instance;
    }

    public void addTaskConfigurations(Device device) {
        Random r = new Random(System.currentTimeMillis());
        List<String> driverPropertyNames = InventoryHelper.getInstance().getDriverPropertyNames(device.getSpecification().getDriverType());
        for (String driverPropertyName : driverPropertyNames) {
            TaskConfiguration taskConfiguration = new TaskConfiguration();
            taskConfiguration.setDeviceId(device.getId());
            taskConfiguration.setVariable(driverPropertyName);
            int randomFrequency = r.nextInt(5);
            taskConfiguration.setFrequency(randomFrequency==0?1:randomFrequency);
            taskConfiguration.setDelay(r.nextInt(3));
            taskConfiguration.setRecurrent(true);
            taskConfigurationManager.createTaskConfiguration(taskConfiguration);
        }
    }

    public void removeTaskConfigurations(Device device) {
        taskConfigurationManager.deleteTaskConfigurationByDevice(device);
    }
}
