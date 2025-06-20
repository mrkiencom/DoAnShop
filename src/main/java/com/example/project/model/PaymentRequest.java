package com.example.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PaymentRequest {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String vnpTxnRef;
    private Integer courseId;
    private Integer userId;
    private LocalDateTime createdAt;
    private String status; // "pending", "success", "failed"

    // Getters / Setters
}

