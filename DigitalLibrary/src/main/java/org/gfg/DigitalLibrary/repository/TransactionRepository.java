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
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String sql = "";
        int result = -1;

        if ("ISSUE".equalsIgnoreCase(bookTransactionRequest.getRequestType())) {
            // FIX: 'book' primary key is 'id', foreign key is 'student_id'
            String bookQuery = "UPDATE book SET student_id = ? WHERE id = ?";
            jdbcTemplate.update(bookQuery, studentId, bookId);

            // FIX: Table is 'transaction' (singular, lowercase)
            sql = "INSERT INTO transaction (txn_id, student_id, book_id, issued_time, updated_time, cost, txn_type) " +
                    "VALUES (?,?,?,?,?,?,?)";
            txnType = "ISSUE";
            result = jdbcTemplate.update(sql, txnId, studentId, bookId, currentTime, currentTime, cost, txnType);
        } else if ("RENEW".equalsIgnoreCase(bookTransactionRequest.getRequestType())) {
            sql = "UPDATE transaction SET txn_type = ?, updated_time = ? WHERE student_id = ? AND book_id = ?";
            txnType = "RENEW";
            result = jdbcTemplate.update(sql, txnType, currentTime, studentId, bookId);
        } else {
            String bookQuery = "UPDATE book SET student_id = ? WHERE id = ?";
            jdbcTemplate.update(bookQuery, (Object) null, bookId);

            sql = "UPDATE transaction SET txn_type = ?, updated_time = ?, cost = ? WHERE book_id = ? AND student_id = ?";
            txnType = "RETURN";
            int fine = calculateFine(bookTransactionRequest);
            result = jdbcTemplate.update(sql, txnType, currentTime, fine, bookId, studentId);
        }

        return result;
    }

    private int calculateFine(BookTransactionRequest bookTransactionRequest) {
        String getDataQuery = "SELECT * FROM transaction WHERE student_id = ? AND book_id = ?";

        Transaction transaction = jdbcTemplate.queryForObject(getDataQuery, new RowMapper<Transaction>() {
            @Override
            public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
                Transaction transaction = new Transaction();
                transaction.setCreatedOn(rs.getTimestamp("issued_time"));
                return transaction;
            }
        }, bookTransactionRequest.getStudentId(), bookTransactionRequest.getBookId());

        long issuedTime = transaction.getCreatedOn().getTime();
        long currentTime = System.currentTimeMillis();

        long diff = TimeUnit.DAYS.convert(currentTime - issuedTime, TimeUnit.MILLISECONDS) + 1;

        return (int) diff * 2 - (Integer.parseInt(bookTransactionRequest.getAmount()));
    }
}


