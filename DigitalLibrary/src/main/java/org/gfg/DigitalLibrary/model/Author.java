package org.gfg.DigitalLibrary.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private Integer id;
    private String name;
    private String email;
    private String mobileNumber;
    List<Book> books;
}
