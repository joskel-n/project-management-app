package com.projectmanagement.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${application.name}")
    private String applicationName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", applicationName);
        model.addAttribute("welcomeMessage", "Welcome to the Project Management System. Organize, track, and manage your projects efficiently.");
        return "home";
    }
}