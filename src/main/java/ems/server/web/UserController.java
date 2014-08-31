package ems.server.web;
/*
 * Copyright
 */

import ems.server.business.UserManager;
import ems.server.domain.EmsRole;
import ems.server.domain.EmsUser;
import ems.server.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
public class UserController {
    @Autowired
    private UserManager userManager;

    @ModelAttribute("allRoles")
    public List<Role> allRoles() {
        return Arrays.asList(Role.ALL);
    }

    @ModelAttribute("allUsers")
    public List<EmsUser> allUsers() {
        return userManager.findAllUsers();
    }

    @RequestMapping(value = "/admin/users", method = POST)
    public String createUser(@ModelAttribute EmsUser emsUser, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "users";
        }
        if (userManager.userCreated(emsUser.getUsername())) {
            return "users";
        }

        userManager.createUser(emsUser);
        model.clear();
        return "redirect:/admin/users";
    }

    @RequestMapping(value = "/admin/users/{username}", method = DELETE)
    public String removeUser(@ModelAttribute("aUser") EmsUser emsUser, @PathVariable("username") String username) {
        if (!userManager.userCreated(username)) {
            return "redirect:/admin/users";
        }

        userManager.deleteUserByUsername(username);
        return "redirect:/admin/users";
    }

    @RequestMapping(value = "/admin/users", method = GET)
    public String show(Model model) {
        model.addAttribute("emsUser", new EmsUser());
        return "users";
    }

    @RequestMapping(value = "/admin/users", params = "addRole")
    public String addRole(@ModelAttribute EmsUser emsUser, final BindingResult bindingResult) {
        emsUser.getRoles().add(new EmsRole());
        return "users";
    }

    @RequestMapping(value = "/admin/users", params = "removeRole")
    public String removeRole(@ModelAttribute EmsUser emsUser, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));
        emsUser.getRoles().remove(rowId.intValue());
        return "users";
    }

}
