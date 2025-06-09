package com.example.project.service.model.admin;

import com.example.project.model.Courses;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Dashboard {
    List<UserInfo> users;
    List<Courses> courses;
    Chart userChart;
}
