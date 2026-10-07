package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data Access Layer (Repository) for BorrowRecord Entity.
 */
@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByMemberId(Long memberId);
    List<BorrowRecord> findByStatus(String status);
}
