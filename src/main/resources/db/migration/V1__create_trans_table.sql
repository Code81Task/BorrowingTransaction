-- Borrowing Transactions table
CREATE TABLE borrowing_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'Borrowed'
);

-- Indexes for performance
CREATE INDEX idx_book_id ON borrowing_transactions(book_id);
CREATE INDEX idx_member_id ON borrowing_transactions(member_id);