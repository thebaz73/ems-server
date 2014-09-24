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
    private MonitoringStatus monitoringStatus = MonitoringStatus.STOPPED;
    @Autowired
    private DeviceManager deviceManager;
    @Autowired
    private TaskConfigurationManager taskConfigurationManager;
    @Autowired
    private ConfigurationManager configurationManager;
    @Autowired
    private DriverConfigurationManager driverConfigurationManager;

    private final List<MonitoringTask> monitoringTasks = new ArrayList<MonitoringTask>();

    public MonitoringStatus getMonitoringStatus() {
        return monitoringStatus;
    }

    public boolean startMonitoring() {
        if(monitoringStatus != MonitoringStatus.RUNNING) {
            try {
                List<Device> devices = deviceManager.findAllDevices();
                for (final Device device : devices) {
                    List<TaskConfiguration> taskConfigurations = taskConfigurationManager.findTaskConfigurationByDevice(device);
                    List<DriverConfiguration> driverConfigurations = driverConfigurationManager.findDriverConfigurationBySpecificationId(device.getSpecification().getId());
                    MonitoringTask monitoringTask = new MonitoringTask();
                    monitoringTask.setDevice(device);
                    monitoringTask.setTaskConfigurations(taskConfigurations);
                    monitoringTask.setDriverConfigurations(driverConfigurations);

                    SaveTask saveTask = new SaveTask();
                    saveTask.setDevice(device);
                    saveTask.setDeviceManager(deviceManager);
                    monitoringTask.setSaveTask(saveTask);

                    monitoringTask.setUpdateFrequency(configurationManager.findEntryByKey("update_frequency_sec"));
                    monitoringTask.setRetries(configurationManager.findEntryByKey("retries"));
                    monitoringTask.setTimeout(configurationManager.findEntryByKey("timeout"));
                    monitoringTask.start();
                    monitoringTasks.add(monitoringTask);
                }
                monitoringStatus = MonitoringStatus.RUNNING;
            } catch (GenericException e) {
                stopMonitoring();
                monitoringStatus = MonitoringStatus.STOPPED;
            }
        }
        return monitoringStatus == MonitoringStatus.RUNNING;
    }

    public boolean stopMonitoring() {
        if(monitoringStatus == MonitoringStatus.RUNNING) {
            try {
                for (MonitoringTask monitoringTask : monitoringTasks) {
                    monitoringTask.stop();
                    monitoringTask.join();
                }
                monitoringTasks.clear();
                monitoringStatus = MonitoringStatus.STOPPED;
            } catch (InterruptedException e) {
                monitoringStatus = MonitoringStatus.RUNNING;
            }
        }
        return monitoringStatus == MonitoringStatus.STOPPED;
    }

}
