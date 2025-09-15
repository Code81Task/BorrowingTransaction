package com.code81.borrowingtransactions.mapper;

import com.code81.borrowingtransactions.dto.BorrowingTransactionDto;
import com.code81.borrowingtransactions.entity.BorrowingTransaction;


public class BorrowingTransactionMapper {

    public static BorrowingTransactionDto toDto(BorrowingTransaction e) {
        if (e == null) return null;
        return BorrowingTransactionDto.builder()
                .id(e.getId())
                .bookId(e.getBookId())
                .memberId(e.getMemberId())
                .borrowDate(e.getBorrowDate())
                .dueDate(e.getDueDate())
                .returnDate(e.getReturnDate())
                .status(e.getStatus())
                .overdueDays(e.getOverdueDays())
                .overdueFee(e.getOverdueFee())
                .build();
    }

    public static BorrowingTransaction toEntity(BorrowingTransactionDto d) {
        if (d == null) return null;
        return BorrowingTransaction.builder()
                .id(d.getId())
                .bookId(d.getBookId())
                .memberId(d.getMemberId())
                .borrowDate(d.getBorrowDate())
                .dueDate(d.getDueDate())
                .returnDate(d.getReturnDate())
                .status(d.getStatus() == null ? "Borrowed" : d.getStatus())
                .overdueDays(d.getOverdueDays())
                .overdueFee(d.getOverdueFee())
                .build();
    }
}