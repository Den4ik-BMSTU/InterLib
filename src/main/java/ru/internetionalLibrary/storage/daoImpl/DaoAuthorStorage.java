package ru.internetionalLibrary.storage.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.internetionalLibrary.exceptions.EmptyResultFromDataBaseException;
import ru.internetionalLibrary.exceptions.ValidationException;
import ru.internetionalLibrary.models.Author;
import ru.internetionalLibrary.models.Book;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DaoAuthorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DaoAuthorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Author> getAllAuthors() {
        String sqlQuery = "SELECT * FROM AUTHORS ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToAuthor);
    }

    public Author getAuthorById(Integer id) {
        String sqlQuery = "SELECT * FROM AUTHORS WHERE id = ?";
        Author director;
        try {
            director = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToAuthor, id);
            return director;
        } catch (Exception e) {
            throw new EmptyResultFromDataBaseException("Автор c id: " + id + " не найден");
        }
    }

    public Author addAuthor(Author author) {
        String sql = "SELECT * FROM AUTHORS WHERE ID = ?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql, author.getId());
        if (directorRows.next()) {
            log.error("Такой автор уже существует! {}", author);
            throw new ValidationException("Такой автор уже существует!");
        }
        String sqlQuery = "INSERT INTO AUTHORS(NAME) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, author.getName());
            return ps;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        author.setId(id);
        return author;
    }

    public Author updateAuthor(Author author) {
        String sql = "SELECT * FROM AUTHORS WHERE ID = ?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql, author.getId());
        if (!directorRows.next()) {
            log.error("Такого режиссера не существует! {}", author);
            throw new ValidationException("Такого режиссера не существует!");
        }
        String sqlQuery = "UPDATE AUTHORS SET NAME = ? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery,
                author.getName(),
                author.getId());
        return author;
    }

    public void removeAuthor(Integer id) {
        String sqlQuery = "DELETE FROM BOOK_AUTHORS WHERE ID_AUTHOR = ?";
        jdbcTemplate.update(sqlQuery, id);
        String sql = "DELETE FROM AUTHORS WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Author> getAuthorsByIdBook(int id) {
        String sqlQuery = "SELECT * FROM AUTHORS WHERE ID IN (SELECT ID_AUTHORS FROM BOOK_AUTHORS WHERE ID_BOOK = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToAuthor, id);
    }

    public void addOrUpdateBookAuthors(Book book) {
        if (book.getAuthors() != null) {
            String sqlDirectors = "DELETE FROM BOOK_AUTHORS WHERE ID_BOOK = ?";
            jdbcTemplate.update(sqlDirectors, book.getId());
            for (Author director : book.getAuthors()) {
                sqlDirectors = "MERGE INTO BOOK_AUTHORS(ID_BOOK, ID_AUTHOR) VALUES (?, ?)";
                jdbcTemplate.update(sqlDirectors, book.getId(), director.getId());
            }
        } else {
            String sqlDirectors = "DELETE FROM BOOK_AUTHORS WHERE ID_BOOK = ?";
            jdbcTemplate.update(sqlDirectors, book.getId());
        }
    }

    private Author mapRowToAuthor(ResultSet resultSet, int i) throws SQLException {
        return Author.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
