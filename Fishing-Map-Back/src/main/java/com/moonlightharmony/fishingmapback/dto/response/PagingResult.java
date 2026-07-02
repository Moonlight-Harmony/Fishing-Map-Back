package com.moonlightharmony.fishingmapback.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
// import java.util.function.Function; // Function 제거
// import java.util.stream.Collectors; // Collectors 제거

// ... (Result 상속 및 필드는 동일)

@Getter
@Setter
@NoArgsConstructor
public class PagingResult<T> extends Result {

    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private List<T> content;

    /**
     * Spring Data Page<T> (이미 DTO로 변환된) 객체를 받아 PagingResult 객체를 생성하는 생성자
     * @param page 이미 DTO(T)로 변환이 완료된 Spring Data Page<T> 객체
     */
    public PagingResult(Page<T> page) { // Page<T>를 직접 받습니다.
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();

        // 이미 DTO로 변환되었기 때문에 content만 가져옵니다.
        this.content = page.getContent();
    }
}