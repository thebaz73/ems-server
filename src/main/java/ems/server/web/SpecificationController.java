package ems.server.web;
/*
 * Copyright
 */

import ems.driver.domain.DriverType;
import ems.protocol.domain.ProtocolType;
import ems.server.business.DeviceManager;
import ems.server.business.DriverConfigurationManager;
import ems.server.business.SpecificationManager;
import ems.server.domain.DriverConfiguration;
import ems.server.domain.Specification;
import ems.server.utils.InventoryHelper;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.util.Arrays;
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
public class SpecificationController {
    private final Log logger = LogFactory.getLog(SpecificationController.class);
    @Autowired
    private SpecificationManager specificationManager;
    @Autowired
    private DeviceManager deviceManager;
    @Autowired
    private DriverConfigurationManager driverConfigurationManager;

    private String process;
    private String processStep;
    private boolean locked;

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
    public String show(Model model, HttpSession session) {
        process = "create";
        Specification currentSpecification = new Specification();
        session.setAttribute("currentSpecification", currentSpecification);
        processStep = "first";
        model.addAttribute("process", process);
        model.addAttribute("specification", currentSpecification);
        model.addAttribute("processStep", processStep);
        locked = (deviceManager.findDeviceBySpecification(currentSpecification).size() > 0);
        model.addAttribute("locked", locked);
        
        return "specifications";
    }

    @RequestMapping(value = "/specifications/{id}", method = GET)
    public String loadSpecification(Model model, @PathVariable("id") String id, HttpSession session) {
        process = "edit";
        Specification currentSpecification = specificationManager.findSpecification(id);
        session.setAttribute("currentSpecification", currentSpecification);
        processStep = "first";
        model.addAttribute("process", process);
        model.addAttribute("specification", currentSpecification);
        model.addAttribute("processStep", processStep);
        locked = (deviceManager.findDeviceBySpecification(currentSpecification).size() > 0);
        model.addAttribute("locked", locked);

        return "specifications";
    }

