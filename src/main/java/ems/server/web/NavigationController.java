package ems.server.web;
/*
 * Copyright
 */

import ems.server.business.DeviceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

/**
 * DashboardController
 * Created by thebaz on 8/25/14.
 */
@Controller
public class NavigationController extends ConfigurationAwareController {
    @Autowired
    private DeviceManager deviceManager;

    @RequestMapping("/")
    public String dashboard(Map<String, Object> model) {
        model.put("configuration", getConfiguration());
        model.put("date", new Date());
        return "dashboard";
    }

    @RequestMapping("/inventory/show/{id}")
    public String show(Map<String, Object> model, @PathVariable("id") String id) {
        model.put("device", deviceManager.findDevice(id));
        return "show";
    }
}
