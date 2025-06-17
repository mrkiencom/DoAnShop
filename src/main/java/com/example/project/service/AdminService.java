package com.example.project.service;

import com.example.project.exception.NotFoundException;
import com.example.project.model.Courses;
import com.example.project.model.Requests;
import com.example.project.model.Users;
import com.example.project.repo.CourseRepo;
import com.example.project.repo.CourseRevenue;
import com.example.project.repo.MonthlyRevenue;
import com.example.project.repo.PaymentDetailHistory;
import com.example.project.repo.RequestRepo;
import com.example.project.repo.UserRepo;
import com.example.project.repo.UserRevenue;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
        final LocalDateTime endDate = LocalDateTime.now();
        final LocalDateTime fromDate = switch (timeRageUser) {
            case "lastMonth" -> LocalDateTime.now().minusMonths(1);
            case "lastWeek" -> LocalDateTime.now().minusWeeks(1);
            case "lastYear" -> LocalDateTime.now().minusYears(1);
            default -> null;
        };

        final List<Users> users = userRepo.findUserByTimeRange(fromDate, endDate);

        return Dashboard.builder()
                .users(users.stream()
                        .map(user -> buildUserInfo(user)) // tránh dùng this:: nếu lỗi
                        .toList())
                .userChart(getUserChartByMonth(LocalDateTime.now().minusYears(1), endDate))
                .build();
    }

    public Chart getUserChartByMonth(final LocalDateTime fromDate, final LocalDateTime toDate) {
        final List<Users> users = userRepo.findUserByTimeRange(fromDate, toDate);

        // Group theo tháng-năm
        final Map<String, Long> grouped = users.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().getMonthValue() + "/" + u.getCreatedAt().getYear(),
                        TreeMap::new, // đảm bảo thứ tự tháng tăng dần
                        Collectors.counting()
                ));

        // Chuyển thành dạng list
        final List<String> labels = new ArrayList<>(grouped.keySet());
        final List<Integer> counts = grouped.values().stream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        return Chart.builder().labels(labels).counts(counts).build();
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
                .status(course.getStatus())
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
            course.setStatus(approve ? "Published" : "Denied");
        }

        request.setStatus(status);
        request.setDoneDate(LocalDateTime.now());

        requestRepo.save(request);

        return buildRequest(request);
    }

    public Long getTotalRevenue() {
        final Long total = courseRepo.sumTotalRevenue();
        return total != null ? total : 0L;
    }

    public List<MonthlyRevenue> getMonthlyRevenue() {
        return courseRepo.getMonthlyRevenue();
    }

    public List<CourseRevenue> getRevenueByCourse() {
        return courseRepo.getRevenueByCourse();
    }

    public List<UserRevenue> getRevenueByUser() {
        return courseRepo.getRevenueByUser();
    }

    public Page<PaymentDetailHistory> getPaymentHist(final String text, final LocalDate fromDate, final LocalDate endDate, final Pageable pageable) {
        return courseRepo.findPaymentsByDateAndSearchText(fromDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), text, pageable);
    }

    public Courses getCourseDetail(final int id) {
        return courseRepo.getCourseDetailById(id).orElse(null);
    }
}
