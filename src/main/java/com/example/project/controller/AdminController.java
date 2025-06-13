package com.example.project.controller;

import com.example.project.service.AdminService;
import com.example.project.service.model.admin.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/adminPage/userPage")
    public String getAllUsers(
            @RequestParam(value = "filter", required = false, defaultValue = "") final String filter,
            @RequestParam(value = "status", required = false, defaultValue = "") final String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(value = "size", required = false, defaultValue = "7") final int size,
            final Model model) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());
        final Page<UserInfo> userPage = adminService.filterUsers(filter, "", "", pageable);
        final var count = adminService.countUser();

        model.addAttribute("usersPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("filter", filter);
        model.addAttribute("countUser", count);
        model.addAttribute("contentTemplate", "admins/users");
        model.addAttribute("filter", filter);
        model.addAttribute("status", status);

        return "admins/layout";
    }

    @GetMapping("/adminPage/dashboard")
    public String getDashBoard(final Model model) {
        final var dashboard = adminService.getUsers("defaultTimeRange");
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("timeRangeUser", "defaultTimeRange");
        model.addAttribute("contentTemplate", "admins/dashboard");

        return "admins/layout";
    }

    @GetMapping("/adminPage/fetch-user")
    public String searchUsers(
            @RequestParam(value = "filter", required = false, defaultValue = "") final String filter,
            @RequestParam(value = "status", required = false, defaultValue = "") final String status,
            @RequestParam(value = "text", required = false, defaultValue = "") final String text,
            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(value = "size", required = false, defaultValue = "7") final int size,
            final Model model) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());
        final Page<UserInfo> userPage = adminService.filterUsers(filter, status, text, pageable);

        model.addAttribute("usersPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("filter", filter);
        model.addAttribute("text", text);
        model.addAttribute("status", status);

        return "admins/users::listUserAfterClickButton";
    }

    @PostMapping("/adminPage/user/change-active")
    @ResponseBody
    public Map<String, Object> searchUsers(
            @RequestParam(value = "id", required = false, defaultValue = "") final int id,
            final Model model) {
        final Map<String, Object> result = new HashMap<>();
        result.put("userInfo", adminService.changeActive(id));
        return result;
    }

    @GetMapping("/adminPage/course-page")
    public String getCourse(@RequestParam(value = "text", required = false, defaultValue = "") final String text,
                            @RequestParam(value = "status", required = false, defaultValue = "") final String status,
                            @RequestParam(value = "level", required = false, defaultValue = "") final String level,
                            @RequestParam(value = "category", required = false, defaultValue = "") final String category,
                            @RequestParam(value = "topic", required = false, defaultValue = "") final String topic,
                            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
                            @RequestParam(value = "size", required = false, defaultValue = "5") final int size,
                            final Model model) {
        final Pageable pageable = PageRequest.of(page, size);
        final var courses = adminService.getCourse(text, status, level, category, topic, pageable);

        model.addAttribute("courses", courses);
        model.addAttribute("text", text);
        model.addAttribute("status", status);
        model.addAttribute("level", level);
        model.addAttribute("category", category);
        model.addAttribute("topic", topic);
        model.addAttribute("contentTemplate", "admins/course");
        model.addAttribute("allCategories", List.of(
                "Web Development",
                "Data Science",
                "Mobile Development",
                "Programming Languages",
                "Game Development",
                "Database Design & Development",
                "Software Testing",
                "Software Engineering",
                "Software Development Tools"
        ));

        model.addAttribute("allTopics", List.of(
                "Machine Learning",
                "Artificial Intelligence (AI)",
                "Large Language Models (LLM)",
                "Python",
                "AI Agents",
                "Deep Learning",
                "Generative AI (GenAI)",
                "LangChain"
        ));
        model.addAttribute("allLevel", List.of(
                "All Levels", "Beginner", "Intermediate", "expect"
        ));
        model.addAttribute("AllStatus", List.of(
                "Waiting", "Approved"
        ));
        return "admins/layout";
    }
}
