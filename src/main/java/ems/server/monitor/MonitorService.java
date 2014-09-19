package ems.server.monitor;


import ems.server.business.ConfigurationManager;
import ems.server.business.DeviceManager;
import ems.server.business.DriverConfigurationManager;
import ems.server.business.TaskConfigurationManager;
import ems.server.domain.Device;
import ems.server.domain.DriverConfiguration;
import ems.server.domain.TaskConfiguration;
import ems.server.utils.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * MonitorService
 * Created by thebaz on 9/15/14.
 */
@Service
public class MonitorService {
    @Autowired
    private DeviceManager deviceManager;
    @Autowired
    private TaskConfigurationManager taskConfigurationManager;
    @Autowired
    private ConfigurationManager configurationManager;
    @Autowired
    private DriverConfigurationManager driverConfigurationManager;

    private final List<MonitoringTask> monitoringTasks = new ArrayList<MonitoringTask>();

    public boolean startMonitoring() {
        try {
            List<Device> devices = deviceManager.findAllDevices();
            for (final Device device : devices) {
                List<TaskConfiguration> taskConfigurations = taskConfigurationManager.findTaskConfigurationByDevice(device);
                List<DriverConfiguration> driverConfigurations = driverConfigurationManager.findDriverConfigurationBySpecificationId(device.getSpecification().getId());
                MonitoringTask monitoringTask = new MonitoringTask();
                monitoringTask.setDevice(device);
                monitoringTask.setTaskConfigurations(taskConfigurations);
                monitoringTask.setDriverConfigurations(driverConfigurations);
                monitoringTask.setSaveTask(new Runnable() {
                    @Override
                    public void run() {
                        deviceManager.editDevice(device);
                    }
                });
                monitoringTask.setRetries(configurationManager.findEntryByKey("retries"));
                monitoringTask.setTimeout(configurationManager.findEntryByKey("timeout"));
                monitoringTask.start();
                monitoringTasks.add(monitoringTask);
            }
        } catch (GenericException e) {
            return false;
        }
        return true;
    }

    public boolean stopMonitoring() {
        try {
            for (MonitoringTask monitoringTask : monitoringTasks) {
                monitoringTask.stop();
                monitoringTask.join();
            }
            monitoringTasks.clear();
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }
}
