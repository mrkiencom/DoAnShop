package com.example.project.repo;

import com.example.project.model.Requests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepo extends JpaRepository<Requests, Integer> {
    @Query("""
            select r from Requests r order by r.createdAt desc limit 5
            """)
    List<Requests> getInBell();

    @Query("""
                    select r from Requests r left join r.user where
                    (:text = '' or lower(r.description) like lower(concat('%', :text, '%'))
                                 or lower(r.submissionName) like lower(concat('%',:text,'%')))
                    and (:type = '' or r.type = :type)
                    and (:status = '' or r.status = :status)
            """)
    Page<Requests> getRequests(String text, String type, String status, Pageable pageable);


    @Query("""
                    select r from Requests r left join r.user u
                    where u.id = :id and r.type = 'FROM_ADMIN'
                    order by r.createdAt desc limit 5
            """)
    List<Requests> getInBellByUserId(int id);
}
