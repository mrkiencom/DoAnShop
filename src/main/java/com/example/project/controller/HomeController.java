package com.example.project.controller;

import com.example.project.model.Courses;
import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.service.AuthenticationService;
import com.example.project.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CourseService courseService;

    @GetMapping("/")
    public String homepage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                Users user = authenticationService.getInforUserByGmail(gmail);

                if (user != null) {
                    model.addAttribute("user_account", user);
                    // Thêm các thuộc tính khác nếu cần, ví dụ:
                    // model.addAttribute("name_user", user.getFirstname() + " " + user.getLastname());
                    // model.addAttribute("email_user", user.getGmail());
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                Users user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        List<Courses> list10Courses = courseService.getLatest10Courses();
        model.addAttribute("list10Courses", list10Courses);

        return "homepage";
    }

    @GetMapping("/user")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }
}
