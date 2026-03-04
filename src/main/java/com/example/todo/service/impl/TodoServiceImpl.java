package com.example.todo.service.impl;

import com.example.todo.dto.*;
import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoStatus;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TodoService} providing full CRUD and query operations.
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<TodoResponse>> getAllTodos(int page, int size) {
        log.debug("Fetching page {} of todos (size={})", page, size);
        Page<TodoResponse> responsePage = todoRepository
                .findByDeletedAtIsNull(PageRequest.of(page, size))
                .map(TodoResponse::fromEntity);
        return ApiResponse.success("Todos retrieved successfully", PageResponse.fromPage(responsePage));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TodoResponse> getTodoById(Long id) {
        log.debug("Fetching todo with id={}", id);
        Todo todo = todoRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        return ApiResponse.success("Todo retrieved successfully", TodoResponse.fromEntity(todo));
    }

    /** {@inheritDoc} */
    @Override
    public ApiResponse<TodoResponse> createTodo(TodoRequest request) {
        log.debug("Creating todo with title='{}'", request.getTitle());
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TodoStatus.PENDING)
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .dueDate(request.getDueDate())
                .build();
        Todo saved = todoRepository.save(todo);
        return ApiResponse.success("Todo created successfully", TodoResponse.fromEntity(saved));
    }

    /** {@inheritDoc} */
    @Override
    public ApiResponse<TodoResponse> updateTodo(Long id, TodoRequest request) {
        log.debug("Updating todo with id={}", id);
        Todo todo = todoRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            todo.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        todo.setDueDate(request.getDueDate());
        Todo saved = todoRepository.save(todo);
        return ApiResponse.success("Todo updated successfully", TodoResponse.fromEntity(saved));
    }

    /** {@inheritDoc} */
    @Override
    public ApiResponse<Void> deleteTodo(Long id) {
        log.debug("Soft-deleting todo with id={}", id);
        Todo todo = todoRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        todo.setDeletedAt(LocalDateTime.now());
        todo.setStatus(TodoStatus.DELETED);
        todoRepository.save(todo);
        return ApiResponse.success("Todo deleted successfully", null);
    }

    /** {@inheritDoc} */
    @Override
    public ApiResponse<TodoResponse> restoreTodo(Long id) {
        log.debug("Restoring todo with id={}", id);
        Todo todo = todoRepository.findById(id)
                .filter(Todo::isDeleted)
                .orElseThrow(() -> new TodoNotFoundException("Deleted todo not found with id: " + id));
        todo.setDeletedAt(null);
        todo.setStatus(TodoStatus.PENDING);
        Todo saved = todoRepository.save(todo);
        return ApiResponse.success("Todo restored successfully", TodoResponse.fromEntity(saved));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TodoResponse>> getTodosByStatus(TodoStatus status) {
        log.debug("Fetching todos with status={}", status);
        List<TodoResponse> responses = todoRepository.findByStatusAndDeletedAtIsNull(status)
                .stream().map(TodoResponse::fromEntity).collect(Collectors.toList());
        return ApiResponse.success("Todos retrieved successfully", responses);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TodoResponse>> searchTodos(String keyword) {
        log.debug("Searching todos with keyword='{}'", keyword);
        List<TodoResponse> responses = todoRepository
                .findByTitleContainingIgnoreCaseAndDeletedAtIsNull(keyword)
                .stream().map(TodoResponse::fromEntity).collect(Collectors.toList());
        return ApiResponse.success("Search completed successfully", responses);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TodoResponse>> getTodosByPriority() {
        log.debug("Fetching todos ordered by priority");
        List<TodoResponse> responses = todoRepository.findByDeletedAtIsNullOrderByPriorityDesc()
                .stream().map(TodoResponse::fromEntity).collect(Collectors.toList());
        return ApiResponse.success("Todos retrieved successfully", responses);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TodoStatisticsResponse> getStatistics() {
        log.debug("Computing todo statistics");
        long pending = todoRepository.countByStatus(TodoStatus.PENDING);
        long inProgress = todoRepository.countByStatus(TodoStatus.IN_PROGRESS);
        long completed = todoRepository.countByStatus(TodoStatus.COMPLETED);
        long deleted = todoRepository.countByDeletedAtIsNotNull();
        long total = todoRepository.count();
        TodoStatisticsResponse stats = TodoStatisticsResponse.builder()
                .total(total)
                .pending(pending)
                .inProgress(inProgress)
                .completed(completed)
                .deleted(deleted)
                .build();
        return ApiResponse.success("Statistics retrieved successfully", stats);
    }
}
