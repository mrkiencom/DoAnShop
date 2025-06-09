package com.example.project.service.model.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Chart {
    List<String> labels;
    List<Integer> counts;
}
