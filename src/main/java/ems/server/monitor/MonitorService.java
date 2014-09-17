package ems.server.monitor;


import ems.server.business.DeviceManager;
import ems.server.business.TaskConfigurationManager;
import ems.server.domain.Device;
import ems.server.domain.TaskConfiguration;
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

    private final List<MonitoringTask> monitoringTasks = new ArrayList<MonitoringTask>();

    public void startMonitoring() {
        List<Device> devices = deviceManager.findAllDevices();
        for (Device device : devices) {
            List<TaskConfiguration> taskConfigurations = taskConfigurationManager.findTaskConfigurationByDevice(device);
            MonitoringTask monitoringTask = new MonitoringTask();
            monitoringTask.setDevice(device);
            monitoringTask.setTaskConfigurations(taskConfigurations);
            monitoringTask.start();
            monitoringTasks.add(monitoringTask);
        }
    }

    public void stopMonitoring() {
        for (MonitoringTask monitoringTask : monitoringTasks) {
            monitoringTask.stop();
            monitoringTask.join();
        }
    }
}
