package org.gfg.DigitalLibrary.repository;

import org.gfg.DigitalLibrary.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // FIX #7: refactored from queryForObject() (which throws EmptyResultDataAccessException
    // when no author matches, forcing callers to use exceptions for normal control flow)
    // to query() + Optional, so a "not found" result is just an empty Optional.
    public Optional<Author> checkAuthor(String email, String mobileNumber) {
        String query = "SELECT * FROM Author WHERE email=? AND mobileNumber=?";
        List<Author> authors = jdbcTemplate.query(query, (rs, rowNum) -> {
            Author mapAuthor = new Author();
            mapAuthor.setName(rs.getString("name"));
            mapAuthor.setEmail(rs.getString("email"));
            return mapAuthor;
        }, email, mobileNumber);

        return authors.stream().findFirst();
    }

    public int createAuthor(Author author) {
        String query = "INSERT INTO Author (name, email, mobileNumber) VALUES  (?,?,?)";
        int rowsUpdated = jdbcTemplate.update(query, author.getName(), author.getEmail(), author.getMobileNumber());
        if (rowsUpdated > 0) {
            System.out.println("Author information has been inserted");
        }
        return rowsUpdated;
    }
}