package com.example.todo.dto;

import com.example.todo.entity.TodoStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating a Todo item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {

    /** Title of the todo (required). */
    @NotBlank(message = "Title must not be blank")
    private String title;

    /** Optional description of the todo. */
    private String description;

    /** Status of the todo; defaults to PENDING. */
    @Builder.Default
    private TodoStatus status = TodoStatus.PENDING;

    /** Priority level; defaults to 0. */
    @Builder.Default
    private Integer priority = 0;

    /** Optional due date. */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
}
