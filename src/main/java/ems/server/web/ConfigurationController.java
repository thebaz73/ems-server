package ems.server.web;
/*
 * Copyright
 */

import ems.server.business.ConfigurationManager;
import ems.server.domain.EmsConfigurationEntry;
import ems.server.monitor.MonitoringStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class ConfigurationController extends StatusAwareController {
    @Autowired
    private ConfigurationManager configurationManager;

    @ModelAttribute("allEntries")
    public List<EmsConfigurationEntry> allEntries() {
        return configurationManager.findAllEntries();
    }

    @RequestMapping(value = "/settings/configuration", method = GET)
    public String show(Model model) {
        model.addAttribute("process", "create");
        model.addAttribute("emsConfigurationEntry", new EmsConfigurationEntry());
        return "configurations";
    }

    @RequestMapping(value = "/settings/configuration/{id}", method = GET)
    public String loadEntry(Model model, @PathVariable("id") String id) {
        EmsConfigurationEntry entry = configurationManager.findEntry(id);
        model.addAttribute("process", "edit");
        model.addAttribute("emsConfigurationEntry", entry);
        return "configurations";
    }

    @RequestMapping(value = "/settings/configuration", method = POST)
    public String createEntry(@ModelAttribute EmsConfigurationEntry emsConfigurationEntry, final BindingResult bindingResult, final ModelMap model) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/settings/configuration", method = PUT)
    public String editEntry(@ModelAttribute EmsConfigurationEntry emsConfigurationEntry, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "configurations";
        }
        if(getMonitoringStatus().equals(MonitoringStatus.RUNNING)) {
            return "redirect:/settings/configuration";
        }

        configurationManager.editEntry(emsConfigurationEntry);
        model.clear();
        return "redirect:/settings/configuration";
    }
}
