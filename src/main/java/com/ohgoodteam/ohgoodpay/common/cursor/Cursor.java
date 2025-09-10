package com.ohgoodteam.ohgoodpay.common.cursor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cursor {
    private Long lastId;
    private Double lastScore;
    private LocalDateTime lastDate;
}
