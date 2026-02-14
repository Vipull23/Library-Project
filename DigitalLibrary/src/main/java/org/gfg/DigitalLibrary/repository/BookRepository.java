package org.gfg.DigitalLibrary.repository;

import org.gfg.DigitalLibrary.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class BookRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int createBookInDatabase(Book book) {
        String query = "INSERT INTO Book (id, name, description, bookType, bookPrice, publisher) VALUES (?,?,?,?,?,?)";
        int rows = jdbcTemplate.update(query, book.getBookId(), book.getBookName(), book.getDescription(), book.getBookType().toString(), book.getPrice(), book.getPublisher());

        if (rows > 0) {
            System.out.println("Book inserted");
        }
        return rows;
    }

    public Book findBookById(int id) {
        String query = "SELECT BOOK_ID FROM book where BOOK_ID=?";
        Book book = jdbcTemplate.queryForObject(query, new RowMapper<Book>() {
            @Override
            public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
                Book book = new Book();
                book.setBookId(rs.getInt(1));
                book.setBookName(rs.getString(2));
                book.setDescription(rs.getString(3));
                return book;
            }
        }, id);
        return book;
    }
}
