package com.example.todo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA entity representing a Todo item stored in the todos table.
 */
@Entity
@Table(name = "todos")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    /** Primary key, auto-incremented. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Title of the todo item (required, max 255 chars). */
    @Column(nullable = false, length = 255)
    private String title;

    /** Optional detailed description. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Current status of the todo item. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TodoStatus status = TodoStatus.PENDING;

    /** Priority level; higher value = higher priority. */
    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    /** Optional due date for the todo item. */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /** Timestamp when the record was created (auto-set by auditing). */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the record was last updated (auto-set by auditing). */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Timestamp when the record was soft-deleted; null if not deleted. */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Returns true if this todo has been soft-deleted.
     *
     * @return true if deletedAt is set
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
