package org.gfg.DigitalLibrary.controller;

import org.gfg.DigitalLibrary.model.Book;
import org.gfg.DigitalLibrary.request.BookCreationRequest;
import org.gfg.DigitalLibrary.response.BookCreationResponse;
import org.gfg.DigitalLibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    BookService bookService;

    @PostMapping("/create/book")
    public ResponseEntity<BookCreationResponse> createBook(@RequestBody BookCreationRequest bookCreationRequest) {
        Book book = bookService.createBookInDatabase(bookCreationRequest);
        BookCreationResponse bookCreationResponse = new BookCreationResponse();
        if (book == null) {
            bookCreationResponse.setStatus("NOT_CREATED");
            bookCreationResponse.setMessage("Book Not Created");
            return new ResponseEntity<>(bookCreationResponse, HttpStatus.BAD_REQUEST);
        }
        bookCreationResponse.setStatus("CREATED");
        bookCreationResponse.setMessage("Book Created");
        bookCreationResponse.setBookName(book.getBookName());

        return new ResponseEntity<>(bookCreationResponse, HttpStatus.CREATED);
    }
    
}
