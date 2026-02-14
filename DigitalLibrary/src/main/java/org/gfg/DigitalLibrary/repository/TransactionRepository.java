package org.gfg.DigitalLibrary.repository;

import org.gfg.DigitalLibrary.model.Transaction;
import org.gfg.DigitalLibrary.request.BookTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class TransactionRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int issueBookToStudent(BookTransactionRequest bookTransactionRequest) {
        String txnId = UUID.randomUUID().toString();
        int studentId = bookTransactionRequest.getStudentId();
        int bookId = bookTransactionRequest.getBookId();
        String cost = bookTransactionRequest.getAmount();
        String txnType = "ISSUE";
        java.sql.Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String sql = "";
        int result = -1;

        if ("ISSUE".equalsIgnoreCase(bookTransactionRequest.getRequestType())) {
            String bookQuery = "UPDATED BOOK SET STUDENT_ID =? WHERE BOOK_ID=?";
            jdbcTemplate.update(bookQuery, studentId, bookId);
            sql = "INSERT INTO Transactions (TXN_ID, STUDENT_ID,BOOK_ID,ISSUED_TIME, UPDATED_TIME, COST, TXN_TYPE)" +
                    " VALUES (?,?,?,?,?,?,?)";
            txnType = "ISSUE";
            result = jdbcTemplate.update(sql, txnId, studentId, bookId, currentTime, currentTime, cost, txnType);
        } else if ("RENEW".equalsIgnoreCase(bookTransactionRequest.getRequestType())) {
            sql = "UPDATE transactions SET TXN_TYPE=?, UPDATED_TIME=? WHERE STUDENT_ID=? AND BOOK_ID=?";
            txnType = "RENEW";
            result = jdbcTemplate.update(sql, currentTime, txnType, studentId, bookId);
        } else {
            String bookQuery = "UPDATE BOOK SET STUDENT_ID=?, WHERE BOOK_ID=?";
            jdbcTemplate.update(bookQuery, null, bookId);
            sql = "UPDATE transactions SET TXN_TYPE=?, UPDATED_TIME=?,COST=? WHERE BOOK_ID=? AND STUDENT_ID=?";
            txnType = "RETURN";
            int fine = calculateFine(bookTransactionRequest);
            result = jdbcTemplate.update(sql, txnType, currentTime, fine, bookId, studentId);
        }

        return result;
    }

    private int calculateFine(BookTransactionRequest bookTransactionRequest) {
        String getDataQuery = "SELECT * FROM transactions WHERE STUDENT_ID=? AND BOOK_ID=? ";
        Transaction transaction = jdbcTemplate.queryForObject(getDataQuery, new RowMapper<Transaction>() {
            @Override
            public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
                Transaction transaction = new Transaction();
                transaction.setCreatedOn(rs.getTimestamp("ISSUED TIME"));
                return transaction;
            }
        });

        long issuedTime = transaction.getCreatedOn().getTime();
        long currentTime = System.currentTimeMillis();

        long diff = TimeUnit.DAYS.convert(currentTime - issuedTime, TimeUnit.MILLISECONDS) + 1;

        return (int) diff * 2 - (Integer.parseInt(bookTransactionRequest.getAmount()));
    }
}
