package com.vps.vpsserver.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean first;
    private boolean last;

    public static <T> PageResponseDTO<T> from(Page<T> page) {
        return new PageResponseDTO<>(
            page.getContent(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.getSize(),
            page.getNumber(),
            page.hasNext(),
            page.hasPrevious(),
            page.isFirst(),
            page.isLast()
        );
    }
}