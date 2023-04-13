package ru.internetionalLibrary;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.internetionalLibrary.models.Author;
import ru.internetionalLibrary.storage.daoImpl.DaoAuthorStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DaoDirectorControllerTest {
    private final DaoAuthorStorage authorStorage;
    private final JdbcTemplate jdbcTemplate;
    Author author1 = Author.builder()
            .id(1)
            .name("ЖульВерн")
            .build();
    Author author2 = Author.builder()
            .id(2)
            .name("ЛевТостой")
            .build();
    Author author3 = Author.builder()
            .id(3)
            .name("ПушкинСергей")
            .build();

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM BOOKS");
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM AUTHORS");
        jdbcTemplate.update("DELETE FROM USERS_FRIENDS");
        jdbcTemplate.update("DELETE FROM BOOK_GENRES");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE BOOKS ALTER COLUMN ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE AUTHORS ALTER COLUMN ID RESTART WITH 1");
    }

    @Test
    public void addAuthorTest(){
        authorStorage.addAuthor(author1);
        Assertions.assertEquals(authorStorage.getAuthorById(1), author1);
    }

    @Test
    public void getAllAuthorsTest(){
        authorStorage.addAuthor(author1);
        authorStorage.addAuthor(author2);
        authorStorage.addAuthor(author3);
        assertEquals(authorStorage.getAllAuthors(), List.of(author1, author2, author3));
    }

    @Test
    public void getAuthorByIdTest(){
        authorStorage.addAuthor(author1);
        authorStorage.addAuthor(author2);
        authorStorage.addAuthor(author3);
        Assertions.assertEquals(authorStorage.getAuthorById(2), author2);
    }

    @Test
    public void updateAuthorTest(){
        authorStorage.addAuthor(author1);
        author1.setName("АлександрРобокопов");
        authorStorage.updateAuthor(author1);
        assertEquals(author1.getName(), "АлександрРобокопов");
    }

    @Test
    public void removeAuthorTest(){
        authorStorage.addAuthor(author1);
        authorStorage.addAuthor(author2);
        authorStorage.addAuthor(author3);
        authorStorage.removeAuthor(2);
        assertEquals(authorStorage.getAllAuthors(), List.of(author1, author3));
    }
}
