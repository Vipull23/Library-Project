package org.gfg.DigitalLibrary.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Book {
    private Integer bookId;
    private String description;
    private String bookName;
    double price ;
    private String publisher;
    private Author author;
    private BookType bookType;

}
