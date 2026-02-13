package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentMapperTest {

    @Test
    void toComment_shouldMapCorrectly() {
        CommentRequestDto request = new CommentRequestDto();
        request.setText("Great item!");

        Item item = new Item();
        item.setId(1);
        User author = new User();
        author.setId(2);

        Comment comment = CommentMapper.toComment(request, item, author);

        assertNotNull(comment);
        assertEquals("Great item!", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertNotNull(comment.getCreated());
        assertTrue(comment.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void toComment_shouldReturnNull_whenRequestNull() {
        assertNull(CommentMapper.toComment(null, new Item(), new User()));
    }

    @Test
    void toCommentDto_shouldMapCorrectly() {
        Comment comment = new Comment();
        comment.setId(10);
        comment.setText("Nice");
        User author = new User();
        author.setName("John");
        comment.setAuthor(author);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreated(now);

        CommentResponseDto dto = CommentMapper.toCommentDto(comment);

        assertNotNull(dto);
        assertEquals(10, dto.getId());
        assertEquals("Nice", dto.getText());
        assertEquals("John", dto.getAuthorName());
        assertEquals(now, dto.getCreated());
    }

    @Test
    void toCommentDto_shouldReturnNull_whenCommentNull() {
        assertNull(CommentMapper.toCommentDto(null));
    }
}