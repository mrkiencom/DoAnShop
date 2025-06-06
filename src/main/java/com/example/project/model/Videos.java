package com.example.project.model;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.*;

@Entity
@Table(name = "Videos")
public class Videos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Video_id")
    private Integer videoId;

    @ManyToOne
    @JoinColumn(name = "Course_id")
    private Courses course;

    @Column(name = "Title", length = 255)
    private String title;

    @Column(name = "Video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "Duration")
    private Float duration;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Uploaded_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt;

    public Videos() {
    }

    public Videos(Courses course, String title, String videoUrl, Float duration, String description, Date uploadedAt) {
        this.course = course;
        this.title = title;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.description = description;
        this.uploadedAt = uploadedAt;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Courses getCourse() {
        return course;
    }

    public void setCourse(Courses course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String toString() {
        return "Videos{" +
                "videoId=" + videoId +
                ", course=" + course +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
