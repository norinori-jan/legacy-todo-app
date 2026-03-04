package com.example.todo.controller;

import com.example.todo.dto.*;
import com.example.todo.entity.TodoStatus;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing Todo CRUD and query endpoints under /api/todos.
 */
@RestController
@RequestMapping("/api/todos")
@Slf4j
@Validated
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * Returns a paginated list of non-deleted todos.
     *
     * @param page zero-based page index (default 0)
     * @param size items per page (default 20)
     * @return 200 with paginated todo list
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TodoResponse>>> getAllTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/todos page={} size={}", page, size);
        return ResponseEntity.ok(todoService.getAllTodos(page, size));
    }

    /**
     * Returns a single todo by ID.
     *
     * @param id the todo ID
     * @return 200 with the todo, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodoById(@PathVariable Long id) {
        log.info("GET /api/todos/{}", id);
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    /**
     * Creates a new todo item.
     *
     * @param request validated create request
     * @return 201 with created todo
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(@Valid @RequestBody TodoRequest request) {
        log.info("POST /api/todos title='{}'", request.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(request));
    }

    /**
     * Updates an existing todo item.
     *
     * @param id      the todo ID
     * @param request validated update request
     * @return 200 with updated todo
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        log.info("PUT /api/todos/{}", id);
        return ResponseEntity.ok(todoService.updateTodo(id, request));
    }

    /**
     * Soft-deletes a todo item.
     *
     * @param id the todo ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(@PathVariable Long id) {
        log.info("DELETE /api/todos/{}", id);
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restores a soft-deleted todo item.
     *
     * @param id the todo ID
     * @return 200 with restored todo
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<TodoResponse>> restoreTodo(@PathVariable Long id) {
        log.info("POST /api/todos/{}/restore", id);
        return ResponseEntity.ok(todoService.restoreTodo(id));
    }

    /**
     * Returns todos filtered by status.
     *
     * @param status the desired status
     * @return 200 with matching todos
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getTodosByStatus(@PathVariable TodoStatus status) {
        log.info("GET /api/todos/status/{}", status);
        return ResponseEntity.ok(todoService.getTodosByStatus(status));
    }

    /**
     * Searches todos by keyword in title.
     *
     * @param keyword the search term
     * @return 200 with matching todos
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> searchTodos(@RequestParam String keyword) {
        log.info("GET /api/todos/search?keyword={}", keyword);
        return ResponseEntity.ok(todoService.searchTodos(keyword));
    }

    /**
     * Returns todos ordered by priority descending.
     *
     * @return 200 with todos sorted by priority
     */
    @GetMapping("/by-priority")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getTodosByPriority() {
        log.info("GET /api/todos/by-priority");
        return ResponseEntity.ok(todoService.getTodosByPriority());
    }

    /**
     * Returns statistics for all todo statuses.
     *
     * @return 200 with statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<TodoStatisticsResponse>> getStatistics() {
        log.info("GET /api/todos/statistics");
        return ResponseEntity.ok(todoService.getStatistics());
    }
}
