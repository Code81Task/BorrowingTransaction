package com.code81.borrowingtransactions.controller;


import com.code81.borrowingtransactions.dto.BorrowingTransactionDto;
import com.code81.borrowingtransactions.service.BorrowingTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class BorrowingTransactionController {

    private final BorrowingTransactionService service;


    @GetMapping
    public Page<BorrowingTransactionDto> getTransactions(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long bookId,
            Pageable pageable
    ) {
        return service.getTransactions(memberId, bookId, pageable);
    }


    @GetMapping("/{id}")
    public BorrowingTransactionDto getTransactionById(@PathVariable Long id) {
        return service.getTransactionById(id);
    }


    @PostMapping
    public BorrowingTransactionDto createTransaction(@RequestBody BorrowingTransactionDto dto) {
        return service.createTransaction(dto);
    }


    @PutMapping("/{id}/return")
    public BorrowingTransactionDto returnBook(@PathVariable Long id) {
        return service.returnBook(id);
    }





}
