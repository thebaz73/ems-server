package ems.server.web;
/*
 * Copyright
 */

import ems.driver.domain.Driver;
import ems.driver.domain.common.Location;
import ems.driver.domain.common.Status;
import ems.protocol.domain.Protocol;
import ems.server.business.DeviceManager;
import ems.server.business.SpecificationManager;
import ems.server.domain.Device;
import ems.server.domain.Specification;
import ems.server.utils.EnumAwareConvertUtilsBean;
import ems.server.utils.InventoryHelper;
import org.apache.commons.beanutils.BeanUtilsBean;
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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

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
public class DeviceController {
    private final Log logger = LogFactory.getLog(DeviceController.class);
    private final BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new EnumAwareConvertUtilsBean());
    @Autowired
    private SpecificationManager specificationManager;
    @Autowired
    private DeviceManager deviceManager;

    private String process;
    private String processStep;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Specification.class,
                new SpecificationPropertyEditor());
    }

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
    public String show(Model model, HttpSession session) {
        process = "create";
        Device currentDevice = new Device();
        session.setAttribute("currentDevice", currentDevice);
        processStep = "specification";
        model.addAttribute("process", process);
        model.addAttribute("device", currentDevice);
        model.addAttribute("processStep", processStep);

        return "devices";
    }

    @RequestMapping(value = "/devices/{id}", method = GET)
    public String loadDevice(Model model, @PathVariable("id") String id, HttpSession session) {
        process = "edit";
        Device currentDevice = deviceManager.findDevice(id);
        session.setAttribute("currentDevice", currentDevice);
        processStep = "specification";
        model.addAttribute("process", process);
        model.addAttribute("device", currentDevice);
        model.addAttribute("processStep", processStep);

        return "devices";
    }

    @RequestMapping(value = "/devices", method = POST)
    public String createDevice(@ModelAttribute Device device, final BindingResult bindingResult, final ModelMap model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return logAndReturn(request.getSession(), bindingResult, model);
        }
        if(processStep.equalsIgnoreCase("specification")) {
            Specification specification = device.getSpecification();
            String driverClassName = InventoryHelper.getInstance().getDriverClassName(specification.getDriverType());
            String protocolClassName =  InventoryHelper.getInstance().getProtocolClassName(specification.getProtocolType());
            if (driverClassName != null && protocolClassName != null) {
                try {
                    Driver driver = InventoryHelper.getInstance().createDriver(driverClassName, specification, Status.UNKNOWN, new Location());
                    device.setDriver(driver);
                } catch (ClassNotFoundException e) {
                    String message = format("Cannot load class: %s class not found", driverClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                } catch (InstantiationException e) {
                    String message = format("Cannot load class: %s cannot instantiate", driverClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                } catch (IllegalAccessException e) {
                    String message = format("Cannot load class: %s illegal method access", driverClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                } catch (InvocationTargetException e) {
                    String message = format("Cannot use class: %s invocation target", driverClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                } catch (NoSuchMethodException e) {
                    String message = format("Cannot allocate class: %s no such method", driverClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                } catch (NoSuchFieldException e) {
                    String message = format("Cannot allocate class: %s no such field", driverClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                }
                try {
                    Protocol protocol = InventoryHelper.getInstance().createProtocol(protocolClassName);
                    device.setProtocol(protocol);
                } catch (ClassNotFoundException e) {
                    String message = format("Cannot load class: %s class not found", protocolClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                } catch (InstantiationException e) {
                    String message = format("Cannot load class: %s cannot instantiate", protocolClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                } catch (IllegalAccessException e) {
                    String message = format("Cannot load class: %s illegal method access", protocolClassName);
                    return logAndReturn(request.getSession(), bindingResult, model, e, message);
                }
            }
            processStep = "protocol";
            request.getSession().setAttribute("currentDevice", device);
            model.addAttribute("process", process);
            model.addAttribute("device", device);
            model.addAttribute("processStep", processStep);
            model.addAttribute("jsonProtocolSchema", device.getSpecification().getProtocol());
            return "devices";
        }
        else if(processStep.equalsIgnoreCase("protocol")) {
            processStep = "final";
            Device currentDevice = (Device) request.getSession().getAttribute("currentDevice");
            Protocol protocol = currentDevice.getProtocol();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if(!entry.getKey().equalsIgnoreCase("_csrf") && !entry.getKey().equalsIgnoreCase("protocolType")) {
                    try {
                        beanUtilsBean.setProperty(protocol, entry.getKey(), entry.getValue()[0]);
                    } catch (IllegalAccessException e) {
                        String message = format("Cannot set property: %s illegal method access", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    } catch (InvocationTargetException e) {
                        String message = format("Cannot set property: %s invocation target", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    }
                }
            }
            model.addAttribute("process", process);
            model.addAttribute("device", currentDevice);
            model.addAttribute("processStep", processStep);
            return "devices";
        }
        else {
            Device currentDevice = (Device) request.getSession().getAttribute("currentDevice");
            Driver driver = currentDevice.getDriver();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if(!entry.getKey().equalsIgnoreCase("_csrf")) {
                    try {
                        beanUtilsBean.setProperty(driver, "location."+entry.getKey(), entry.getValue()[0]);
                    } catch (IllegalAccessException e) {
                        String message = format("Cannot set property: %s illegal method access", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    } catch (InvocationTargetException e) {
                        String message = format("Cannot set property: %s invocation target", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    }
                }
            }

            deviceManager.createDevice(currentDevice);
            model.clear();
            return "redirect:/devices";
        }
    }

    @RequestMapping(value = "/devices", method = PUT)
    public String editDevice(@ModelAttribute Device device, final BindingResult bindingResult, final ModelMap model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "devices";
        }
        if(processStep.equalsIgnoreCase("specification")) {
            //TODO Handle specification changes.
            processStep = "protocol";
            Device currentDevice = deviceManager.findDevice(device.getId());
            device.setDriver(currentDevice.getDriver());
            device.setProtocol(currentDevice.getProtocol());
            request.getSession().setAttribute("currentDevice", device);
            model.addAttribute("process", process);
            model.addAttribute("device", device);
            model.addAttribute("processStep", processStep);
            model.addAttribute("jsonProtocolSchema", device.getSpecification().getProtocol());
            return "devices";
        }
        else if(processStep.equalsIgnoreCase("protocol")) {
            processStep = "final";
            Device currentDevice = (Device) request.getSession().getAttribute("currentDevice");
            Protocol protocol = currentDevice.getProtocol();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if(!entry.getKey().equalsIgnoreCase("_csrf") && !entry.getKey().equalsIgnoreCase("protocolType") && !entry.getKey().equalsIgnoreCase("_method") && !entry.getKey().equalsIgnoreCase("id")) {
                    try {
                        beanUtilsBean.setProperty(protocol, entry.getKey(), entry.getValue()[0]);
                    } catch (IllegalAccessException e) {
                        String message = format("Cannot set property: %s illegal method access", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    } catch (InvocationTargetException e) {
                        String message = format("Cannot set property: %s invocation target", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    }
                }
            }
            model.addAttribute("process", process);
            model.addAttribute("device", currentDevice);
            model.addAttribute("processStep", processStep);
            return "devices";
        }
        else {
            Device currentDevice = (Device) request.getSession().getAttribute("currentDevice");
            Driver driver = currentDevice.getDriver();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if(!entry.getKey().equalsIgnoreCase("_csrf") && !entry.getKey().equalsIgnoreCase("_method") && !entry.getKey().equalsIgnoreCase("id")) {
                    try {
                        beanUtilsBean.setProperty(driver, "location."+entry.getKey(), entry.getValue()[0]);
                    } catch (IllegalAccessException e) {
                        String message = format("Cannot set property: %s illegal method access", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    } catch (InvocationTargetException e) {
                        String message = format("Cannot set property: %s invocation target", entry.getKey());
                        return logAndReturn(request.getSession(), bindingResult, model, e, message);
                    }
                }
            }

            deviceManager.editDevice(currentDevice);
            model.clear();
            return "redirect:/devices";
        }
    }

    @RequestMapping(value = "/devices/{id}", method = DELETE)
    public String deleteDevice(@PathVariable("id") String id) {
        deviceManager.deleteDevice(id);
        return "redirect:/devices";
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
        Device currentDevice = (Device) session.getAttribute("currentDevice");
        if(currentDevice.getSpecification() != null) {
            model.addAttribute("jsonProtocolSchema", currentDevice.getSpecification().getProtocol());
        }

        return "devices";
    }

    public class SpecificationPropertyEditor extends PropertyEditorSupport {
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
            setValue(specificationManager.findSpecification(text));
        }
    }
}
