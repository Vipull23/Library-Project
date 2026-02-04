package org.gfg.DigitalLibrary.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Author {
    private Integer id;
    private String name;
    private String email;
    private String mobileNumber;
    List<Book> books;
}
