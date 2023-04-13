package ru.internetionalLibrary.storage.interf;

import ru.internetionalLibrary.models.Book;

import java.time.Year;
import java.util.List;

public interface BookStorage {

    Book addBook(Book book);
    void removeBook(Integer id);
    Book updateBook(Book book);
    List<Book> getBooks();
    Book getBookById(Integer bookId);

    Book addLikeFromUserById(Integer bookId, Integer userId);

    Book removeLikeFromUserById(Integer bookId, Integer userId);

    List<Book> findCommon(int userId, int friendsId);

    public List<Book> searchBooks(String substring, String by) throws IllegalArgumentException;
}
