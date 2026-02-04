package org.gfg.DigitalLibrary.model;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data  // for getter and setter -> lombok
@Builder  // uses builder patter -> lombok
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Integer id;

    private String name;
    private String email;
    private String mobileNumber;
    private Address address;
    private String dob;
    private StudentStatus studentStatus;
    List<Book> issuedBooks;
    Date createdOn;
    Date updatedOn;


}
