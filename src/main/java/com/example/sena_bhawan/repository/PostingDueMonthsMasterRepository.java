package com.example.sena_bhawan.repository;
import com.example.sena_bhawan.entity.PostingDueMonthsMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostingDueMonthsMasterRepository
        extends JpaRepository<PostingDueMonthsMaster, Long> {

    @Query("""
        SELECT p.months
        FROM PostingDueMonths p
        WHERE p.isActive = true
        ORDER BY p.months
    """)
    List<Integer> findActiveMonths();
}
