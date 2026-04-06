package com.example.financeprp.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.financeprp.Service.DashboardService;
import com.example.financeprp.Service.UserService;
import com.example.financeprp.model.User;
import com.example.financeprp.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final DashboardService service;
    private final UserService userService;

    public DashboardController(DashboardService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        SessionUtil.requireLogin(session);
        User sessionUser = SessionUtil.getLoggedInUser(session);

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("data", service.getDashboardData());
        model.addAttribute("canViewInsights", SessionUtil.canViewInsights(session));
        model.addAttribute("canManageRecords", SessionUtil.canManageRecords(session));
        model.addAttribute("isAdmin", SessionUtil.isAdmin(session));
        model.addAttribute("users", SessionUtil.isAdmin(session) ? userService.getAllUsers() : Collections.emptyList());
        return "dashboard";
    }

    @GetMapping("/insights")
    public String insights(Model model, HttpSession session) {
        SessionUtil.requireInsightsAccess(session);
        model.addAttribute("sessionUser", SessionUtil.getLoggedInUser(session));
        model.addAttribute("canManageRecords", SessionUtil.canManageRecords(session));
        model.addAttribute("isAdmin", SessionUtil.isAdmin(session));
        model.addAttribute("data", service.getDashboardData());
        return "insights";
    }

    @GetMapping("/api/dashboard")
    @ResponseBody
    public Map<String, Object> api(HttpSession session) {
        SessionUtil.requireLogin(session);
        return service.getDashboardData();
    }
}
