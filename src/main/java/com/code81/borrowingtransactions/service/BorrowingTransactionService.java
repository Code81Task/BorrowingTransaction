package com.code81.borrowingtransactions.service;


import com.code81.borrowingtransactions.dto.BorrowingTransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowingTransactionService {

    Page<BorrowingTransactionDto> getTransactions(Long memberId, Long bookId, Pageable pageable);
    BorrowingTransactionDto getTransactionById(Long id);
    BorrowingTransactionDto createTransaction(BorrowingTransactionDto dto);
    BorrowingTransactionDto returnBook(Long id);
    void deleteTransaction(Long id);
    void markOverdueTransactions();
}
