package ru.internetionalLibrary.storage.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.internetionalLibrary.exceptions.EmptyResultFromDataBaseException;
import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class DaoGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public DaoGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenre() {
        String sqlQuery = "SELECT * " +
                "FROM genres ";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }
    public Genre getGenreById(Integer id) {
        String sqlQuery = "SELECT * " +
                "FROM genres " +
                "WHERE id = ?";
        Genre genre;

        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
            return genre;
        } catch (Exception e) {
            log.info("Genre c id: {} не найден ", id);
            throw new EmptyResultFromDataBaseException("Genre c id: " + id + " не найден");
        }
    }

    public void addOrUpdateBookGenres(Book book) {
        int id = book.getId();

        if (book.getGenres() != null) {
            String sqlGenre = "DELETE " +
                    "FROM book_genres " +
                    "WHERE id_book = ? ";
            jdbcTemplate.update(sqlGenre, book.getId());

            for (Genre genre : book.getGenres()) {
                sqlGenre = "MERGE INTO book_genres(id_book, id_genre) " +
                        "VALUES (?, ?)";

                jdbcTemplate.update(sqlGenre, id, genre.getId());
            }
        } else {
            String sqlGenre = "DELETE " +
                    "FROM book_genres " +
                    "WHERE id_book = ? ";

            jdbcTemplate.update(sqlGenre, book.getId());
        }
    }

    public List<Genre> getGenresByIdBook(int id) {
        String sqlQuery = "SELECT * " +
                "FROM genres " +
                "WHERE id IN" +
                "(" +
                "SELECT id_genre " +
                "FROM book_genres " +
                "WHERE id_book = ?" +
                ")";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
    }
    private Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
