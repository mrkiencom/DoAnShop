package com.example.project.service;

import com.example.project.MailProperties;
import com.example.project.model.Courses;
import com.example.project.model.Requests;
import com.example.project.model.Videos;
import com.example.project.repo.CourseRepo;
import com.example.project.repo.RequestRepo;
import com.example.project.repo.UserRepo;
import com.example.project.repo.VideoRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LecturerService {

    private final CourseRepo courseRepo;
    private final VideoRepo videoRepo;
    private final UserRepo userRepo;
    private final RequestRepo requestRepo;
    private final NotificationService notificationService;
    private final MailProperties mailProperties;


    public Integer createCourses(final Courses course) {
        final Date currentDate = new Date();
        if (course.getPrice() == 0) {
            course.setIs_paid(false);
        } else {
            course.setIs_paid(true);
        }
        course.setDate(currentDate);
        course.setStatus("Waiting");
        final Courses savedCourse = courseRepo.save(course);

        sendRequestToCreateCourse(course);
        return savedCourse.getCourseId();
    }

    public void uploadVideo(final Videos video, final Courses Course) {
        final Date currentDate = new Date();
        video.setUploadedAt(currentDate);
        video.setCourse(Course);
        videoRepo.save(video);
    }

    private void sendRequestToCreateCourse(final Courses courses) {
        final var request = Requests.builder()
                .status("Waiting")
                .description("A new course has been submitted for approval. "
                        + "You can view the details at: "
                        + "<a href='" + mailProperties.getDomain()
                        + "/adminPage/course-details?id=" + courses.getCourseId()
                        + "' target='_blank' style='color:#1d4ed8; text-decoration:underline;'>"
                        + "View Course Details</a>")
                .user(courses.getLecturer())
                .type("Up load course")
                .submissionName(courses.getTitle())
                .submissionId(courses.getCourseId())
                .createdAt(LocalDateTime.now())
                .build();

        final var requestSaved = requestRepo.save(request);
        notificationService.notifyAdmin(requestSaved.getId(), request.getType(),
                request.getUser().getFirstname() + ' ' + request.getUser().getLastname(), "Request to upload course", request.getUser().getId());

    }

    public Courses getCourseById(final Integer courseId) {
        final Optional<Courses> optionalCourse = courseRepo.findById(courseId);
        return optionalCourse.orElse(null);
    }

    public Page<Courses> getCoursesByLecturer(final Integer lecturerId, final Pageable pageable) {
        return courseRepo.findByLecturerId(lecturerId, pageable);
    }

    public Page<Courses> searchCourses(final String text, final int id, final Pageable pageable) {
        return courseRepo.searchCoursesById(text, id, pageable);
    }

    public void saveCourse(final Courses course) {
        courseRepo.save(course);
    }

    public void deleteCourseById(final Integer courseId) {
        courseRepo.deleteById(courseId);
    }

    public Videos getVideoById(final Integer videoId) {
        final Optional<Videos> optionalVideo = videoRepo.findById(videoId);
        return optionalVideo.orElse(null);
    }

    public void saveVideo(final Videos video) {
        videoRepo.save(video);
    }

    public void deleteVideoById(final Integer videoId) {
        videoRepo.deleteById(videoId);
    }

    public String handleSubmitRequest(final int userId, final String description) {
        final var user = userRepo.findById(userId).orElseThrow();

        final var request = Requests.builder()
                .status("Waiting")
                .description(description)
                .user(user)
                .type("To Lecturer")
                .submissionName(user.getFirstname() + ' ' + user.getLastname())
                .createdAt(LocalDateTime.now())
                .build();

        final var requestSaved = requestRepo.save(request);
        notificationService.notifyAdmin(requestSaved.getId(), "To Lecturer", user.getFirstname() + ' ' + user.getLastname(), description, userId);

        return "successful";
    }
}
