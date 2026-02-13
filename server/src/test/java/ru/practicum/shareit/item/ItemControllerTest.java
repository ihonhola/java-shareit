package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturn201() throws Exception {
        ItemDto input = new ItemDto(null, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        ItemDto output = new ItemDto(1, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        when(itemService.createItem(any(), anyInt())).thenReturn(output);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateItem_shouldReturn200() throws Exception {
        ItemDto input = new ItemDto(null, "Updated", null, false, null,
                null, null, null);
        ItemDto output = new ItemDto(1, "Updated", "Desc", false, null,
                null, null, null);
        when(itemService.updateItem(eq(1), any(), eq(1))).thenReturn(output);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void getItem_shouldReturn200() throws Exception {
        ItemDto output = new ItemDto(1, "Гиперболоид", "Принадлежал инженеру Гарину",
                true, null, null, null, null);
        when(itemService.getItemById(1, 1)).thenReturn(output);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllItemsByOwner_shouldReturn200() throws Exception {
        when(itemService.getAllItemsByOwner(1)).thenReturn(List.of(
                new ItemDto(1, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null)
        ));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void searchItems_shouldReturn200() throws Exception {
        when(itemService.searchAvailableItems("гиперболоид")).thenReturn(List.of(
                new ItemDto(1, "Гиперболоид", "Принадлежал инженеру Гарину",
                        true, null, null, null, null)
        ));

        mockMvc.perform(get("/items/search")
                        .param("text", "гиперболоид"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гиперболоид"));
    }

    @Test
    void addComment_shouldReturn201() throws Exception {
        CommentRequestDto commentRequest = new CommentRequestDto();
        commentRequest.setText("Great!");
        CommentResponseDto commentResponse = new CommentResponseDto();
        commentResponse.setId(1);
        commentResponse.setText("Great!");
        commentResponse.setAuthorName("User");
        commentResponse.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(1), any(), eq(1))).thenReturn(commentResponse);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}