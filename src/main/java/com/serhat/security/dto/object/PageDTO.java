package com.serhat.security.dto.object;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

public record PageDTO<T> (
        List<T> content,
        int page,
        int size,
        int totalElements
) implements Serializable{
    public PageDTO {
    }

    public static <T> PageDTO<T> from(Page<T> page) {
        return new PageDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                (int) page.getTotalElements()
        );
    }
}