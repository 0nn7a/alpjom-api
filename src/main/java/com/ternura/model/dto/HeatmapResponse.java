package com.ternura.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HeatmapResponse {
    private LocalDate date;
    private Integer count;
}
