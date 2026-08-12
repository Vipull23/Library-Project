package org.gfg.DigitalLibrary.service;

import org.gfg.DigitalLibrary.model.Author;
import org.gfg.DigitalLibrary.model.Book;
import org.gfg.DigitalLibrary.repository.AuthorRepository;
import org.gfg.DigitalLibrary.repository.BookRepository;
import org.gfg.DigitalLibrary.request.BookCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;


    public Book createBookInDatabase(BookCreationRequest bookCreationRequest) {

        Book book = Book.builder().bookId(bookCreationRequest.getBookId()).bookName(bookCreationRequest.getBookName()).price(bookCreationRequest.getBookPrice()).bookType(bookCreationRequest.getBookType()).publisher(bookCreationRequest.getPublisher()).description(bookCreationRequest.getDescription()).build();

        Author author = Author.builder().name(bookCreationRequest.getAuthorName()).email(bookCreationRequest.getAuthorEmail()).mobileNumber(bookCreationRequest.getAuthorMobile()).build();

        int bookUpdated = 0;

        // FIX #7 (continued): checkAuthor now returns Optional<Author> instead of throwing
        // an exception on "not found", so the old try/catch-as-control-flow is gone.
        // We just check whether the Optional is present.
        Optional<Author> dbAuthor = authorRepository.checkAuthor(author.getEmail(), author.getMobileNumber());
        boolean authorExists = dbAuthor.isPresent() && dbAuthor.get().getName() != null && !dbAuthor.get().getName().isEmpty();

        try {
            if (!authorExists) {
                authorRepository.createAuthor(author);
            }
            bookUpdated = bookRepository.createBookInDatabase(book);
        } catch (Exception e) {
            System.out.println(e);
        }

        if (bookUpdated == 0) {
            return null;
        }
        return book;
    }

}