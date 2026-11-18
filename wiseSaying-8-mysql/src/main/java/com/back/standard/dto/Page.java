package com.back.standard.dto;

import java.util.List;

public record Page<T>(List<T> content, int totalCount, int page, int pageSize) {
    public int totalPages() {
        return Math.max(1, (int) Math.ceil((double) totalCount / pageSize));
    }
}
