package com.example.project.model;// Thay đổi package của bạn

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment") // Tên bảng trong database
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne //
    @JoinColumn(name = "user_id")
    private Users user; // Đối tượng User

    @ManyToOne //
    @JoinColumn(name = "course_id")
    private Courses course; // Đối tượng Course

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    // Constructors
    public Payments() {
        this.enrollmentDate = LocalDateTime.now(); // Gán thời gian hiện tại khi tạo
    }

    public Payments(Users user, Courses course) {
        this.user = user;
        this.course = course;
        this.enrollmentDate = LocalDateTime.now();
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Courses getCourse() {
        return course;
    }

    public void setCourse(Courses course) {
        this.course = course;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", user=" + user +
                ", course=" + course +
                ", enrollmentDate=" + enrollmentDate +
                '}';
    }
}