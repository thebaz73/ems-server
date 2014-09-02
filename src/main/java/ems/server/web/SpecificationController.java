package ems.server.web;
/*
 * Copyright
 */

import ems.driver.domain.DriverType;
import ems.protocol.domain.ProtocolType;
import ems.server.business.DeviceManager;
import ems.server.business.SpecificationManager;
import ems.server.domain.Specification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * AdministratorController
 * <p/>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 1:14 PM
 */
@Controller
public class SpecificationController {
    private final Log logger = LogFactory.getLog(SpecificationController.class);
    @Autowired
    private SpecificationManager specificationManager;
    @Autowired
    private DeviceManager deviceManager;
    @Value("classpath:/extension.properties")
    private Resource resource;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(DriverType.class,
                new DriverTypePropertyEditor());
        binder.registerCustomEditor(ProtocolType.class,
                new ProtocolTypePropertyEditor());
    }

    @ModelAttribute("allDriverTypes")
    public List<DriverType> allDriverTypes() {
        return Arrays.asList(DriverType.values());
    }

    @ModelAttribute("allProtocolTypes")
    public List<ProtocolType> allProtocolTypes() {
        return Arrays.asList(ProtocolType.values());
    }

    @ModelAttribute("allSpecifications")
    public Page<Specification> allSpecifications(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "name");
        return specificationManager.findAllSpecifications(pageable);
    }

    @RequestMapping(value = "/specifications", method = GET)
    public String show(Model model) {
        model.addAttribute("process", "create");
        model.addAttribute("specification", new Specification());
        model.addAttribute("locked", false);
        return "specifications";
    }

    @RequestMapping(value = "/specifications/{id}", method = GET)
    public String loadSpecification(Model model, @PathVariable("id") String id) {
        Specification entry = specificationManager.findSpecification(id);
        model.addAttribute("process", "edit");
        model.addAttribute("specification", entry);
        model.addAttribute("locked", deviceManager.findDeviceBySpecification(entry).size() > 0);

        return "specifications";
    }

    @RequestMapping(value = "/specifications", method = POST)
    public String createSpecification(@ModelAttribute Specification specification, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("process", "create");
            model.addAttribute("locked", false);
            return "specifications";
        }
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            specification.setDriver((String) properties.get(format("extension.driver.%s.json", specification.getDriverType())));
            specification.setProtocol((String) properties.get(format("extension.protocol.%s.json", specification.getProtocolType())));

            specificationManager.createSpecification(specification);
            model.clear();
        } catch (IOException e) {
            logger.error("Cannot load properties", e);
            bindingResult.reject(" Fatal! Cannot load extension properties.");
            model.addAttribute("process", "create");
            model.addAttribute("locked", false);
            return "specifications";
        }

        return "redirect:/specifications";
    }

    @RequestMapping(value = "/specifications", method = PUT)
    public String editSpecification(@ModelAttribute Specification specification, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "specifications";
        }
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            specification.setDriver((String) properties.get(format("extension.driver.%s.json", specification.getDriverType())));
            specification.setProtocol((String) properties.get(format("extension.protocol.%s.json", specification.getProtocolType())));

            specificationManager.editSpecification(specification);
            model.clear();
        } catch (IOException e) {
            logger.error("Cannot load properties", e);
            bindingResult.reject(" Fatal! Cannot load extension properties.");
            return "specifications";
        }

        model.clear();
        return "redirect:/specifications";
    }

    @RequestMapping(value = "/specifications/{id}", method = DELETE)
    public String deleteSpecification(Model model, @PathVariable("id") String id) {
        Specification specification = specificationManager.findSpecification(id);
        if(deviceManager.findDeviceBySpecification(specification).size() > 0) {
            model.addAttribute("warning", "Cannot delete specification " + specification.getName() + ", because devices are build on it.");
            return "specifications";
        }
        specificationManager.deleteSpecification(id);
        return "redirect:/specifications";
    }

    public class DriverTypePropertyEditor extends PropertyEditorSupport {
        /**
         * Sets the property value by parsing a given String.  May raise
         * java.lang.IllegalArgumentException if either the String is
         * badly formatted or if this kind of property can't be expressed
         * as text.
         *
         * @param text The string to be parsed.
         */
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(DriverType.fromValue(text.trim()));
        }
    }

    public class ProtocolTypePropertyEditor extends PropertyEditorSupport {
        /**
         * Sets the property value by parsing a given String.  May raise
         * java.lang.IllegalArgumentException if either the String is
         * badly formatted or if this kind of property can't be expressed
         * as text.
         *
         * @param text The string to be parsed.
         */
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(ProtocolType.fromValue(text.trim()));
        }
    }
}
