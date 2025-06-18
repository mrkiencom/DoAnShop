package com.example.project.service;

import com.example.project.repo.RequestRepo;
import com.example.project.service.model.admin.RequestInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RequestService {
    private final RequestRepo requestRepo;

    public List<RequestInfo> getLimitRequestBell() {
        return requestRepo.getInBell().stream()
                .map(i -> RequestInfo.builder()
                        .id(i.getId())
                        .submissionName(i.getSubmissionName())
                        .message(i.getDescription())
                        .senderId(i.getUser().getId())
                        .sender(i.getUser().getFirstname() + ' ' + i.getUser().getLastname())
                        .timestamp(i.getCreatedAt())
                        .build())
                .toList();
    }

    public List<RequestInfo> getLimitRequestBellByUserId(final int id) {
        return requestRepo.getInBellByUserId(id).stream()
                .map(i -> RequestInfo.builder()
                        .id(i.getId())
                        .submissionName(i.getSubmissionName())
                        .courseId(i.getSubmissionId())
                        .message(i.getDescription())
                        .senderId(i.getUser().getId())
                        .sender(i.getUser().getFirstname() + ' ' + i.getUser().getLastname())
                        .timestamp(i.getCreatedAt())
                        .build())
                .toList();
    }
}
