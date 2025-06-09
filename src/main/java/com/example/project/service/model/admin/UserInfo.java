package com.example.project.service.model.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfo {
    String fullName;
    String role;
    String picture;
    String gmail;

}
