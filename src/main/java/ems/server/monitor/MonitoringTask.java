package ems.server.monitor;

import ems.server.domain.Device;
import ems.server.domain.TaskConfiguration;

import java.util.List;

/**
 * MonitoringTask
 * Created by thebaz on 17/09/14.
 */
public class MonitoringTask {
    private Device device;
    private List<TaskConfiguration> taskConfigurations;

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setTaskConfigurations(List<TaskConfiguration> taskConfigurations) {
        this.taskConfigurations = taskConfigurations;
    }

    public void start() {

    }

    public void stop() {

    }

    public void join() {

    }
}
