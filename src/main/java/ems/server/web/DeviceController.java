package ems.server.web;
/*
 * Copyright
 */

import ems.server.business.DeviceManager;
import ems.server.business.SpecificationManager;
import ems.server.domain.Device;
import ems.server.domain.Specification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * AdministratorController
 * <p/>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 1:14 PM
 */
@Controller
public class DeviceController {
    private final Log logger = LogFactory.getLog(DeviceController.class);
    @Autowired
    private SpecificationManager specificationManager;
    @Autowired
    private DeviceManager deviceManager;

    @ModelAttribute("allSpecifications")
    public List<Specification> allDriverTypes() {
        return specificationManager.findAllSpecifications();
    }

    @ModelAttribute("allDevices")
    public Page<Device> allDevices(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "name");
        return deviceManager.findAllDevices(pageable);
    }

    @RequestMapping(value = "/devices", method = GET)
    public String show(Model model) {
        model.addAttribute("process", "create");
        model.addAttribute("device", new Device());
        return "devices";
    }

    @RequestMapping(value = "/devices/{id}", method = GET)
    public String loadDevice(Model model, @PathVariable("id") String id) {
        Device entry = deviceManager.findDevice(id);
        model.addAttribute("process", "edit");
        model.addAttribute("device", entry);

        return "devices";
    }

    @RequestMapping(value = "/devices", method = POST)
    public String createDevice(@ModelAttribute Device device, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("process", "create");
            model.addAttribute("locked", false);
            return "devices";
        }

        deviceManager.createDevice(device);
        model.clear();
        return "redirect:/devices";
    }

    @RequestMapping(value = "/devices", method = PUT)
    public String editDevice(@ModelAttribute Device device, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "devices";
        }

        deviceManager.editDevice(device);
        model.clear();
        return "redirect:/devices";
    }

    @RequestMapping(value = "/devices/{id}", method = DELETE)
    public String deleteDevice(Model model, @PathVariable("id") String id) {
        deviceManager.deleteDevice(id);
        return "redirect:/devices";
    }
}
