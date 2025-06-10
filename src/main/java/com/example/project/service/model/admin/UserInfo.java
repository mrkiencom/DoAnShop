package com.example.project.service.model.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserInfo {
    String fullName;
    String role;
    String picture;
    String gmail;
    LocalDate createdAt;
    boolean status;
}