    @RequestMapping(value = "/specifications", method = POST)
    public String createSpecification(@ModelAttribute Specification specification, @ModelAttribute DriverConfiguration driverConfiguration, final BindingResult bindingResult, final ModelMap model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return logAndReturn(request.getSession(), bindingResult, model);
        }
        if(processStep.equalsIgnoreCase("first")) {
            specification.setDriver(InventoryHelper.getInstance().getDriverJsonSchema(specification.getDriverType()));
            specification.setProtocol(InventoryHelper.getInstance().getProtocolJsonSchema(specification.getProtocolType()));

            List<DriverConfiguration> driverConfigurationList = InventoryHelper.getInstance().getDriverConfigurationList(specification.getDriverType());
            request.getSession().setAttribute("driverConfigurationList", driverConfigurationList);
            request.getSession().setAttribute("currentConfiguration", 0);

            processStep = "final";
            request.getSession().setAttribute("currentSpecification", specification);
            model.addAttribute("process", process);
            model.addAttribute("specification", specification);
            model.addAttribute("driverConfiguration", driverConfigurationList.get(0));
            model.addAttribute("processStep", processStep);
            model.addAttribute("jsonDriverSchema", specification.getDriver());
            return "specifications";
        }
        else {
            Specification currentSpecification = (Specification) request.getSession().getAttribute("currentSpecification");
            List<DriverConfiguration> driverConfigurationList = (List<DriverConfiguration>) request.getSession().getAttribute("driverConfigurationList");
            Integer currentConfiguration = (Integer) request.getSession().getAttribute("currentConfiguration");
            driverConfigurationList.set(currentConfiguration, driverConfiguration);

            if(driverConfigurationList.size() == (currentConfiguration + 1)) {
                specificationManager.createSpecification(currentSpecification);
                for (DriverConfiguration configuration : driverConfigurationList) {
                    configuration.setSpecificationId(currentSpecification.getId());
                    driverConfigurationManager.createDriverConfiguration(configuration);
                }
                request.getSession().removeAttribute("driverConfigurationList");
                request.getSession().removeAttribute("currentConfiguration");
                model.clear();
                return "redirect:/specifications";
            }
            else {
                processStep = "final";
                request.getSession().setAttribute("currentSpecification", currentSpecification);
                request.getSession().setAttribute("currentConfiguration", ++currentConfiguration);
                model.addAttribute("process", process);
                model.addAttribute("specification", specification);
                model.addAttribute("driverConfiguration", driverConfigurationList.get(currentConfiguration));
                model.addAttribute("processStep", processStep);
                model.addAttribute("jsonDriverSchema", specification.getDriver());
                return "specifications";
            }
        }
    }

    @RequestMapping(value = "/specifications", method = PUT)
    public String editSpecification(@ModelAttribute Specification specification, @ModelAttribute DriverConfiguration driverConfiguration, final BindingResult bindingResult, final ModelMap model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return logAndReturn(request.getSession(), bindingResult, model);
        }
        if(processStep.equalsIgnoreCase("first")) {
            specification.setDriver(InventoryHelper.getInstance().getDriverJsonSchema(specification.getDriverType()));
            specification.setProtocol(InventoryHelper.getInstance().getProtocolJsonSchema(specification.getProtocolType()));

            List<DriverConfiguration> driverConfigurationList = driverConfigurationManager.findDriverConfigurationBySpecificationId(specification.getId());
            request.getSession().setAttribute("driverConfigurationList", driverConfigurationList);
            request.getSession().setAttribute("currentConfiguration", 0);

            processStep = "final";
            request.getSession().setAttribute("currentSpecification", specification);
            model.addAttribute("process", process);
            model.addAttribute("specification", specification);
            model.addAttribute("driverConfiguration", driverConfigurationList.get(0));
            model.addAttribute("processStep", processStep);
            model.addAttribute("jsonDriverSchema", specification.getDriver());
            return "specifications";
        }
        else {
            Specification currentSpecification = (Specification) request.getSession().getAttribute("currentSpecification");
            List<DriverConfiguration> driverConfigurationList = (List<DriverConfiguration>) request.getSession().getAttribute("driverConfigurationList");
            Integer currentConfiguration = (Integer) request.getSession().getAttribute("currentConfiguration");
            driverConfigurationList.set(currentConfiguration, driverConfiguration);

            if(driverConfigurationList.size() == (currentConfiguration + 1)) {
                specificationManager.editSpecification(currentSpecification);
                for (DriverConfiguration configuration : driverConfigurationList) {
                    driverConfigurationManager.editDriverConfiguration(configuration);
                }
                request.getSession().removeAttribute("driverConfigurationList");
                request.getSession().removeAttribute("currentConfiguration");
                model.clear();
                return "redirect:/specifications";
            }
            else {
                processStep = "final";
                request.getSession().setAttribute("currentSpecification", currentSpecification);
                request.getSession().setAttribute("currentConfiguration", ++currentConfiguration);
                model.addAttribute("process", process);
                model.addAttribute("specification", specification);
                model.addAttribute("driverConfiguration", driverConfigurationList.get(currentConfiguration));
                model.addAttribute("processStep", processStep);
                model.addAttribute("jsonDriverSchema", specification.getDriver());
                return "specifications";
            }
        }
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

    private String logAndReturn(HttpSession session, BindingResult bindingResult, ModelMap model) {
        return logAndReturn(session, bindingResult, model, null, null);
    }

    private String logAndReturn(HttpSession session, BindingResult bindingResult, ModelMap model, Exception e, String message) {
        if(e == null || message == null) {
            logger.error(message, e);
            bindingResult.reject(message);
        }
        model.addAttribute("process", process);
        model.addAttribute("processStep", processStep);
        model.addAttribute("locked", locked);
        Specification currentSpecification = (Specification) session.getAttribute("currentSpecification");
        if(currentSpecification != null) {
            model.addAttribute("jsonDriverSchema", currentSpecification.getDriver());
        }
        List<DriverConfiguration> driverConfigurationList = (List<DriverConfiguration>) session.getAttribute("driverConfigurationList");
        Integer currentConfiguration = (Integer) session.getAttribute("currentConfiguration");
        if(driverConfigurationList != null && currentConfiguration != null) {
            model.addAttribute("driverConfiguration", driverConfigurationList.get(currentConfiguration));
        }
        return "specifications";
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
