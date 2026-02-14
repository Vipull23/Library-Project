package org.gfg.DigitalLibrary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private Integer bookId;
    private String description;
    private String bookName;
    double price ;
    private String publisher;
    private Author author;
    private BookType bookType;

}
