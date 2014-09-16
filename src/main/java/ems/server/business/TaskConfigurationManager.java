package ems.server.business;


import ems.server.data.TaskConfigurationRepository;
import ems.server.domain.Device;
import ems.server.domain.TaskConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TaskConfigurationManager
 * Created by thebaz on 9/16/14.
 */
@Component
public class TaskConfigurationManager {
    @Autowired
    private TaskConfigurationRepository taskConfigurationRepository;

    public TaskConfiguration findTaskConfiguration(String id) {
        return taskConfigurationRepository.findOne(id);
    }

    public List<TaskConfiguration> findAllTaskConfigurations() {
        return taskConfigurationRepository.findAll();
    }

    public void createTaskConfiguration(TaskConfiguration taskConfiguration) {
        if(taskConfiguration.getId() != null) {
            taskConfiguration.setId(null);
        }
        taskConfigurationRepository.save(taskConfiguration);
    }

    public void editTaskConfiguration(TaskConfiguration taskConfiguration) {
        if(taskConfiguration.getId() != null && taskConfigurationRepository.findOne(taskConfiguration.getId()) != null ) {
            taskConfigurationRepository.save(taskConfiguration);
        }
    }

    public void deleteTaskConfiguration(String id) {
        taskConfigurationRepository.delete(id);
    }

    public List<TaskConfiguration> findTaskConfigurationByDevice(Device device) {
        return  taskConfigurationRepository.findByDeviceId(device.getId());
    }

    public Page<TaskConfiguration> findAllTaskConfigurations(Pageable pageable) {
        return taskConfigurationRepository.findAll(pageable);
    }
}
