package com.code.share.codesharing.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultPage<T> {
    private List<T> data;
    private int totalCount;
    private int pageSize;
    private int pageStart;
}
