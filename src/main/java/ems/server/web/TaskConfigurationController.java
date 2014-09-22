package ems.server.web;


import ems.server.business.DeviceManager;
import ems.server.business.TaskConfigurationManager;
import ems.server.domain.Device;
import ems.server.domain.TaskConfiguration;
import ems.server.monitor.MonitoringStatus;
import ems.server.utils.InventoryHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * TaskConfigurationController
 * Created by thebaz on 9/16/14.
 */
@Controller
public class TaskConfigurationController extends StatusAwareController {
    private final Log logger = LogFactory.getLog(DeviceController.class);
    @Autowired
    private TaskConfigurationManager taskConfigurationManager;
    @Autowired
    private DeviceManager deviceManager;
    private Device currentDevice;
    private int page;
    private int pageSize;

    @ModelAttribute("allDevices")
    public List<Device> allDevices() {
        List<Device> allDevices = deviceManager.findAllDevices();
        if(!allDevices.isEmpty() && currentDevice == null) {
            currentDevice = allDevices.get(0);
        }
        return allDevices;
    }

    @RequestMapping(value = "/tasks", method = GET)
    public String loadTask(Model model, @RequestParam(value = "deviceId", required = false) String deviceId, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "variable");
        if(deviceId != null) {
            currentDevice = deviceManager.findDevice(deviceId);
        }
        model.addAttribute("currentDevice", currentDevice);
        model.addAttribute("driverPropertyNames", InventoryHelper.getInstance().getDriverPropertyNames(currentDevice.getSpecification().getDriverType()));
        model.addAttribute("allEntries", taskConfigurationManager.findTaskConfigurationByDevice(currentDevice, pageable));
        TaskConfiguration taskConfiguration = new TaskConfiguration();
        taskConfiguration.setDeviceId(currentDevice.getId());
        model.addAttribute("task", taskConfiguration);

        return "tasks";
    }

    @RequestMapping(value = "/tasks", method = POST)
    public String createEntry(@ModelAttribute TaskConfiguration taskConfiguration, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return logAndReturn(bindingResult, model);
        }
        if(getMonitoringStatus().equals(MonitoringStatus.RUNNING)) {
            return "redirect:/tasks";
        }
        taskConfigurationManager.createTaskConfiguration(taskConfiguration);
        model.clear();
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks/{id}", method = DELETE)
    public String deleteTask(@PathVariable("id") String id) {
        if(getMonitoringStatus().equals(MonitoringStatus.RUNNING)) {
            return "redirect:/tasks";
        }
        taskConfigurationManager.deleteTaskConfiguration(id);
        return "redirect:/tasks";
    }

    private String logAndReturn(BindingResult bindingResult, ModelMap model) {
        String message = "Error during creation process";
        logger.error(message);
        bindingResult.reject(message);
        model.addAttribute("currentDevice", currentDevice);
        model.addAttribute("driverPropertyNames", InventoryHelper.getInstance().getDriverPropertyNames(currentDevice.getSpecification().getDriverType()));
        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "variable");
        model.addAttribute("allEntries", taskConfigurationManager.findTaskConfigurationByDevice(currentDevice, pageable));
        return "tasks";
    }
}
