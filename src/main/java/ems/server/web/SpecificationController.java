package ems.server.web;
/*
 * Copyright
 */

import ems.server.business.SpecificationManager;
import ems.server.domain.Specification;
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
 * <p>
 * User: mbazzoni
 * Date: 8/27/14
 * Time: 1:14 PM
 */
@Controller
public class SpecificationController {
    @Autowired
    private SpecificationManager specificationManager;

    @ModelAttribute("allSpecifications")
    public List<Specification> allSpecifications() {
        return specificationManager.findAllSpecifications();
    }

    @RequestMapping(value = "/specifications", method = GET)
    public String show(Model model) {
        model.addAttribute("process", "create");
        model.addAttribute("specification", new Specification());
        return "specifications";
    }

    @RequestMapping(value = "/specifications/{id}", method = GET)
    public String loadEntry(Model model, @PathVariable("id") String id) {
        Specification entry = specificationManager.findSpecification(id);
        model.addAttribute("process", "edit");
        model.addAttribute("specification", entry);
        return "specifications";
    }

    @RequestMapping(value = "/specifications", method = POST)
    public String createEntry(@ModelAttribute Specification specification, final BindingResult bindingResult, final ModelMap model) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/specifications", method = PUT)
    public String editEntry(@ModelAttribute Specification specification, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "specifications";
        }

        specificationManager.editEntry(specification);
        model.clear();
        return "redirect:/specifications";
    }
}
