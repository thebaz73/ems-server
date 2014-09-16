package ems.server.web;


import ems.server.business.DeviceManager;
import ems.server.business.TaskConfigurationManager;
import ems.server.domain.Device;
import ems.server.domain.TaskConfiguration;
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

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * TaskConfigurationController
 * Created by thebaz on 9/16/14.
 */
@Controller
public class TaskConfigurationController {
    private final Log logger = LogFactory.getLog(DeviceController.class);
    @Autowired
    private TaskConfigurationManager taskConfigurationManager;
    @Autowired
    private DeviceManager deviceManager;
    private Device currentDevice;

    @ModelAttribute("allDevices")
    public List<Device> allDevices() {
        List<Device> allDevices = deviceManager.findAllDevices();
        if(!allDevices.isEmpty()) {
            currentDevice = allDevices.get(0);
        }
        return allDevices;
    }

    @RequestMapping(value = "/tasks", method = GET)
    public String loadTask(Model model, HttpSession session, @RequestParam(value = "id", required = false) String id, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "variable");
        return initTask(model, session, pageable, id);
    }

    @RequestMapping(value = "/tasks", method = POST)
    public String createEntry(@ModelAttribute TaskConfiguration taskConfiguration, final BindingResult bindingResult, final ModelMap model) {
        throw new UnsupportedOperationException();
    }

    private String initTask(Model model, HttpSession session, Pageable pageable, String id) {
        if(id != null) {
            currentDevice = deviceManager.findDevice(id);
        }
        model.addAttribute("currentDevice", currentDevice);
        model.addAttribute("allEntries", taskConfigurationManager.findTaskConfigurationByDevice(currentDevice, pageable));
        TaskConfiguration taskConfiguration = new TaskConfiguration();
        taskConfiguration.setDeviceId(id);
        model.addAttribute("task", taskConfiguration);

        return "tasks";
    }
}
