package com.example.project.repo;

import java.time.LocalDateTime;

public interface PaymentDetailHistory {
    Integer getStt();             // Số thứ tự (sử dụng ROW_NUMBER)

    String getCourseTitle();      // Tên khóa học

    Integer getPrice();           // Giá

    String getUsername();         // Người mua

    LocalDateTime getEnrollmentDate(); // Ngày mua
}