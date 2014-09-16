package ems.server.web;


import ems.server.business.DeviceManager;
import ems.server.business.TaskConfigurationManager;
import ems.server.domain.Device;
import ems.server.domain.TaskConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @ModelAttribute("allDevices")
    public List<Device> allDevices() {
        return deviceManager.findAllDevices();
    }

    @RequestMapping(value = "/tasks", method = GET)
    public String show(Model model, HttpSession session) {
        return initTask(model, session, null);
    }

    @RequestMapping(value = "/tasks/{id}", method = GET)
    public String loadTask(Model model, HttpSession session, @PathVariable("id") String id) {
        return initTask(model, session, id);
    }

    @RequestMapping(value = "/tasks", method = POST)
    public String createEntry(@ModelAttribute TaskConfiguration taskConfiguration, final BindingResult bindingResult, final ModelMap model) {
        throw new UnsupportedOperationException();
    }

    private String initTask(Model model, HttpSession session, String id) {
        if(id != null) {
            Device currentDevice = deviceManager.findDevice(id);
            session.setAttribute("currentDevice", currentDevice);
            TaskConfiguration taskConfiguration = new TaskConfiguration();
            taskConfiguration.setDeviceId(id);
            model.addAttribute("task", taskConfiguration);
        }

        return "tasks";
    }
}
