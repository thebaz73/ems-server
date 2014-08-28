package ems.server.web;
/*
 * Copyright
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

/**
 * DashboardController
 * Created by thebaz on 8/25/14.
 */
@Controller
public class DashboardController {
    @RequestMapping("/")
    public String dashboard(Map<String, Object> model) {
        model.put("message", "Hello World");
        model.put("title", "Hello Home");
        model.put("date", new Date());
        return "dashboard";
    }
}
