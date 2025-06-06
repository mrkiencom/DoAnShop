package com.example.project.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "Courses")
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "category", length = 255)
    private String category;

    @Column(name = "topic", length = 255)
    private String topic;

    @Column(name = "price")
    private Integer price;

    @Column(name = "level", length = 50)
    private String level;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "requirement", columnDefinition = "TEXT")
    private String requirement;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "status", length = 50)
    private String status;

    // Quan hệ nhiều-một với bảng Users (nhiều khóa học được tạo bởi một giảng viên)
    @ManyToOne
    @JoinColumn(name = "lecturer_id", referencedColumnName = "id")
    private Users lecturer;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Videos> videos;

    @Column(name = "is_paid")
    private boolean is_paid;

    public Courses() {
    }

    public Courses(String title, String category, String topic, Integer price, String level, String content, String requirement, String description, String image, Date date, String status, Users lecturer, boolean is_paid) {
        this.title = title;
        this.category = category;
        this.topic = topic;
        this.price = price;
        this.level = level;
        this.content = content;
        this.requirement = requirement;
        this.description = description;
        this.image = image;
        this.date = date;
        this.status = status;
        this.lecturer = lecturer;
        this.is_paid = is_paid;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Videos> getVideos() {
        return videos;
    }

    public void setVideos(List<Videos> videos) {
        this.videos = videos;
    }

    public Users getLecturer() {
        return lecturer;
    }

    public void setLecturer(Users lecturer) {
        this.lecturer = lecturer;
    }

    public boolean isIs_paid() {
        return is_paid;
    }

    public void setIs_paid(boolean is_paid) {
        this.is_paid = is_paid;
    }

    @Override
    public String toString() {
        return "Courses{" +
                "courseId=" + courseId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", topic='" + topic + '\'' +
                ", price=" + price +
                ", level='" + level + '\'' +
                ", content='" + content + '\'' +
                ", requirement='" + requirement + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", lecturer=" + lecturer +
                ", is_paid=" + is_paid +
                '}';
    }
}
