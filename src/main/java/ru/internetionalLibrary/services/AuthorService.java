package ru.internetionalLibrary.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.internetionalLibrary.models.Author;
import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.storage.daoImpl.DaoAuthorStorage;

import java.util.List;

@Service
@Slf4j
public class AuthorService {

    private final DaoAuthorStorage daoAuthorStorage;

    @Autowired
    public AuthorService(DaoAuthorStorage daoAuthorStorage){
        this.daoAuthorStorage = daoAuthorStorage;
    }

    public List<Author> getAllAuthors() {
        return daoAuthorStorage.getAllAuthors();
    }

    public Author getAuthorById(Integer id) {
        return daoAuthorStorage.getAuthorById(id);
    }

    public Author addAuthor(Author author) {
        return daoAuthorStorage.addAuthor(author);
    }

    public Author updateAuthor(Author author) {
        return daoAuthorStorage.updateAuthor(author);
    }

    public void removeAuthor(Integer id) {
        daoAuthorStorage.removeAuthor(id);
    }

    public List<Author> getAuthorsByIdBook(int id) {
        return daoAuthorStorage.getAuthorsByIdBook(id);
    }

    public void addOrUpdateBookAuthors(Book book) {
        daoAuthorStorage.addOrUpdateBookAuthors(book);
    }
}
