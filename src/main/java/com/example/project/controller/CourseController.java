package com.example.project.controller;

import com.example.project.model.Courses;
import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.model.Videos;
import com.example.project.service.AuthenticationService;
import com.example.project.service.CourseService;
import com.example.project.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class CourseController {

    @Autowired
    CourseService courseService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    PaymentService paymentService;

    @GetMapping("/listCourses")
    public String listCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String level,
            @PageableDefault(size = 16) Pageable pageable,
            Model model) {

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

        Page<Courses> coursePage;

        coursePage = courseService.getCoursesByFilters(category, topic, level, pageable);

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());

        // --- Logic cho việc hiển thị số trang --- (giữ nguyên)
        int totalPages = coursePage.getTotalPages();
        int currentPage = pageable.getPageNumber();
        int pagesToShow = 5;

        if (totalPages > 0) {
            List<Integer> pageNumbers;
            if (totalPages <= pagesToShow) {
                pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                        .boxed()
                        .collect(Collectors.toList());
            } else {
                int startPage = Math.max(0, currentPage - (pagesToShow / 2));
                int endPage = Math.min(totalPages - 1, currentPage + (pagesToShow / 2));

                if (endPage - startPage + 1 < pagesToShow) {
                    if (startPage == 0) {
                        endPage = Math.min(totalPages - 1, startPage + pagesToShow - 1);
                    } else if (endPage == totalPages - 1) {
                        startPage = Math.max(0, endPage - pagesToShow + 1);
                    }
                }
                pageNumbers = IntStream.rangeClosed(startPage, endPage)
                        .boxed()
                        .collect(Collectors.toList());
            }
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "listCourses";
    }

    @GetMapping("/courseDetail/{courseId}")
    public String showCourseDetail(@PathVariable("courseId") Integer courseId, Model model) {

        Courses course = courseService.getCourseById(courseId);
        List<Courses> coursesList = courseService.recommendCourses(course.getCategory(), courseId);

        model.addAttribute("courseDetail", course);
        model.addAttribute("coursesList", coursesList);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();
            Users user = new Users();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                user = authenticationService.getInforUserByGmail(gmail);

                if (user != null) {
                    model.addAttribute("user_account", user);
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
            boolean checkPayment = paymentService.hasUserPaidForCourse(user,course);
            model.addAttribute("checkPayment", checkPayment);
        }

        Optional<Videos> earliestVideo = course.getVideos().stream()
                .min(Comparator.comparing(Videos::getUploadedAt));

        // Thêm video sớm nhất vào model nếu nó tồn tại
        earliestVideo.ifPresent(video -> model.addAttribute("earliestVideo", video));
        return "courseDetail";
    }

    @GetMapping("/search_courses")
    public String showCourses(@RequestParam(name = "search", required = false) String searchTerm, Model model,
                              @PageableDefault(size = 16) Pageable pageable) {

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

        Page<Courses> coursePage;

        coursePage = courseService.searchCoursesByNameAndStatusPublished(searchTerm, pageable);

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());

        // --- Logic cho việc hiển thị số trang --- (giữ nguyên)
        int totalPages = coursePage.getTotalPages();
        int currentPage = pageable.getPageNumber();
        int pagesToShow = 5;

        if (totalPages > 0) {
            List<Integer> pageNumbers;
            if (totalPages <= pagesToShow) {
                pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                        .boxed()
                        .collect(Collectors.toList());
            } else {
                int startPage = Math.max(0, currentPage - (pagesToShow / 2));
                int endPage = Math.min(totalPages - 1, currentPage + (pagesToShow / 2));

                if (endPage - startPage + 1 < pagesToShow) {
                    if (startPage == 0) {
                        endPage = Math.min(totalPages - 1, startPage + pagesToShow - 1);
                    } else if (endPage == totalPages - 1) {
                        startPage = Math.max(0, endPage - pagesToShow + 1);
                    }
                }
                pageNumbers = IntStream.rangeClosed(startPage, endPage)
                        .boxed()
                        .collect(Collectors.toList());
            }
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "listCourses"; // Tên trang Thymeleaf/JSP hiển thị danh sách khóa học
    }

}
