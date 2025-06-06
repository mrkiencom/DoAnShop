package com.example.project.controller;

import com.example.project.model.*;
import com.example.project.service.AuthenticationService;
import com.example.project.service.CourseService;
import com.example.project.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller
public class VideoController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    VideoService videoService;

    @Autowired
    CourseService courseService;

    @GetMapping("/learn/{courseId}/{videoId}")
    public String getVideo(@PathVariable("courseId") Integer courseId, @PathVariable("videoId") Integer videoId, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users user = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                user = authenticationService.getInforUserByGmail(gmail);

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

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }
        Videos videos = videoService.findVideoById(videoId);

        Courses course = courseService.getCourseById(courseId);
        List<Videos> videosInCourse = course.getVideos();
        Collections.sort(videosInCourse, Comparator.comparing(Videos::getUploadedAt));

        model.addAttribute("videoLearn", videos);
        model.addAttribute("courseVideo", course);
        model.addAttribute("listVideoLearn", videosInCourse);

        return "users/learnPage";
    }
}
