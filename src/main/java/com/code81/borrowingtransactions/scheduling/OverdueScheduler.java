package com.code81.borrowingtransactions.scheduling;

import com.code81.borrowingtransactions.service.BorrowingTransactionService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OverdueScheduler {

    private final BorrowingTransactionService transactionService;

    @Scheduled(cron = "0 5 0 * * *")
    public void markOverdue() {
        transactionService.markOverdueTransactions();
    }
}
