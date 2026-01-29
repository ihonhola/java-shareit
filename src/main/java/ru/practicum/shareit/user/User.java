package ru.practicum.shareit.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 256)
    @NotBlank(message = "Логин не может быть пустым")
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 512)
    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
}
