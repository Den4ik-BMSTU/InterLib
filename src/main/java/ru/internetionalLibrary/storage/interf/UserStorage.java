package ru.internetionalLibrary.storage.interf;

import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.models.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);
    void removeUser(Integer id);
    User updateUser(User user);
    List<User> getUsers();
    User getUserById(Integer userId);

    User addFriend(Integer userId, Integer friendId);

    Set<User> getFriendsById(Integer userId);

    Set<User> getCommonFriends(Integer userId, Integer otherId);

    User removeFriend(Integer userId, Integer friendId);

    List<Book> getRecommendations(Integer id);
}
