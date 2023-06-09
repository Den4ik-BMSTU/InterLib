package ru.internetionalLibrary.storage.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.internetionalLibrary.exceptions.ValidationException;
import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.models.User;
import ru.internetionalLibrary.storage.interf.UserStorage;
import ru.internetionalLibrary.services.GenreService;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
@Slf4j
@Primary
public class DaoUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private final GenreService genreService;
    public DaoUserStorage(JdbcTemplate jdbcTemplate, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
    }

    @Override
    public User getUserById(Integer userId) {
        try{
            String sqlQuery = "SELECT id, name, email, login, birthday " +
                    "FROM users " +
                    "WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUsers, userId);
        } catch (Exception e) {
            log.info("Пользователь c id - {} не содержится в базе", userId);
            throw new ValidationException("Пользователь c id - " + userId + " не содержится в базе");
        }
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "INSERT INTO users(name, email, login, birthday)" +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            checkNullNameAndSetName(ps, user);
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        return getUserById(id);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET " +
                "name = ?, email = ?, login = ?, birthday = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getEmail()
                , user.getLogin()
                , user.getBirthday()
                , user.getId());
        return getUserById(user.getId());
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * " +
                "FROM users";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers);
    }

    @Override
    public void removeUser (Integer id) {
        String sqlQuery = "DELETE " +
                "FROM users " +
                "WHERE id = ?";

        log.info("Удален пользователь под id: {}", id);
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {

        String sqlQueryInsertFriend = "MERGE INTO users_friends(id_user_one, id_user_two)" +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQueryInsertFriend, userId, friendId);
        log.info("У пользоателя с id: {} добавлен новый друг с id: {} ", userId, friendId);
        return getUserById(userId);
    }

    @Override
    public Set<User> getFriendsById(Integer userId) {
        getUserById(userId);

        String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE id IN" +
                "(" +
                "SELECT id_user_two " +
                "FROM users_friends " +
                "WHERE id_user_one = ?" +
                ")";


        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToUsers, userId));
    }

    @Override
    public User removeFriend(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE " +
                "FROM users_friends " +
                "WHERE id_user_one = ? AND id_user_two = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("У пользователя с id: {} удален друг с id: {} ", userId, friendId);
        return getUserById(userId);
    }

    @Override
    public Set<User> getCommonFriends(Integer userId, Integer otherId) {
        String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE id IN" +
                "(" +
                "SELECT DISTINCT uf1.id_user_two " +
                "FROM users_friends AS uf1, users_friends AS uf2 " +
                "WHERE uf1.id_user_two = uf2.id_user_two " +
                "AND uf1.id_user_one = ? " +
                "AND uf2.id_user_one = ? " +
                ")";

        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToUsers, userId, otherId));
    }

    private void checkNullNameAndSetName(PreparedStatement ps, User user) throws SQLException {
        if (user.getName() == null || user.getName().isBlank()) {
            ps.setString(1, user.getLogin());
        } else {
            ps.setString(1, user.getName());
        }
    }

    private List<Integer> getUserFriendsIds(Integer userId) {
        String sqlQuery = "SELECT id_user_two " +
                "FROM users_friends " +
                "WHERE id_user_one = ?";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }

    public List<Book> getRecommendations(Integer userId) {
        return jdbcTemplate.query(
                "SELECT * FROM BOOKS book " +
                        "WHERE book.ID IN " +
                            "(SELECT likes.ID_BOOK  FROM LIKES likes " +
                                "WHERE likes.ID_USER IN " +
                                    "(SELECT ID_USER FROM LIKES l " +
                                        "WHERE l.ID_USER != ? " +
                                            "AND " +
                                                "l.ID_FILM IN " +
                                                    "(SELECT l.ID_BOOK FROM LIKES l " +
                                                        "WHERE l.ID_USER = ?)" +
                                    ") " +
                            "GROUP BY likes.ID_BOOK " +
                            "HAVING likes.ID_BOOK NOT IN " +
                                "(SELECT l.ID_BOOK FROM LIKES l WHERE l.ID_USER = ?)" +
                            ")",
                this::mapRowToBooks,
                userId, userId, userId);
    }

    private Book mapRowToBooks(ResultSet resultSet, int i) throws SQLException {
        return Book.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .volume(resultSet.getInt("volume"))
                .likes(new HashSet<>(new DaoBookStorage(jdbcTemplate).getLikesFromUserByBookId(resultSet.getInt("id"))))
                .rate(resultSet.getInt("rate"))
                .genres(genreService.getGenresByIdBook(resultSet.getInt("id")))
                .build();
    }

    private User mapRowToUsers(ResultSet resultSet, int i) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(new HashSet<>(getUserFriendsIds(resultSet.getInt("id"))))
                .build();
    }
}
