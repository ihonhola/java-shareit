package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, Integer> emailToIdMap = new HashMap<>(); //для проверки уникальности email

    public User save(User user) {

        if (emailToIdMap.containsKey(user.getEmail())) {
           throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        emailToIdMap.put(user.getEmail(), user.getId());
        return user;
    }

    public User update(User user) {
        Integer userId = user.getId();

        if (userId == null || !users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User existingUser = users.get(userId);
        String newEmail = user.getEmail();

        if (newEmail != null && !newEmail.isBlank()) {
            String oldEmail = existingUser.getEmail();

            // Если email изменился
            if (!newEmail.equals(oldEmail)) {
                // Проверяем уникальность
                if (emailToIdMap.containsKey(newEmail)) {
                    throw new ConflictException("Пользователь с email " + newEmail + " уже существует");
                }

                // Обновляем
                emailToIdMap.remove(oldEmail);
                emailToIdMap.put(newEmail, userId);
                existingUser.setEmail(newEmail);
            }
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

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
        User user = users.get(id);
        if (user != null) {
            emailToIdMap.remove(user.getEmail());
            users.remove(id);
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
