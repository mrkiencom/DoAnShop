package com.example.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Users {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String gmail;
    private String picture;
    private String role;
    private String firstname;
    private String lastname;
    private LocalDateTime createdAt;
    private LocalDateTime toLecturerAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // Quan hệ một-nhiều với bảng Courses (một giảng viên có nhiều khóa học)
    @OneToMany(mappedBy = "lecturer")
    private List<Courses> createdCourses;

    public Users() {
    }

    public Users(final String username, final String password, final String gmail, final String picture, final String role, final String firstname, final String lastname) {
        this.username = username;
        this.password = password;
        this.gmail = gmail;
        this.picture = picture;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gmail='" + gmail + '\'' +
                ", picture='" + picture + '\'' +
                ", role='" + role + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
