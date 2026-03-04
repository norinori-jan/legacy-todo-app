package com.example.todo.service;

import com.example.todo.dto.*;
import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoStatus;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TodoServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoServiceImpl(todoRepository);
    }

    private Todo buildTodo(Long id) {
        return Todo.builder()
                .id(id)
                .title("Test Todo")
                .status(TodoStatus.PENDING)
                .priority(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllTodos_returnsPaginatedResult() {
        Todo todo = buildTodo(1L);
        Page<Todo> page = new PageImpl<>(List.of(todo), PageRequest.of(0, 20), 1);
        when(todoRepository.findByDeletedAtIsNull(any(Pageable.class))).thenReturn(page);

        ApiResponse<PageResponse<TodoResponse>> response = todoService.getAllTodos(0, 20);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getContent()).hasSize(1);
    }

    @Test
    void getTodoById_found_returnsResponse() {
        Todo todo = buildTodo(1L);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        ApiResponse<TodoResponse> response = todoService.getTodoById(1L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getId()).isEqualTo(1L);
    }

    @Test
    void getTodoById_notFound_throwsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodoById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getTodoById_deleted_throwsException() {
        Todo todo = buildTodo(1L);
        todo.setDeletedAt(LocalDateTime.now());
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        assertThatThrownBy(() -> todoService.getTodoById(1L))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void createTodo_savesAndReturnsResponse() {
        TodoRequest request = TodoRequest.builder().title("New").build();
        Todo saved = buildTodo(1L);
        saved.setTitle("New");
        when(todoRepository.save(any(Todo.class))).thenReturn(saved);

        ApiResponse<TodoResponse> response = todoService.createTodo(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getTitle()).isEqualTo("New");
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void updateTodo_updatesFields() {
        Todo todo = buildTodo(1L);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoRequest request = TodoRequest.builder().title("Updated").status(TodoStatus.IN_PROGRESS).priority(5).build();
        ApiResponse<TodoResponse> response = todoService.updateTodo(1L, request);

        assertThat(response.isSuccess()).isTrue();
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void deleteTodo_softDeletes() {
        Todo todo = buildTodo(1L);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        ApiResponse<Void> response = todoService.deleteTodo(1L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(todo.getDeletedAt()).isNotNull();
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.DELETED);
    }

    @Test
    void deleteTodo_notFound_throwsException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.deleteTodo(99L))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void restoreTodo_restoresDeletedTodo() {
        Todo todo = buildTodo(1L);
        todo.setDeletedAt(LocalDateTime.now());
        todo.setStatus(TodoStatus.DELETED);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        ApiResponse<TodoResponse> response = todoService.restoreTodo(1L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(todo.getDeletedAt()).isNull();
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    void restoreTodo_notDeleted_throwsException() {
        Todo todo = buildTodo(1L);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        assertThatThrownBy(() -> todoService.restoreTodo(1L))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void getTodosByStatus_returnsFilteredList() {
        when(todoRepository.findByStatusAndDeletedAtIsNull(TodoStatus.PENDING))
                .thenReturn(List.of(buildTodo(1L)));

        ApiResponse<List<TodoResponse>> response = todoService.getTodosByStatus(TodoStatus.PENDING);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void searchTodos_returnsMatchingTodos() {
        when(todoRepository.findByTitleContainingIgnoreCaseAndDeletedAtIsNull("test"))
                .thenReturn(List.of(buildTodo(1L)));

        ApiResponse<List<TodoResponse>> response = todoService.searchTodos("test");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void getTodosByPriority_returnsOrderedList() {
        when(todoRepository.findByDeletedAtIsNullOrderByPriorityDesc())
                .thenReturn(List.of(buildTodo(1L)));

        ApiResponse<List<TodoResponse>> response = todoService.getTodosByPriority();

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void getStatistics_returnsCounts() {
        when(todoRepository.count()).thenReturn(10L);
        when(todoRepository.countByStatus(TodoStatus.PENDING)).thenReturn(3L);
        when(todoRepository.countByStatus(TodoStatus.IN_PROGRESS)).thenReturn(2L);
        when(todoRepository.countByStatus(TodoStatus.COMPLETED)).thenReturn(4L);
        when(todoRepository.countByDeletedAtIsNotNull()).thenReturn(1L);

        ApiResponse<TodoStatisticsResponse> response = todoService.getStatistics();

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getTotal()).isEqualTo(10L);
        assertThat(response.getData().getPending()).isEqualTo(3L);
        assertThat(response.getData().getDeleted()).isEqualTo(1L);
    }
}
