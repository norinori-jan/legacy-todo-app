package com.example.todo.controller;

import com.example.todo.dto.*;
import com.example.todo.entity.TodoStatus;
import com.example.todo.exception.GlobalExceptionHandler;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link TodoController} using MockMvc.
 */
@WebMvcTest(controllers = {TodoController.class, GlobalExceptionHandler.class})
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private TodoResponse buildTodoResponse(Long id, String title) {
        return TodoResponse.builder()
                .id(id)
                .title(title)
                .status(TodoStatus.PENDING)
                .priority(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllTodos_returnsOk() throws Exception {
        PageResponse<TodoResponse> page = PageResponse.<TodoResponse>builder()
                .content(List.of(buildTodoResponse(1L, "Test")))
                .pageNumber(0).pageSize(20).totalElements(1).totalPages(1)
                .first(true).last(true).build();
        when(todoService.getAllTodos(0, 20)).thenReturn(ApiResponse.success("ok", page));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getTodoById_found_returnsOk() throws Exception {
        when(todoService.getTodoById(1L))
                .thenReturn(ApiResponse.success("ok", buildTodoResponse(1L, "Test")));

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getTodoById_notFound_returns404() throws Exception {
        when(todoService.getTodoById(99L)).thenThrow(new TodoNotFoundException("Todo not found with id: 99"));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createTodo_valid_returnsCreated() throws Exception {
        TodoRequest request = TodoRequest.builder().title("New Todo").build();
        when(todoService.createTodo(any(TodoRequest.class)))
                .thenReturn(ApiResponse.success("created", buildTodoResponse(1L, "New Todo")));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("New Todo"));
    }

    @Test
    void createTodo_blankTitle_returns400() throws Exception {
        TodoRequest request = TodoRequest.builder().title("").build();

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateTodo_valid_returnsOk() throws Exception {
        TodoRequest request = TodoRequest.builder().title("Updated").build();
        when(todoService.updateTodo(eq(1L), any(TodoRequest.class)))
                .thenReturn(ApiResponse.success("updated", buildTodoResponse(1L, "Updated")));

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated"));
    }

    @Test
    void deleteTodo_returnsNoContent() throws Exception {
        when(todoService.deleteTodo(1L)).thenReturn(ApiResponse.success("deleted", null));

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void restoreTodo_returnsOk() throws Exception {
        when(todoService.restoreTodo(1L))
                .thenReturn(ApiResponse.success("restored", buildTodoResponse(1L, "Test")));

        mockMvc.perform(post("/api/todos/1/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getTodosByStatus_returnsOk() throws Exception {
        when(todoService.getTodosByStatus(TodoStatus.PENDING))
                .thenReturn(ApiResponse.success("ok", List.of(buildTodoResponse(1L, "Test"))));

        mockMvc.perform(get("/api/todos/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    @Test
    void searchTodos_returnsOk() throws Exception {
        when(todoService.searchTodos("test"))
                .thenReturn(ApiResponse.success("ok", List.of(buildTodoResponse(1L, "Test Todo"))));

        mockMvc.perform(get("/api/todos/search").param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getTodosByPriority_returnsOk() throws Exception {
        when(todoService.getTodosByPriority())
                .thenReturn(ApiResponse.success("ok", List.of(buildTodoResponse(1L, "Test"))));

        mockMvc.perform(get("/api/todos/by-priority"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getStatistics_returnsOk() throws Exception {
        TodoStatisticsResponse stats = TodoStatisticsResponse.builder()
                .total(5).pending(2).inProgress(1).completed(1).deleted(1).build();
        when(todoService.getStatistics()).thenReturn(ApiResponse.success("ok", stats));

        mockMvc.perform(get("/api/todos/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(5));
    }
}
