package org.gfg.DigitalLibrary.service;

import org.gfg.DigitalLibrary.model.Author;
import org.gfg.DigitalLibrary.model.Book;
import org.gfg.DigitalLibrary.repository.AuthorRepository;
import org.gfg.DigitalLibrary.repository.BookRepository;
import org.gfg.DigitalLibrary.request.BookCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        boolean authorExist = false;

        try {
            //Create Author if not exist
            Author dbAuthor = authorRepository.checkAuthor(author.getEmail(), author.getMobileNumber());
            if (dbAuthor == null || dbAuthor.getName() == null || dbAuthor.getName().isEmpty()) {
                //author does not exist we need to create author

                // create book in database
                int rows = authorRepository.createAuthor(author);
            }
//            bookUpdated = bookRepository.createBookInDatabase(book);
            authorExist = true;

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Going to create author");
            authorExist = false;
        }
        try {
            if (!authorExist) {
                int rows = authorRepository.createAuthor(author);

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
