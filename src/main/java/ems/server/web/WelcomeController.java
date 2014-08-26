package ems.server.web;
/*
 * Copyright
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

/**
 * WelcomeController
 * <p/>
 * User: mbazzoni
 * Date: 8/25/14
 * Time: 5:49 PM
 */
@Controller
public class WelcomeController {
    @RequestMapping("/")
    public String home(Map<String, Object> model) {
        model.put("message", "Hello World");
        model.put("title", "Hello Home");
        model.put("date", new Date());
        return "home";
    }
}
