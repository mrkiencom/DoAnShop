package com.example.project.repo;

import com.example.project.model.Courses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepo extends JpaRepository<Courses, Integer> {
    Page<Courses> findByLecturerId(Integer lecturerId, Pageable pageable);

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

    @Query("""
            select c from Courses c left join c.lecturer l where
            (:text = '' or  lower(c.description ) like lower(concat('%', :text,'%'))
                            or  lower(c.content ) like lower(concat('%', :text,'%'))  
                            or lower(c.title ) like lower(concat('%', :text,'%'))
                            or  lower(c.requirement ) like lower(concat('%', :text,'%'))   
                            or   lower(c.topic ) like lower(concat('%', :text,'%'))
                            or   lower(concat(l.firstname,' ',l.lastname)) like lower(concat('%', :text,'%')))
            and (:status = '' or c.status = :status)               
            and (:level  = '' or c.level = :level)               
            and (:category = '' or c.category = :category)               
            and (:topic = '' or c.topic = :topic)
            """)
    Page<Courses> findAllBy(String text, String status, String level, String category, String topic, Pageable pageable);

    @Query("""
                SELECT SUM(p.course.price)
                FROM Payments p
            """)
    Long sumTotalRevenue();

    @Query("""
                SELECT FUNCTION('YEAR', p.enrollmentDate) AS year,
                       FUNCTION('MONTH', p.enrollmentDate) AS month,
                       SUM(p.course.price) AS totalRevenue
                FROM Payments p
                GROUP BY FUNCTION('YEAR', p.enrollmentDate), FUNCTION('MONTH', p.enrollmentDate)
                ORDER BY FUNCTION('YEAR', p.enrollmentDate), FUNCTION('MONTH', p.enrollmentDate)
            """)
    List<MonthlyRevenue> getMonthlyRevenue();

    @Query("""
                SELECT p.course.title AS title,
                       COUNT(p.id) AS purchaseCount,
                       SUM(p.course.price) AS totalRevenue
                FROM Payments p
                GROUP BY p.course.title
                ORDER BY SUM(p.course.price) DESC
            """)
    List<CourseRevenue> getRevenueByCourse();

    @Query("""
                SELECT p.user.username as fullName, COUNT(p.id) as purchaseCount, SUM(p.course.price) as totalRevenue
                FROM Payments p
                GROUP BY p.user.username
                ORDER BY SUM(p.course.price) DESC
            """)
    List<UserRevenue> getRevenueByUser();

    @Query(value = """
            SELECT 
                ROW_NUMBER() OVER (ORDER BY p.enrollment_date DESC) AS stt,
                c.title AS courseTitle,
                c.price AS price,
                u.username AS username,
                p.enrollment_date AS enrollmentDate
            FROM payment p
            JOIN courses c ON p.course_id = c.course_id
            JOIN users u ON p.user_id = u.id
            WHERE p.enrollment_date BETWEEN :startDate AND :endDate
              AND (
                    :searchText = '' or :searchText is null or
                  LOWER(c.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
                  LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%'))
              )
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM payment p
                    JOIN courses c ON p.course_id = c.course_id
                    JOIN users u ON p.user_id = u.id
                    WHERE p.enrollment_date BETWEEN :startDate AND :endDate
                      AND (
                         :searchText = '' or :searchText is null or
                          LOWER(c.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
                          LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%'))
                      )
                    """,
            nativeQuery = true)
    Page<PaymentDetailHistory> findPaymentsByDateAndSearchText(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("searchText") String searchText,
            Pageable pageable
    );

    @Query("""
            select c from Courses c left join c.videos left join c.lecturer
            where c.courseId = :id
            """)
    Optional<Courses> getCourseDetailById(int id);

    @Query("""
                SELECT c FROM Courses c left join c.lecturer cl
                WHERE cl.id = :id 
                  AND (
                      :text IS NULL OR :text = '' OR 
                      LOWER(c.title) LIKE LOWER(CONCAT('%', :text, '%')) OR 
                      LOWER(c.category) LIKE LOWER(CONCAT('%', :text, '%'))
                  )
            """)
    Page<Courses> searchCoursesById(final String text, final int id, Pageable pageable);
}
