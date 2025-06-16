package com.example.project.service;

import com.example.project.exception.NotFoundException;
import com.example.project.model.Courses;
import com.example.project.model.Requests;
import com.example.project.model.Users;
import com.example.project.repo.CourseRepo;
import com.example.project.repo.RequestRepo;
import com.example.project.repo.UserRepo;
import com.example.project.service.model.admin.CardCount;
import com.example.project.service.model.admin.Chart;
import com.example.project.service.model.admin.CourseInfo;
import com.example.project.service.model.admin.Dashboard;
import com.example.project.service.model.admin.RequestInfo;
import com.example.project.service.model.admin.UserCount;
import com.example.project.service.model.admin.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final RequestRepo requestRepo;

    private static final String LECTURER_ROLE = "lecturer";
    private static final String LEARNER_ROLE = "learner";

    public Dashboard getDashboard() {
        final var users = getUsers("timeRageUser").getUsers();
        final var courses = courseRepo.findAll();

        return Dashboard.builder()
                .users(users)
                .courses(courses)
                .build();
    }

    public UserInfo buildUserInfo(final Users user) {
        return UserInfo.builder()
                .id(user.getId())
                .fullName(user.getFirstname() + ' ' + user.getLastname())
                .gmail(user.getGmail())
                .picture(user.getPicture())
                .role(user.getRole())
                .status(user.isActive())
                .createdAt(user.getCreatedAt().toLocalDate())
                .build();
    }

    public Dashboard getUsers(final String timeRageUser) {
        final var users = switch (timeRageUser) {
            case "lastMonth" -> userRepo.findUserByTimeRage(LocalDateTime.now()
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0));
            case "lastYear" -> userRepo.findUserByTimeRage(LocalDate.now()
                    .withDayOfMonth(1)
                    .atStartOfDay());
            default -> userRepo.findUserByTimeRage((LocalDateTime.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .minusWeeks(1)));
        };

        return Dashboard.builder()
                .users(users.stream().map(this::buildUserInfo).toList())
                .courses(null)
                .userChart(Chart.builder()
                        .labels(List.of("1", "4", "8", "12"))
                        .counts(List.of(10, 15, 8, 12))
                        .build())
                .build();
    }

    public CardCount count() {
        final var user = userRepo.findOnlyUsers().stream()
                .map(this::buildUserInfo).toList();
        final var course = courseRepo.findAll();

        final var learner = user.stream().filter(u -> u.getRole().equals(LEARNER_ROLE) || u.getRole().equals("user")).toList();
        final var lecturer = user.stream().filter(u -> u.getRole().equals(LECTURER_ROLE)).toList();

        return CardCount.builder()
                .lecturers(lecturer.size())
                .courses(course.size())
                .learners(learner.size())
                .build();
    }

    public Page<UserInfo> filterUsers(final String filter, final String status, final String text, final Pageable pageable) {

        return userRepo.filterUsers(
                filter.isEmpty() ? null : filter,
                status.isEmpty() ? null : status,
                text.isEmpty() ? null : text,
                pageable).map(this::buildUserInfo);
    }

    public UserCount countUser() {
        final var users = userRepo.findAll().stream().map(this::buildUserInfo).toList();

        final var learner = users.stream().filter(u -> u.getRole().equals(LEARNER_ROLE) || u.getRole().equals("user")).toList();
        final var lecturer = users.stream().filter(u -> u.getRole().equals(LECTURER_ROLE)).toList();
        final var admin = users.stream().filter(u -> u.getRole().equals("admin")).toList();

        return UserCount.builder()
                .all(users.size())
                .learner(learner.size())
                .lecturer(lecturer.size())
                .admin(admin.size())
                .build();
    }

    public UserInfo changeActive(final int userId) {
        final var user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Cannot found user with id:" + String.valueOf(userId)));
        user.setActive(!user.isActive());
        return buildUserInfo(userRepo.save(user));
    }

    public Page<CourseInfo> getCourse(final String text, final String status, final String level, final String category, final String topic, final Pageable page) {
        final var course = courseRepo.findAllBy(text, status, level, category, topic, page);
        return course.map(this::buildCourseInfo);
    }

    public CourseInfo buildCourseInfo(final Courses course) {
        return CourseInfo.builder()
                .courseId(course.getCourseId())
                .image(course.getImage())
                .price(course.getPrice())
                .category(course.getCategory())
                .content(course.getContent())
                .description(course.getDescription())
                .is_paid(course.isIs_paid())
                .title(course.getTitle())
                .lecturer(course.getLecturer() != null
                        ? course.getLecturer().getFirstname() + ' ' + course.getLecturer().getFirstname()
                        : "")
                .level(course.getLevel())
                .requirement(course.getRequirement())
                .topic(course.getTopic())
                .build();
    }

    private RequestInfo buildRequest(final Requests request) {
        return RequestInfo.builder()
                .id(request.getId())
                .senderId(request.getUser().getId())
                .submissionName(request.getSubmissionName())
                .sender(request.getUser().getUsername())
                .message(request.getDescription())
                .type(request.getType())
                .status(request.getStatus())
                .timestamp(request.getCreatedAt())
                .doneDate(request.getDoneDate())
                .build();
    }

    public Page<RequestInfo> getRequests(final String text, final String type, final String status, final Pageable pageable) {
        return requestRepo.getRequests(text, type, status, pageable).map(this::buildRequest);
    }

    public RequestInfo getRequestById(final int id) {
        return requestRepo.findById(id).map(this::buildRequest).orElse(null);
    }

    public RequestInfo handleRequest(final int id, final boolean approve) {
        final var request = requestRepo.findById(id).orElseThrow();

        if (request.getDoneDate() != null) {
            return null;
        }

        final var type = request.getType();

        final var status = approve ? "Approved" : "Denied";

        if (type.equals("To Lecturer")) {
            final var user = request.getUser();
            if (approve) {
                user.setRole("lecturer");
                userRepo.save(user);
            }
        } else {
            final var course = courseRepo.findById(request.getSubmissionId()).orElseThrow();
            course.setStatus(approve ? "Approved" : "Denied");
        }

        request.setStatus(status);
        request.setDoneDate(LocalDateTime.now());

        requestRepo.save(request);

        return buildRequest(request);
    }
}
