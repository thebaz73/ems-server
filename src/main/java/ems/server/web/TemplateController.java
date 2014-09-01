package ems.server.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * HandlebarsController
 * Created by thebaz on 9/1/14.
 */
@Controller
public class TemplateController {

    @RequestMapping(value = "/template/{name}", method = GET)
    public String show(@PathVariable("name") String name) {
        return "handlebars/" + name;
    }

}
