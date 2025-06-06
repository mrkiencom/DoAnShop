package com.example.project.repo;

import com.example.project.model.Courses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends JpaRepository<Courses, Integer> {
    List<Courses> findByLecturerId(Integer lecturerId);

    @Query("SELECT c FROM Courses c " +
            "WHERE c.status = 'Published' " + // Thêm điều kiện này
            "AND (:category IS NULL OR :category = '' OR c.category = :category) " +
            "AND (:topic IS NULL OR :topic = '' OR c.topic = :topic) " +
            "AND (:level IS NULL OR :level = '' OR :level = 'All' OR c.level = :level)")
    Page<Courses> findCoursesByFilters(@Param("category") String category,
                                       @Param("topic") String topic,
                                       @Param("level") String level,
                                       Pageable pageable);

    @Query("SELECT c FROM Courses c " +
            "WHERE c.status = 'Published' " +
            "AND c.category = :category " +
            "AND c.courseId != :excludeCourseId")
    List<Courses> findRecommendedCoursesByCategoryAndExcludeId(
            @Param("category") String category,
            @Param("excludeCourseId") Integer excludeCourseId);

    List<Courses> findTop10ByOrderByDateDesc();

    @Query("SELECT c FROM Courses c " +
            "WHERE c.status = 'Published' " +
            "AND (:courseName IS NULL OR :courseName = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :courseName, '%')))")
    Page<Courses> findCoursesByNameAndStatus(@Param("courseName") String courseName, Pageable pageable);
}
