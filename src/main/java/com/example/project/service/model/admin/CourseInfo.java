package com.example.project.service.model.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseInfo {
    Integer courseId;
    String title;
    String category;
    String topic;
    Integer price;
    String level;
    String content;
    String requirement;
    String description;
    String image;
    String status;
    String lecturer;
    boolean is_paid;
}
