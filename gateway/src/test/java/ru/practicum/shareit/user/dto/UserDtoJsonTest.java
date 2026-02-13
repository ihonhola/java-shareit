package ru.practicum.shareit.user.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testSerialization() throws Exception {
        UserDto dto = new UserDto(1, "John Doe", "john@example.com");
        JsonContent<UserDto> content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void testDeserialization() throws Exception {
        String content = """
                {
                    "id": 2,
                    "name": "Jane Smith",
                    "email": "jane@example.com"
                }
                """;

        UserDto dto = json.parseObject(content);
        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("Jane Smith");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void validation_shouldFailWhenNameBlank() {
        UserDto dto = new UserDto(null, "", "john@example.com");
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void validation_shouldFailWhenEmailBlank() {
        UserDto dto = new UserDto(null, "John", "");
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void validation_shouldFailWhenEmailInvalid() {
        UserDto dto = new UserDto(null, "John", "not-an-email");
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void validation_shouldPassWhenAllFieldsValid() {
        UserDto dto = new UserDto(null, "John", "john@example.com");
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}