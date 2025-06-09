package com.example.project.controller;

import com.example.project.service.model.admin.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Controller
public class AdminController {
    final AdminService adminService;

    @GetMapping("/adminPage/users")
    public String getUsers(@RequestParam final String timeRangeUser, final Model model) {
        final var dashboard = adminService.getUsers(timeRangeUser);

        model.addAttribute("timeRangeUser", timeRangeUser);
        model.addAttribute("dashboard", dashboard);

        return "admins/dashboard :: usersListAndCount";
    }

    @GetMapping("/adminPage/usersChart")
    @ResponseBody
    public Map<String, Object> getUsersChart(@RequestParam final String timeRangeUser, final Model model) {
        final var dashboard = adminService.getUsers(timeRangeUser);

        final Map<String, Object> result = new HashMap<>();
        result.put("labels", dashboard.getUserChart().getLabels());
        result.put("data", dashboard.getUserChart().getCounts());

        return result;
    }

    @ResponseBody
    @GetMapping("/adminPage/card-count")
    public Map<String, Object> countCards() {
        final var count = adminService.count();

        final Map<String, Object> result = new HashMap<>();
        result.put("lecturer", count.getLecturers());
        result.put("learner", count.getLearners());
        result.put("courses", count.getCourses());

        return result;
    }

    @GetMapping("/adminPage/learner")
    public String getLearner(final Model model) {
        return "admins/learnerPage";
    }

    @GetMapping("/adminPage/dashboard")
    public String getDashBoard(final Model model) {
        final var dashboard = adminService.getUsers("defaultTimeRange");
        model.addAttribute("dashboard", dashboard);
        return "admins/dashboard :: usersListAndCount";
    }
}
