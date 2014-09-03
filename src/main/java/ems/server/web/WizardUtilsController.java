package ems.server.web;


import ems.server.domain.Device;
import ems.server.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * WizardUtilsController
 * Created by thebaz on 9/3/14.
 */
@RestController
public class WizardUtilsController {
    @RequestMapping(value = "/devices/current", method = GET, produces = "application/json")
    public Device getCurrentDevice(HttpSession session) {
        return (Device) session.getAttribute("currentDevice");
    }

    @RequestMapping(value = "/specifications/current", method = GET, produces = "application/json")
    public Specification getCurrentSpecification(HttpSession session) {
        return (Specification) session.getAttribute("currentSpecification");
    }
}