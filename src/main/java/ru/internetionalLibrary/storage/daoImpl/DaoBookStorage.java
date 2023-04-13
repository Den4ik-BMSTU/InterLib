package ru.internetionalLibrary.storage.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.internetionalLibrary.exceptions.ValidationException;
import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.services.AuthorService;
import ru.internetionalLibrary.services.GenreService;
import ru.internetionalLibrary.storage.interf.BookStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
@Slf4j
@Primary
public class DaoBookStorage implements BookStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;
    private final AuthorService authorService;

    public DaoBookStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = new GenreService(new DaoGenreStorage(jdbcTemplate));
        this.authorService = new AuthorService(new DaoAuthorStorage(jdbcTemplate));
    }

    @Override
    public List<Book> getBooks() {
        String sqlQuery = "SELECT * " +
                "FROM books";

        return jdbcTemplate.query(sqlQuery, this::mapRowToBooks);
    }

    @Override
    public Book getBookById(Integer bookId) {
        try {
            String sqlQuery = "SELECT id, name, description, release_date, volume " +
                    "FROM books " +
                    "WHERE id = ?";

            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToBooks, bookId);
        } catch (Exception e) {
            log.info("Книга c id {} не содержится в базе ", bookId);
            throw new ValidationException("Книга c id: " + bookId + " не содержится в базе");
        }
    }

    @Override
    public Book addBook(Book book) {
        String sqlQuery = "INSERT INTO books(name, description, release_date, volume)" +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getName());
            ps.setString(2, book.getDescription());
            ps.setDate(3, Date.valueOf(book.getReleaseDate()));
            ps.setInt(4, book.getVolume());
            return ps;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        book.setId(id);

        genreService.addOrUpdateFilmGenres(book);
        authorService.addOrUpdateBookAuthors(book);

        return getBookById(id);
    }

    @Override
    public Book updateBook(Book book) {
        getBookById(book.getId());
        String sqlQuery = "UPDATE books SET " +
                "name = ?, description = ?, release_date = ?, volume = ?, rate = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery
                , book.getName()
                , book.getDescription()
                , book.getReleaseDate()
                , book.getVolume()
                , book.getRate()
                , book.getId()
                , book.getId());

        genreService.addOrUpdateFilmGenres(book);
        authorService.addOrUpdateBookAuthors(book);

        return getBookById(book.getId());
    }

    @Override
    public void removeBook(Integer id) {

        //удаляем жанры в связанной таблице book_genres
        String sqlQueryGenre = "DELETE " +
                "FROM book_genres " +
                "WHERE id_book = ? ";

        jdbcTemplate.update(sqlQueryGenre, id);

        //удаляем режиссеров в связанной таблице book_authors
        String sqlQueryAuthor = "DELETE " +
                "FROM BOOK_AUTHOR " +
                "WHERE ID_BOOK = ? ";

        jdbcTemplate.update(sqlQueryAuthor, id);

        String sqlQuery = "DELETE " +
                "FROM books " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Book addLikeFromUserById(Integer bookId, Integer userId) {
        String sqlQuery = "INSERT INTO likes(id_user, id_book) " +
                "VALUES(?, ?)";

        jdbcTemplate.update(sqlQuery, userId, bookId);
        return getBookById(bookId);
    }

    @Override
    public Book removeLikeFromUserById(Integer bookId, Integer userId) {
        String sqlQuery = "DELETE " +
                "FROM likes " +
                "WHERE id_user = ? AND id_books = ? ";

        jdbcTemplate.update(sqlQuery, userId, bookId);
        return getBookById(bookId);
    }



    @Override
    public List<Book> findCommon(int userId, int friendsId){
        String sqlQuery = " SELECT books.* " +
                "FROM books " +
                "WHERE books.id IN (SELECT DISTINCT id_book FROM likes WHERE id_user = ? AND ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToBooks, userId, friendsId);
    }



    private Book mapRowToBooks(ResultSet resultSet, int i) throws SQLException {
        return Book.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .volume(resultSet.getInt("volume"))
                .likes(new HashSet<>(getLikesFromUserByBookId(resultSet.getInt("id"))))
                .rate(resultSet.getInt("rate"))
                .genres(genreService.getGenresByIdBook(resultSet.getInt("id")))
                .authors(authorService.getAuthorsByIdBook(resultSet.getInt("id")))
                .build();
    }
    public List<Integer> getLikesFromUserByBookId(int id) {
        String sqlQuery = "SELECT id " +
                "FROM users " +
                "WHERE id IN" +
                "( " +
                "SELECT id_user " +
                "FROM likes " +
                "WHERE id_book IN" +
                "(" +
                "SELECT id_book " +
                "FROM books " +
                "WHERE id_book = ?" +
                ")" +
                ")";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class, id);
    }


    private String getInsertString(String substring, String by) throws IllegalArgumentException {
        substring = substring.toLowerCase(Locale.ROOT);
        switch (by) {
            case "author":
                return "(LOWER(d.name) LIKE '%" + substring + "%')";
            case "title":
                return "(LOWER(f.name) LIKE '%" + substring + "%')";
            case "author,title":
            case "title,author":
                return "(LOWER(d.name) LIKE '%" + substring + "%') OR (LOWER(f.name) LIKE '%" + substring + "%')";
            default:
                throw new IllegalArgumentException("Wrong request param.");
        }
    }

    @Override
    public List<Book> searchBooks(String substring, String by) throws IllegalArgumentException {
        String sql = "SELECT *" +
                    "FROM books AS f " +
                    "LEFT OUTER JOIN book_authors AS fd ON f.id = fd.id_book " +
                    "LEFT OUTER JOIN authors AS d ON fd.id_author = d.id " +
                    "LEFT JOIN likes AS l ON f.id = l.id_book " +
                    "WHERE " + getInsertString(substring, by) + " " +
                    "GROUP BY f.id, l.id_user " +
                    "ORDER BY COUNT(l.id_user) DESC;";
        Set<Book> books = new HashSet<>(jdbcTemplate.query(sql, this::mapRowToBooks));
        List<Book> result = new ArrayList<>(books);
        result.sort(Comparator.comparingInt(book -> book.getLikes().size()));
        Collections.reverse(result);
        return result;
    }
}