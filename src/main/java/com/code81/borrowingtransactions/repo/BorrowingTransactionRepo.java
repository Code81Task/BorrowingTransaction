package com.code81.borrowingtransactions.repo;


import com.code81.borrowingtransactions.entity.BorrowingTransaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BorrowingTransactionRepo extends JpaRepository<BorrowingTransaction, Long> {

    Page<BorrowingTransaction> findByMemberId(Long memberId, Pageable pageable);

    Page<BorrowingTransaction> findByBookId(Long bookId, Pageable pageable);

    Page<BorrowingTransaction> findByMemberIdAndBookId(Long memberId, Long bookId, Pageable pageable);


    List<BorrowingTransaction> findByStatusAndDueDateBefore(String status, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM BorrowingTransaction t WHERE t.bookId = :bookId AND t.status IN :statuses")
    List<BorrowingTransaction> findActiveByBookIdForUpdate(@Param("bookId") Long bookId,
                                                           @Param("statuses") List<String> statuses);
}
