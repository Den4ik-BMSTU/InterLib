package ru.internetionalLibrary.controllers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.internetionalLibrary.models.Author;
import ru.internetionalLibrary.services.AuthorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/author")
public class AuthorController {
    @Getter
    AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService directorService){
        this.authorService = directorService;
    }
    @GetMapping
    public List<Author> getAllAuthors(){
        log.info("Запрос списка авторов из базы");
        return authorService.getAllAuthors();
    }

    @GetMapping("/{id}")
    public Author getAuthorById(@PathVariable("id") Integer id){
        log.info("Запрос автора с id: {} из базы", id);
        return authorService.getAuthorById(id);
    }

    @PostMapping
    public Author addAuthor(@Valid @RequestBody Author author) {
        log.info("Запрос добавления автора с имененем: {} в базе", author.getName());
        return authorService.addAuthor(author);
    }

    @PutMapping
    public Author updateAuthor(@Valid @RequestBody Author author) {
        log.info("Запрос обновления автора с id: {} в базе", author.getId());
        return authorService.updateAuthor(author);
    }

    @DeleteMapping("/{id}")
    public void removeAuthor(@PathVariable("id") Integer id) {
        log.info("Запрос удаления автора с id: {} в базе", id);
        authorService.removeAuthor(id);
    }
}
