package com.example.project.controller;

import com.example.project.model.Courses;
import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.model.Videos;
import com.example.project.service.AuthenticationService;
import com.example.project.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;

@Controller
public class LecturerController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    LecturerService lecturerService;

    @PostMapping("/createCourses")
    public String createCourses(@ModelAttribute("course") Courses course, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);
        course.setLecturer(user);

        Integer id_course = lecturerService.createCourses(course);
        model.addAttribute("new_id_create_course", id_course);
        model.addAttribute("createCourseSuccess", true);
        return "redirect:/uploadVideo/" + id_course;
    }

    @GetMapping("/uploadVideo/{courseId}")
    public String uploadVideo(Model model,@PathVariable Integer courseId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);

        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());
        model.addAttribute("courseId", courseId);

        model.addAttribute("video", new Videos());

        return "lecturers/uploadVideo";
    }

    @PostMapping("/uploadVideo/{courseId}")
    public String createVideos(@PathVariable Integer courseId, @ModelAttribute("video") Videos video, Model model){
        Courses course = lecturerService.getCourseById(courseId);
        lecturerService.uploadVideo(video, course);
        return "redirect:/lecturerPage";
    }

    @GetMapping("/lecturerPage")
    public String lecturerPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);

        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

        List<Courses> courses = lecturerService.getCoursesByLecturer(user.getId());
        model.addAttribute("courses", courses);

        return "lecturers/lecturerPage";
    }

    @GetMapping("/createCourses")
    public String createCourses(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);

        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

        model.addAttribute("course", new Courses());
        return "lecturers/createCourses";
    }

    @GetMapping("/updateCourses/{courseId}")
    public String getCourseById(@PathVariable Integer courseId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);

        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

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
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();
        Users user = authenticationService.getInforUser(username);
        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

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
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);

        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

        List<Courses> courses = lecturerService.getCoursesByLecturer(user.getId());
        model.addAttribute("courses", courses);
        return "redirect:/lecturerPage";
    }

    @GetMapping("/updateVideo/{videoId}")
    public String getVideoById(@PathVariable Integer videoId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);

        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

        Videos getVideoById = lecturerService.getVideoById(videoId);
        model.addAttribute("getVideoById", getVideoById);

        model.addAttribute("updateVideo", new Videos());

        return "lecturers/updateVideo";
    }

    @PostMapping("/updateVideo/{videoId}")
    public String processUpdateVideo(@PathVariable Integer videoId, @ModelAttribute("updateVideo") Videos updatedVideo, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();
        Users user = authenticationService.getInforUser(username);
        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

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
        Object principal = authentication.getPrincipal();
        UserPrincipal userDetails = (UserPrincipal) principal;
        String username = userDetails.getUsername();

        Users user = authenticationService.getInforUser(username);

        model.addAttribute("name_lecturer", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("email_lecturer", user.getGmail());
        model.addAttribute("picture_lecturer", user.getPicture());
        model.addAttribute("role_name_lecturer", user.getRole());

        List<Courses> courses = lecturerService.getCoursesByLecturer(user.getId());
        model.addAttribute("courses", courses);
        return "redirect:/lecturerPage";
    }
}
