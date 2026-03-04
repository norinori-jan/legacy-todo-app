package com.example.todo.dto;

import lombok.*;

/**
 * Statistics summary for Todo items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatisticsResponse {

    /** Total number of todos (including deleted). */
    private long total;

    /** Number of todos with PENDING status. */
    private long pending;

    /** Number of todos with IN_PROGRESS status. */
    private long inProgress;

    /** Number of todos with COMPLETED status. */
    private long completed;

    /** Number of soft-deleted todos. */
    private long deleted;
}
