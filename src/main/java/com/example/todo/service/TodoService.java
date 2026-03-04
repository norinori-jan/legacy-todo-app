package com.example.todo.service;

import com.example.todo.dto.*;
import com.example.todo.entity.TodoStatus;

import java.util.List;

/**
 * Service interface defining all business operations for Todo items.
 */
public interface TodoService {

    /**
     * Retrieves a paginated list of non-deleted todos.
     *
     * @param page zero-based page index
     * @param size number of items per page
     * @return paginated todos
     */
    ApiResponse<PageResponse<TodoResponse>> getAllTodos(int page, int size);

    /**
     * Retrieves a single todo by its ID.
     *
     * @param id the todo ID
     * @return todo response
     */
    ApiResponse<TodoResponse> getTodoById(Long id);

    /**
     * Creates a new todo item.
     *
     * @param request the create request
     * @return created todo response
     */
    ApiResponse<TodoResponse> createTodo(TodoRequest request);

    /**
     * Updates an existing todo item.
     *
     * @param id      the todo ID
     * @param request the update request
     * @return updated todo response
     */
    ApiResponse<TodoResponse> updateTodo(Long id, TodoRequest request);

    /**
     * Soft-deletes a todo item.
     *
     * @param id the todo ID
     * @return void response
     */
    ApiResponse<Void> deleteTodo(Long id);

    /**
     * Restores a soft-deleted todo item.
     *
     * @param id the todo ID
     * @return restored todo response
     */
    ApiResponse<TodoResponse> restoreTodo(Long id);

    /**
     * Retrieves todos filtered by status.
     *
     * @param status the desired status
     * @return matching todos
     */
    ApiResponse<List<TodoResponse>> getTodosByStatus(TodoStatus status);

    /**
     * Searches todos by keyword in title.
     *
     * @param keyword the search term
     * @return matching todos
     */
    ApiResponse<List<TodoResponse>> searchTodos(String keyword);

    /**
     * Retrieves non-deleted todos ordered by priority descending.
     *
     * @return todos ordered by priority
     */
    ApiResponse<List<TodoResponse>> getTodosByPriority();

    /**
     * Returns statistics counts for all todo statuses.
     *
     * @return statistics response
     */
    ApiResponse<TodoStatisticsResponse> getStatistics();
}
