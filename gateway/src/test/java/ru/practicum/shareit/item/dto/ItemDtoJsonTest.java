package ru.practicum.shareit.item.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 3, 1, 12, 0, 0);

        // Создаем вложенные DTO
        BookingResponseDto lastBooking = new BookingResponseDto();
        lastBooking.setId(10);
        lastBooking.setStart(now.minusDays(2));
        lastBooking.setEnd(now.minusDays(1));
        lastBooking.setStatus("APPROVED");

        BookingResponseDto nextBooking = new BookingResponseDto();
        nextBooking.setId(11);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(2));
        nextBooking.setStatus("WAITING");

        CommentResponseDto comment = new CommentResponseDto();
        comment.setId(100);
        comment.setText("Great item!");
        comment.setAuthorName("John");
        comment.setCreated(now);

        ItemDto dto = new ItemDto();
        dto.setId(1);
        dto.setName("Гиперболоид");
        dto.setDescription("Принадлежал инженеру Гарину");
        dto.setAvailable(true);
        dto.setRequestId(5);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(List.of(comment));

        JsonContent<ItemDto> content = json.write(dto);

        // Проверяем основные поля
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("Гиперболоид");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Принадлежал инженеру Гарину");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);

        // Проверяем lastBooking
        assertThat(content).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(10);
        assertThat(content).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2026-02-27T12:00:00");
        assertThat(content).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2026-02-28T12:00:00");
        assertThat(content).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("APPROVED");

        // Проверяем nextBooking
        assertThat(content).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(11);
        assertThat(content).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2026-03-02T12:00:00");
        assertThat(content).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2026-03-03T12:00:00");
        assertThat(content).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("WAITING");

        // Проверяем комментарий
        assertThat(content).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(content).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(100);
        assertThat(content).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Great item!");
        assertThat(content).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("John");
        assertThat(content).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2026-03-01T12:00:00");
    }

    @Test
    void testDeserialization() throws Exception {
        String content = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Молот\",\n" +
                "    \"description\": \"Принадлежал инженеру Тору\",\n" +
                "    \"available\": true,\n" +
                "    \"requestId\": 7,\n" +
                "    \"lastBooking\": {\n" +
                "        \"id\": 20,\n" +
                "        \"start\": \"2026-03-01T10:00:00\",\n" +
                "        \"end\": \"2026-03-02T12:00:00\",\n" +
                "        \"status\": \"APPROVED\"\n" +
                "    },\n" +
                "    \"nextBooking\": null,\n" +
                "    \"comments\": [\n" +
                "        {\n" +
                "            \"id\": 101,\n" +
                "            \"text\": \"Nice\",\n" +
                "            \"authorName\": \"Jane\",\n" +
                "            \"created\": \"2026-03-01T15:30:00\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ItemDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("Молот");
        assertThat(dto.getDescription()).isEqualTo("Принадлежал инженеру Тору");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(7);
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(20);
        assertThat(dto.getLastBooking().getStart()).isEqualTo("2026-03-01T10:00:00");
        assertThat(dto.getLastBooking().getEnd()).isEqualTo("2026-03-02T12:00:00");
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getId()).isEqualTo(101);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Nice");
    }

    @Test
    void validation_shouldFailWhenRequiredFieldsMissing() {
        ItemDto dto = new ItemDto(); // все поля null
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        // Должны быть нарушения для name, description, available
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("available"));
    }

    @Test
    void validation_shouldFailWhenNameBlank() {
        ItemDto dto = new ItemDto();
        dto.setName("");
        dto.setDescription("Desc");
        dto.setAvailable(true);
        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void validation_shouldPassWhenAllRequiredFieldsPresent() {
        ItemDto dto = new ItemDto();
        dto.setName("Гиперболоид");
        dto.setDescription("Принадлежал инженеру Гарину");
        dto.setAvailable(true);
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}