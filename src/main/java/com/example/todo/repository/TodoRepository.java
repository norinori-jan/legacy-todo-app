package com.example.todo.repository;

import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Todo} entities.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * Find all non-deleted todos with the given status.
     */
    List<Todo> findByStatusAndDeletedAtIsNull(TodoStatus status);

    /**
     * Find all non-deleted todos with pagination.
     */
    Page<Todo> findByDeletedAtIsNull(Pageable pageable);

    /**
     * Search non-deleted todos by title (case-insensitive).
     */
    List<Todo> findByTitleContainingIgnoreCaseAndDeletedAtIsNull(String keyword);

    /**
     * Find all non-deleted todos ordered by priority descending.
     */
    List<Todo> findByDeletedAtIsNullOrderByPriorityDesc();

    /**
     * Count todos by status.
     */
    long countByStatus(TodoStatus status);

    /**
     * Count soft-deleted todos.
     */
    long countByDeletedAtIsNotNull();
}
