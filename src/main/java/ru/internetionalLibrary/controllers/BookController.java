package ru.internetionalLibrary.controllers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.exceptions.IncorrectParameterException;
import ru.internetionalLibrary.services.BookService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/books")
public class BookController {
    @Getter
    BookService bookService;

    @Autowired
    public BookController(BookService filmService) {
        this.bookService = filmService;
    }

    @GetMapping
    public List<Book> getBooks() {
        log.info("Запрос списка книг из базы");
        return bookService.getBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable("id") Integer bookId) {
        log.info("Запрос книги с id: {} из базы", bookId);
        return bookService.getBookById(bookId);
    }

    @PostMapping
    public Book addBook(@Valid @RequestBody Book book) {
        log.info("Запрос добавления книги в базу c названием: {}", book.getName());
        return bookService.addBook(book);
    }

    @PutMapping
    public Book updateBook(@Valid @RequestBody Book book) {
        log.info("Запрос обновления книги c id: {} в базе", book.getId());
        return bookService.updateBook(book);
    }

    @DeleteMapping("/{id}")
    public void removeBook(@PathVariable("id") Integer id) {
        log.info("Запрос удаления книги c id: {} из базы", id);
        bookService.removeBook(id);
    }


    @PutMapping("/{id}/like/{userId}")
    public Book addLikeFromUserById(@PathVariable("id") Integer bookId, @PathVariable("userId") Integer userId) {
        log.info("Запрос добавления лайка книге c id: {} от пользователя с id: {}", bookId, userId);
        return bookService.addLikeFromUserById(bookId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Book removeLikeFromUserById(@PathVariable("id") Integer bookId, @PathVariable("userId") Integer userId) {
        log.info("Запрос удаления лайка книги c id: {} от пользователя с id: {}", bookId, userId);
        return bookService.removeLikeFromUserById(bookId, userId);
    }

    @GetMapping("/common")
    public List<Book> getCommonBooks(@RequestParam(value = "userId") int userId, @RequestParam(value = "friendId")
        int friendId){
        log.info("Запрос списка общих книг пользователей с id: {} и {} ", userId, friendId);
        return bookService.findCommon(userId, friendId);
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(name = "query") String query,
                                  @RequestParam(name = "by") String by)
                                  throws IllegalArgumentException {
        log.info("Запрос поиска книг по тексту: {} и параметрам: {}", query, by);
        return bookService.searchBook(query, by);
    }
}
