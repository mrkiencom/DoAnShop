package com.example.project.repo;

import com.example.project.model.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRequestRepo extends JpaRepository<PaymentRequest, Long> {

    @Query("""
            select p from PaymentRequest p where p.vnpTxnRef = :vnpTxnRef
            """)
    List<PaymentRequest> findAllByVnpTxnRef(String vnpTxnRef);
}
