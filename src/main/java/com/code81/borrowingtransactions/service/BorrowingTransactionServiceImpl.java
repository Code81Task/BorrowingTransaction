package com.code81.borrowingtransactions.service;

import com.code81.borrowingtransactions.dto.BorrowingTransactionDto;
import com.code81.borrowingtransactions.entity.BorrowingTransaction;
import com.code81.borrowingtransactions.error.BorrowingTransactionApiException;
import com.code81.borrowingtransactions.mapper.BorrowingTransactionMapper;
import com.code81.borrowingtransactions.repo.BorrowingTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class BorrowingTransactionServiceImpl implements BorrowingTransactionService {
    @Autowired
    private  BorrowingTransactionRepo repo;

    @Value("${borrowing.fee.per-day:10}")
    private long feePerDay;

    private static final List<String> ACTIVE_STATUSES = Arrays.asList("Borrowed", "Overdue");

    @Override
    public Page<BorrowingTransactionDto> getTransactions(Long memberId, Long bookId, Pageable pageable) {
        Page<BorrowingTransaction> transactions;

        if (memberId != null && bookId != null) {
            transactions = repo.findByMemberIdAndBookId(memberId, bookId, pageable);
        } else if (memberId != null) {
            transactions = repo.findByMemberId(memberId, pageable);
        } else if (bookId != null) {
            transactions = repo.findByBookId(bookId, pageable);
        } else {
            transactions = repo.findAll(pageable);
        }

        if (transactions.isEmpty()) {
            throw new BorrowingTransactionApiException("No transactions found for the given filters");
        }

        return transactions.map(BorrowingTransactionMapper::toDto);
    }




    @Override
    public BorrowingTransactionDto getTransactionById(Long id) {
        BorrowingTransaction t = repo.findById(id)
                .orElseThrow(() -> new BorrowingTransactionApiException("Transaction not found: " + id));
        return BorrowingTransactionMapper.toDto(t);
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BorrowingTransactionDto createTransaction(BorrowingTransactionDto dto) {
        if (dto.getBookId() == null || dto.getMemberId() == null || dto.getDueDate() == null) {
            throw new BorrowingTransactionApiException("Book ID, Member ID and Due Date are required");
        }

        Long bookId = dto.getBookId();

        // ✅ استخدم الـ Lock هنا عشان تمنع الـ race condition
        List<BorrowingTransaction> active = repo.findActiveByBookIdForUpdate(bookId, ACTIVE_STATUSES);
        if (!active.isEmpty()) {
            throw new BorrowingTransactionApiException(
                    "Book is currently borrowed by another member (bookId=" + bookId + ")"
            );
        }

        BorrowingTransaction entity = BorrowingTransactionMapper.toEntity(dto);
        entity.setBorrowDate(LocalDate.now());
        entity.setStatus("Borrowed");
        entity.setOverdueDays(null);
        entity.setOverdueFee(null);

        BorrowingTransaction saved = repo.save(entity);
        return BorrowingTransactionMapper.toDto(saved);
    }


    @Override
    @Transactional
    public BorrowingTransactionDto returnBook(Long id) {
        BorrowingTransaction transaction = repo.findById(id)
                .orElseThrow(() -> new BorrowingTransactionApiException("Transaction not found with id: " + id));

        if ("Returned".equals(transaction.getStatus())) {
            throw new BorrowingTransactionApiException("Book already returned for transaction id: " + id);
        }

        LocalDate now = LocalDate.now();
        transaction.setReturnDate(now);

        long overdueDays = 0;
        if (transaction.getDueDate() != null && now.isAfter(transaction.getDueDate())) {
            overdueDays = ChronoUnit.DAYS.between(transaction.getDueDate(), now);
            transaction.setStatus("Returned"); // we still set Returned, but we recorded overdueDays
        } else {
            transaction.setStatus("Returned");
        }

        transaction.setOverdueDays((int) overdueDays);
        transaction.setOverdueFee(overdueDays * feePerDay);

        BorrowingTransaction updated = repo.save(transaction);
        return BorrowingTransactionMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        BorrowingTransaction t = repo.findById(id)
                .orElseThrow(() -> new BorrowingTransactionApiException("Transaction not found: " + id));
        repo.delete(t);
    }


    @Override
    @Transactional
    public void markOverdueTransactions() {
        LocalDate today = LocalDate.now();
        List<BorrowingTransaction> list = repo.findByStatusAndDueDateBefore("Borrowed", today);
        for (BorrowingTransaction t : list) {
            t.setStatus("Overdue");
            long overdueDays = ChronoUnit.DAYS.between(t.getDueDate(), today);
            t.setOverdueDays((int) overdueDays);
            t.setOverdueFee(overdueDays * feePerDay);
        }
        repo.saveAll(list);
    }
}
