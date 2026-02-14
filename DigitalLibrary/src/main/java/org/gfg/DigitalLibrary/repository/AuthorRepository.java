package org.gfg.DigitalLibrary.repository;

import org.gfg.DigitalLibrary.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AuthorRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Author checkAuthor(String email, String mobileNumber) {
//        String query = "SELECT * FROM Author WHERE email = " + email + "AND mobile = " + mobileNumber ;
        String query = "SELECT * FROM Author WHERE email=? AND mobileNumber=?";
        Author author = jdbcTemplate.queryForObject(query, new RowMapper<Author>() {
            @Override
            public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (rs == null || rs.wasNull()) {
                    return null;
                }
                Author mapAuthor = new Author();
                mapAuthor.setName(rs.getString("name"));
                mapAuthor.setEmail(rs.getString("email"));
                return mapAuthor;
            }
        },email,mobileNumber);

        System.out.println("Author: " + author);
        return author;
    }

    public int createAuthor(Author author) {
        String query = "INSERT INTO Author (name, email, mobileNumber) VALUES  (?,?,?)";
        int rowsUpdated = jdbcTemplate.update(query, author.getName(), author.getEmail(), author.getMobileNumber());
        if (rowsUpdated > 0) {
            System.out.println("Author information has been insert");
        }
        return rowsUpdated;
    }


}
