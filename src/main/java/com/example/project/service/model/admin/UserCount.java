package com.example.project.service.model.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserCount {
    int all;
    int learner;
    int lecturer;
    int admin;
}
