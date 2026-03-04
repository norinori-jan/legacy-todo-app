package com.example.todo.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Pagination wrapper for list responses.
 *
 * @param <T> type of content items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    /**
     * Creates a {@link PageResponse} from a Spring Data {@link Page}.
     *
     * @param page the page result from repository
     * @param <T>  type of page content
     * @return populated PageResponse
     */
    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
