package com.code81.borrowingtransactions.error;

public class BorrowingTransactionApiException extends RuntimeException {
    public BorrowingTransactionApiException(String message) {
        super(message);
    }


    public BorrowingTransactionApiException() {
        super();
    }

}
