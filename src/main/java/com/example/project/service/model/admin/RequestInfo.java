package com.example.project.service.model.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RequestInfo {
    int id;
    String submissionName;
    int courseId;
    String message;
    int senderId;
    String sender;
    String type;
    String status;
    LocalDateTime timestamp;
    LocalDateTime doneDate;
}
