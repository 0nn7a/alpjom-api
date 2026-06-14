package com.ternura.model.vo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HeatmapVO {
    private LocalDate date;   // YYYY-MM-DD
    private Integer count;
}
