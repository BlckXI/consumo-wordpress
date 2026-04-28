package com.colibrihub.wordpress.dto;

import lombok.Data;

import java.util.List;
@Data
public class PagedResponse<T> {
    private List<T> content;    // Los datos de esta página
    private int page;           // Página actual (base 0)
    private int size;           // Elementos por página solicitados
    private long totalElements; // Total de elementos en TODAS las páginas
    private int totalPages;     // Total de páginas calculado
    private boolean first;      // ¿Es la primera página?
    private boolean last;       // ¿Es la última página?
    private boolean empty;      // ¿No hay resultados?

    // Constructor estático — crea el PagedResponse calculando automáticamente
    public static <T> PagedResponse<T> of(
            List<T> content, int page, int size, long totalElements
    ) {
        PagedResponse<T> r = new PagedResponse<>();
        r.content = content;
        r.page = page;
        r.size = size;
        r.totalElements = totalElements;
        r.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        r.first = page == 0;
        r.last = page >= r.totalPages - 1;
        r.empty = content == null || content.isEmpty();
        return r;
    }

}
