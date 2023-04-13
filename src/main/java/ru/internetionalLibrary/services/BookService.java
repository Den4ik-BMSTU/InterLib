package ru.internetionalLibrary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.models.User;
import ru.internetionalLibrary.storage.interf.BookStorage;

import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Service
@Slf4j
@Qualifier("daoFilmStorage")
@RequiredArgsConstructor
public class BookService {
    private final UserService userService;
    private final BookStorage bookStorage;

    public List<Book> getBooks(){
        return bookStorage.getBooks();
    }
    public Book getBookById(Integer bookId) {
        return bookStorage.getBookById(bookId);
    }

    public Book addBook(Book book){
        return bookStorage.addBook(book);
    }

    public Book updateBook(Book book){
        return bookStorage.updateBook(book);
    }
    public void removeBook(Integer id){
        bookStorage.removeBook(id);
    }
    public Book addLikeFromUserById(Integer bookId, Integer userId){
        Book book = bookStorage.getBookById(bookId);
        User user = userService.getUserById(userId);

        return bookStorage.addLikeFromUserById(book.getId(), user.getId());
    }
    public Book removeLikeFromUserById(Integer bookId, Integer userId){
        Book book = bookStorage.getBookById(bookId);
        User user = userService.getUserById(userId);

        return bookStorage.removeLikeFromUserById(book.getId(), user.getId());
    }

    public UserService getUserService() {
        return userService;
    }

    public List<Book> findCommon (int userId, int friendId){
        List<Book> common = bookStorage.findCommon(userId, friendId);
        common.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());

                return common;
    }

    public List<Book> searchBook(String substring, String by) throws IllegalArgumentException {
        return bookStorage.searchBooks(substring, by);
    }
}