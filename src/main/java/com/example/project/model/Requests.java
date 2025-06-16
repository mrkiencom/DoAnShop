package com.example.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Requests {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int submissionId;
    private String submissionName;
    private String type;
    private String description;
    private String status;
    @CreatedDate
    private LocalDateTime createdAt;
    private LocalDateTime doneDate;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Users user;

    public Requests() {

    }
}
