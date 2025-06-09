package com.example.project.service.model.admin;

import com.example.project.model.Users;
import com.example.project.repo.CourseRepo;
import com.example.project.repo.UserRepo;
import lombok.AllArgsConstructor;
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

    private static final String LECTURER_ROLE = "lecturer";
    private static final String LEARNER_ROLE = "learner";

    public Dashboard getDashboard() {
        final var users = findOnlyUsers().stream().map(this::buildUserInfo).toList();
        final var courses = courseRepo.findAll();

        return Dashboard.builder()
                .users(users)
                .courses(courses)
                .build();
    }

    public List<Users> findOnlyUsers() {
        return userRepo.findOnlyUsers();
    }

    public UserInfo buildUserInfo(final Users user) {
        return UserInfo.builder()
                .fullName(user.getFirstname() + ' ' + user.getLastname())
                .gmail(user.getGmail())
                .picture(user.getPicture())
                .role(user.getToLecturerAt() == null ? "learner" : "lecturer")
                .build();
    }

    public Dashboard getUsers(final String timeRageUser) {
        final var users = switch (timeRageUser) {
            case "lastWeek" -> userRepo.findUserByTimeRage((LocalDateTime.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .minusWeeks(1)));
            case "lastMonth" -> userRepo.findUserByTimeRage(LocalDateTime.now()
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0));
            case "lastYear" -> userRepo.findUserByTimeRage(LocalDate.now()
                    .withDayOfMonth(1)
                    .atStartOfDay());
            default -> userRepo.findAll();
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

        final var learner = user.stream().filter(u -> u.role.equals(LEARNER_ROLE)).toList();
        final var lecturer = user.stream().filter(u -> u.role.equals(LECTURER_ROLE)).toList();

        return CardCount.builder()
                .lecturers(lecturer.size())
                .courses(course.size())
                .learners(learner.size())
                .build();
    }
}
