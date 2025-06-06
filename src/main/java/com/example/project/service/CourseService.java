package com.example.project.service;


import com.example.project.model.*;
import com.example.project.repo.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    CourseRepo courseRepo;

    public Courses getCourseById(Integer courseId) {
        Optional<Courses> optionalCourse = courseRepo.findById(courseId);
        return optionalCourse.orElse(null);
    }

    public Page<Courses> getCoursesByFilters(String category, String topic, String level, Pageable pageable) {
        return courseRepo.findCoursesByFilters(category, topic, level, pageable);
    }

    public List<Courses> recommendCourses(String category, Integer excludeCourseId) {

        if (category == null || category.trim().isEmpty()) {
            return List.of();
        }
        return courseRepo.findRecommendedCoursesByCategoryAndExcludeId(category, excludeCourseId);
    }

    public List<Courses> getLatest10Courses() {
        return courseRepo.findTop10ByOrderByDateDesc();
    }

    public Page<Courses> searchCoursesByNameAndStatusPublished(String courseName, Pageable pageable) {
        return courseRepo.findCoursesByNameAndStatus(courseName, pageable);
    }
}
