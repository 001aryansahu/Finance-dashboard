package com.example.financeprp.controller;

import com.example.financeprp.Service.UserService;
import com.example.financeprp.exception.InvalidOperationException;
import com.example.financeprp.model.User;
import com.example.financeprp.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewController {

    private final UserService userService;

    public ViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        return SessionUtil.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/signin";
    }




    @GetMapping("/signin")
    public String signinPage(Model model, HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/signin")
    public String signin(@ModelAttribute User user,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        User loggedInUser = userService.authenticate(user.getName(), user.getPassword());
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
            return "redirect:/signin";
        }

        session.setAttribute("loggedInUser", loggedInUser);
        redirectAttributes.addFlashAttribute("success", "Signed in successfully.");
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "You have been signed out.");
        return "redirect:/signin";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model, HttpSession session) {
        SessionUtil.requireAdmin(session);
        model.addAttribute("user", new User());
        return "edit-user";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        SessionUtil.requireAdmin(session);
        if (bindingResult.hasErrors()) {
            return "edit-user";
        }
        try {
            user.setId(null);
            userService.registerUser(user);
        } catch (InvalidOperationException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            return "edit-user";
        }
        redirectAttributes.addFlashAttribute("success", "User created successfully.");
        return "redirect:/dashboard";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id,
                           Model model,
                           HttpSession session) {
        SessionUtil.requireAdmin(session);
        User user = userService.getUserById(id);
        if (user == null) {
            throw new com.example.financeprp.exception.ResourceNotFoundException("User not found.");
        }
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PostMapping("/users/update")
    public String updateUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        SessionUtil.requireAdmin(session);
        if (bindingResult.hasErrors()) {
            return "edit-user";
        }
        try {
            userService.updateUser(user);
        } catch (InvalidOperationException ex) {
            bindingResult.rejectValue("name", "invalid", ex.getMessage());
            return "edit-user";
        }
        redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/dashboard";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        SessionUtil.requireAdmin(session);
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/dashboard";
    }
}
