package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated
@Repository
public class InMemoryUserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, Integer> emailToIdMap = new HashMap<>(); //для проверки уникальности email

    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        emailToIdMap.put(user.getEmail(), user.getId());
        return user;
    }

    public User update(User user) {
        Integer userId = user.getId();

        User existingUser = users.get(userId);

        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());

        users.put(userId, existingUser);
        return existingUser;
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteById(Integer id) {
        User removedUser = users.remove(id);
        if (removedUser != null) {
            emailToIdMap.remove(removedUser.getEmail());
        }
    }

    public boolean existsById(Integer id) {
        return users.containsKey(id);
    }

    public boolean existsByEmail(String email) {
        return emailToIdMap.containsKey(email);
    }

    public Optional<User> findByEmail(String email) {
        Integer userId = emailToIdMap.get(email);
        return Optional.ofNullable(users.get(userId));
    }

    private int getNextId() {
        return users.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
