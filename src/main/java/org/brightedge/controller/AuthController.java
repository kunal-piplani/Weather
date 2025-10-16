package org.brightedge.controller;

import org.brightedge.model.User;
import org.brightedge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("success", "User registered successfully!");
            model.addAttribute("user", new User()); // reset form
            return "signup"; // stay on same page to show green success message
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "signup"; // show red error message
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}