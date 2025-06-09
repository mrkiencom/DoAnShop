package com.example.project.service.model.admin;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CardCount {
    int learners;
    int lecturers;
    int courses;
}
