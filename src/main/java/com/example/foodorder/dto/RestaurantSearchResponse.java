package com.example.foodorder.dto;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantSearchResponse {
    private List<RestaurantResponse> data;
    private PaginationInfo pagination;

    @Data
    public static class PaginationInfo {
        private long total;
        private int page;
        private int pages;
    }
}
