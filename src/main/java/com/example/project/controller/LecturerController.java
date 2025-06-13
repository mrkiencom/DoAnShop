package com.example.project.controller;

import com.example.project.model.Courses;
import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.model.Videos;
import com.example.project.service.AuthenticationService;
import com.example.project.service.LecturerService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
public class LecturerController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    LecturerService lecturerService;

    @Autowired
    UserService userService;

    @PostMapping("/createCourses")
    public String createCourses(@ModelAttribute("course") Courses course, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }
        course.setLecturer(lecturer);

        Integer id_course = lecturerService.createCourses(course);
        model.addAttribute("new_id_create_course", id_course);
        model.addAttribute("createCourseSuccess", true);
        return "redirect:/uploadVideo/" + id_course;
    }

    @GetMapping("/uploadVideo/{courseId}")
    public String uploadVideo(Model model, @PathVariable Integer courseId,
                              @RequestParam(value = "createCourseSuccess", required = false) boolean createCourseSuccess){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }
        model.addAttribute("courseId", courseId);
        model.addAttribute("createCourseSuccess", createCourseSuccess);

        model.addAttribute("video", new Videos());

        return "lecturers/uploadVideo";
    }

    @PostMapping("/uploadVideo/{courseId}")
    public String createVideos(@PathVariable Integer courseId, @ModelAttribute("video") Videos video
            , Model model, RedirectAttributes redirectAttributes){
        Courses course = lecturerService.getCourseById(courseId);
        lecturerService.uploadVideo(video, course);
        redirectAttributes.addFlashAttribute("uploadVideoSuccess", true);
        return "redirect:/uploadVideo/" + courseId;
    }

    @GetMapping("/lecturerPage")
    public String lecturerPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        List<Courses> courses = lecturerService.getCoursesByLecturer(lecturer.getId());
        model.addAttribute("courses", courses);

        return "lecturers/lecturerPage";
    }

    @GetMapping("/createCourses")
    public String createCourses(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        model.addAttribute("course", new Courses());
        return "lecturers/createCourses";
    }

    @GetMapping("/updateCourses/{courseId}")
    public String getCourseById(@PathVariable Integer courseId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        Courses getCourseById = lecturerService.getCourseById(courseId);
        model.addAttribute("getCourseById", getCourseById);

        List<Videos> listVideoByCourses = getCourseById.getVideos();
        model.addAttribute("listVideoByCourses", listVideoByCourses);

        model.addAttribute("updateCourse", new Courses());

        return "lecturers/updateCourses";
    }

    @PostMapping("/updateCourses/{courseId}")
    public String processUpdateCourse(@PathVariable Integer courseId, @ModelAttribute("updateCourse") Courses updatedCourse, Model model) {
        // Lấy thông tin giảng viên (tương tự như trên)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        Courses existingCourse = lecturerService.getCourseById(courseId);

        existingCourse.setTitle(updatedCourse.getTitle());
        existingCourse.setCategory(updatedCourse.getCategory());
        existingCourse.setTopic(updatedCourse.getTopic());
        existingCourse.setPrice(updatedCourse.getPrice());
        existingCourse.setLevel(updatedCourse.getLevel());
        existingCourse.setContent(updatedCourse.getContent());
        existingCourse.setRequirement(updatedCourse.getRequirement());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setImage(updatedCourse.getImage());
        Date currentDate = new Date();
        existingCourse.setDate(currentDate);

        lecturerService.saveCourse(existingCourse);

        return "redirect:/lecturerPage";
    }

    @GetMapping("/deleteCourses/{courseId}")
    public String deleteCourseById(@PathVariable Integer courseId, Model model){
        lecturerService.deleteCourseById(courseId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        List<Courses> courses = lecturerService.getCoursesByLecturer(lecturer.getId());
        model.addAttribute("courses", courses);
        return "redirect:/lecturerPage";
    }

    @GetMapping("/updateVideo/{videoId}")
    public String getVideoById(@PathVariable Integer videoId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        Videos getVideoById = lecturerService.getVideoById(videoId);
        model.addAttribute("getVideoById", getVideoById);

        model.addAttribute("updateVideo", new Videos());

        return "lecturers/updateVideo";
    }

    @PostMapping("/updateVideo/{videoId}")
    public String processUpdateVideo(@PathVariable Integer videoId, @ModelAttribute("updateVideo") Videos updatedVideo, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        Videos existingVideo = lecturerService.getVideoById(videoId);

        existingVideo.setTitle(updatedVideo.getTitle());
        existingVideo.setVideoUrl(updatedVideo.getVideoUrl());
        existingVideo.setDuration(updatedVideo.getDuration());
        existingVideo.setDescription(updatedVideo.getDescription());
        Date currentDate = new Date();
        existingVideo.setUploadedAt(currentDate);

        lecturerService.saveVideo(existingVideo);

        return "redirect:/lecturerPage";
    }

    @GetMapping("/deleteVideo/{videoId}")
    public String deleteVideoById(@PathVariable Integer videoId, Model model){
        lecturerService.deleteVideoById(videoId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        List<Courses> courses = lecturerService.getCoursesByLecturer(lecturer.getId());
        model.addAttribute("courses", courses);
        return "redirect:/lecturerPage";
    }

    @GetMapping("/image_lecturer")
    public String getImage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        return "lecturers/updateImage";
    }

    @GetMapping("/profile_lecturer")
    public String getInformation(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        model.addAttribute("updateProfileLecturer", new Users());

        return "lecturers/profileLecturer";
    }

    @PostMapping("/profile_lecturer")
    public String updateInformation(Model model, @ModelAttribute("updateProfileLecturer") Users informationUser) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        Users existingUser = userService.getUserById(informationUser.getId());

        existingUser.setFirstname(informationUser.getFirstname());
        existingUser.setLastname(informationUser.getLastname());
        existingUser.setGmail(informationUser.getGmail());

        userService.saveUser(existingUser);

        model.addAttribute("messageSuccessfully", "Cập nhật thành công!");

        return "lecturers/profileLecturer";
    }

    @GetMapping("/changePassword_lecturer")
    public String changePasswordPage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        model.addAttribute("changePasswordLecturer", new Users());

        return "lecturers/changePasswordLecturer";
    }
}
