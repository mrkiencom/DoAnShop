package com.example.project.controller;

import com.example.project.model.Courses;
import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.model.Videos;
import com.example.project.service.AuthenticationService;
import com.example.project.service.LecturerService;
import com.example.project.service.RequestService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class LecturerController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    LecturerService lecturerService;

    @Autowired
    UserService userService;

    @Autowired
    RequestService requestService;

    @PostMapping("/createCourses")
    public String createCourses(@ModelAttribute("course") final Courses course, final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }
        course.setLecturer(lecturer);

        final Integer id_course = lecturerService.createCourses(course);
        model.addAttribute("new_id_create_course", id_course);
        model.addAttribute("createCourseSuccess", true);
        return "redirect:/uploadVideo/" + id_course;
    }

    @GetMapping("/uploadVideo/{courseId}")
    public String uploadVideo(final Model model, @PathVariable final Integer courseId,
                              @RequestParam(value = "createCourseSuccess", required = false) final boolean createCourseSuccess) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

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
    public String createVideos(@PathVariable final Integer courseId, @ModelAttribute("video") final Videos video
            , final Model model, final RedirectAttributes redirectAttributes) {
        final Courses course = lecturerService.getCourseById(courseId);
        lecturerService.uploadVideo(video, course);
        redirectAttributes.addFlashAttribute("uploadVideoSuccess", true);
        return "redirect:/uploadVideo/" + courseId;
    }

    @GetMapping("/lecturerPage")
    public String lecturerPage(
            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(value = "size", required = false, defaultValue = "7") final int size,
            final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        final Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());
        final Page<Courses> courses = lecturerService.getCoursesByLecturer(lecturer.getId(), pageable);
        model.addAttribute("bellList", requestService.getLimitRequestBellByUserId(lecturer.getId()));
        model.addAttribute("courses", courses);

        return "lecturers/lecturerPage";
    }

    @GetMapping("/createCourses")
    public String createCourses(final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }
        model.addAttribute("bellList", requestService.getLimitRequestBellByUserId(lecturer.getId()));

        model.addAttribute("course", new Courses());
        return "lecturers/createCourses";
    }

    @GetMapping("/updateCourses/{courseId}")
    public String getCourseById(@PathVariable final Integer courseId, final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        final Courses getCourseById = lecturerService.getCourseById(courseId);
        model.addAttribute("getCourseById", getCourseById);

        final List<Videos> listVideoByCourses = getCourseById.getVideos();
        model.addAttribute("listVideoByCourses", listVideoByCourses);

        model.addAttribute("updateCourse", new Courses());

        return "lecturers/updateCourses";
    }

    @PostMapping("/updateCourses/{courseId}")
    public String processUpdateCourse(@PathVariable final Integer courseId, @ModelAttribute("updateCourse") final Courses updatedCourse, final Model model) {
        // Lấy thông tin giảng viên (tương tự như trên)
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        final Courses existingCourse = lecturerService.getCourseById(courseId);

        existingCourse.setTitle(updatedCourse.getTitle());
        existingCourse.setCategory(updatedCourse.getCategory());
        existingCourse.setTopic(updatedCourse.getTopic());
        existingCourse.setPrice(updatedCourse.getPrice());
        existingCourse.setLevel(updatedCourse.getLevel());
        existingCourse.setContent(updatedCourse.getContent());
        existingCourse.setRequirement(updatedCourse.getRequirement());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setImage(updatedCourse.getImage());
        final Date currentDate = new Date();
        existingCourse.setDate(currentDate);

        lecturerService.saveCourse(existingCourse);
        model.addAttribute("bellList", requestService.getLimitRequestBellByUserId(lecturer.getId()));
        return "redirect:/lecturerPage";
    }

    @GetMapping("/deleteCourses/{courseId}")
    public String deleteCourseById(@PathVariable final Integer courseId, final Model model) {
        lecturerService.deleteCourseById(courseId);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        model.addAttribute("bellList", requestService.getLimitRequestBellByUserId(lecturer.getId()));
        final Pageable pageable = PageRequest.of(0, 7, Sort.by("date").ascending());
        final Page<Courses> courses = lecturerService.getCoursesByLecturer(lecturer.getId(), pageable);
        model.addAttribute("courses", courses);
        return "redirect:/lecturerPage";
    }

    @GetMapping("/updateVideo/{videoId}")
    public String getVideoById(@PathVariable final Integer videoId, final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        final Videos getVideoById = lecturerService.getVideoById(videoId);
        model.addAttribute("getVideoById", getVideoById);

        model.addAttribute("updateVideo", new Videos());

        return "lecturers/updateVideo";
    }

    @PostMapping("/updateVideo/{videoId}")
    public String processUpdateVideo(@PathVariable final Integer videoId, @ModelAttribute("updateVideo") final Videos updatedVideo, final Model model) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        final Videos existingVideo = lecturerService.getVideoById(videoId);

        existingVideo.setTitle(updatedVideo.getTitle());
        existingVideo.setVideoUrl(updatedVideo.getVideoUrl());
        existingVideo.setDuration(updatedVideo.getDuration());
        existingVideo.setDescription(updatedVideo.getDescription());
        final Date currentDate = new Date();
        existingVideo.setUploadedAt(currentDate);

        lecturerService.saveVideo(existingVideo);
        model.addAttribute("bellList", requestService.getLimitRequestBellByUserId(lecturer.getId()));
        return "redirect:/lecturerPage";
    }

    @GetMapping("/deleteVideo/{videoId}")
    public String deleteVideoById(@PathVariable final Integer videoId, final Model model) {
        lecturerService.deleteVideoById(videoId);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);

                }
            }
        }

        model.addAttribute("bellList", requestService.getLimitRequestBellByUserId(lecturer.getId()));
        final Pageable pageable = PageRequest.of(0, 7, Sort.by("date").ascending());
        final Page<Courses> courses = lecturerService.getCoursesByLecturer(lecturer.getId(), pageable);
        model.addAttribute("courses", courses);
        return "redirect:/lecturerPage";
    }

    @GetMapping("/image_lecturer")
    public String getImage(final Model model) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        return "lecturers/updateImage";
    }

    @GetMapping("/profile_lecturer")
    public String getInformation(final Model model) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

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
    public String updateInformation(final Model model, @ModelAttribute("updateProfileLecturer") final Users informationUser) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        final Users existingUser = userService.getUserById(informationUser.getId());

        existingUser.setFirstname(informationUser.getFirstname());
        existingUser.setLastname(informationUser.getLastname());
        existingUser.setGmail(informationUser.getGmail());

        userService.saveUser(existingUser);

        model.addAttribute("messageSuccessfully", "Cập nhật thành công!");

        return "lecturers/profileLecturer";
    }

    @GetMapping("/changePassword_lecturer")
    public String changePasswordPage(final Model model) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users lecturer = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                lecturer = authenticationService.getInforUserByGmail(gmail);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                lecturer = authenticationService.getInforUser(username);

                if (lecturer != null) {
                    model.addAttribute("lecturer_account", lecturer);
                }
            }
        }

        model.addAttribute("changePasswordLecturer", new Users());

        return "lecturers/changePasswordLecturer";
    }

    @GetMapping("/request-to-lecturer")
    public String showRequestForm(@RequestParam("id") final String id, final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                final Users user = authenticationService.getInforUserByGmail(gmail);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                final Users user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        model.addAttribute("id", id);
        return "request_lecturer"; // tên file .html trong templates
    }

    @PostMapping("/submit-request")
    public String handleRequestSubmit(@RequestParam("id") final int id, @RequestParam("description") final String description, final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                final Users user = authenticationService.getInforUserByGmail(gmail);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                final Users user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        if (Objects.equals(lecturerService.handleSubmitRequest(id, description), "successful")) {
            model.addAttribute("message", "successful");
        } else {
            model.addAttribute("message", "failed");
        }
        return "request_lecturer";
    }

    @GetMapping("/search-lecturer-course")
    public String searchLecturer(
            @RequestParam("id") final int id,
            @RequestParam(value = "text", required = false) final String text,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "size", defaultValue = "7") final int size,
            final Model model) {
        final var lecturer = userService.getUserById(id);
        final String keyword = (text != null && !text.trim().isEmpty()) ? text.trim() : null;
        final Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());
        final Page<Courses> courses = lecturerService.searchCourses(keyword, id, pageable);

        model.addAttribute("bellList", requestService.getLimitRequestBellByUserId(id));
        model.addAttribute("courses", courses);
        model.addAttribute("lecturer_account", lecturer);
        model.addAttribute("searchText", keyword);

        return "lecturers/lecturerPage";
    }
}
