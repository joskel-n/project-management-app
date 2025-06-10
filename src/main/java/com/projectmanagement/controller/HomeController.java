package com.projectmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", "Project Management System");
        model.addAttribute("welcomeMessage", "Welcome to the Project Management System. Organize, track, and manage your projects efficiently.");
        return "home";
    }
}