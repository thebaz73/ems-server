package ems.server.web;


import ems.server.business.DeviceManager;
import ems.server.business.TaskConfigurationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
}
