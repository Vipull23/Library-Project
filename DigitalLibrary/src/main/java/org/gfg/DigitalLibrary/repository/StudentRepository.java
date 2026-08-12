package org.gfg.DigitalLibrary.repository;

import org.gfg.DigitalLibrary.model.Address;
import org.gfg.DigitalLibrary.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;

@Repository
public class StudentRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;


    private SimpleJdbcInsert jdbcInsert;


    @Autowired
    StudentRepository(DataSource dataSource){
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("Student").usingColumns( "name",
                "email",
                "dob",
                "mobile",
                "status",
                "createdOn",
                "updatedOn").usingGeneratedKeyColumns("id");
        //this.jdbcInsert.withTableName("Student");
        //this.jdbcInsert.usingGeneratedKeyColumns("id");
    }

    public int createStudentInDatabase(Student student){
     /*  int rows = jdbcTemplate.update("INSERT INTO Student(name, email, dob, mobile, status, createdOn, updatedOn) VALUES (?,?,?,?,?,?,?)", student.getName(),
               student.getEmail(),student.getDob(),student.getMobileNo(),student.getStudentStatus(), new Date(),new Date());*/

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("name",student.getName());
        mapSqlParameterSource.addValue("email",student.getEmail());
        mapSqlParameterSource.addValue("dob",student.getDob());
        mapSqlParameterSource.addValue("mobile",student.getMobileNumber());
        mapSqlParameterSource.addValue("status", student.getStudentStatus().name());
        mapSqlParameterSource.addValue("createdOn", new Date());
        mapSqlParameterSource.addValue("updatedOn", new Date());

        Number number = jdbcInsert.executeAndReturnKey(mapSqlParameterSource);
        int studentId = number.intValue();

        System.out.println("Student Data inserted");
        Address address = student.getAddress();
        int ar = jdbcTemplate.update("INSERT INTO address (studentId, street, city,pincode) VALUES (?,?,?,?)", studentId, address.getStreet(), address.getCity(), address.getPincode());

        System.out.println("Rows Updated for address: "+ar);

        return ar;
    }
}



