package org.gfg.DigitalLibrary.service;

import org.gfg.DigitalLibrary.model.Book;
import org.gfg.DigitalLibrary.model.Transaction;
import org.gfg.DigitalLibrary.repository.BookRepository;
import org.gfg.DigitalLibrary.repository.TransactionRepository;
import org.gfg.DigitalLibrary.request.BookTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    BookRepository bookRepository;

    public Transaction createTransaction(BookTransactionRequest request) {
        int row = transactionRepository.issueBookToStudent(request);
        if (row <= 0) {
            return null;
        }

        Book book = bookRepository.findBookById(request.getBookId());
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        return transaction;
    }
}
