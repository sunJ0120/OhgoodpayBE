package com.ohgoodteam.ohgoodpay.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Entity
@Table(name = "grade")
@Builder
@Getter
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class GradeEntity {
    @Id
    private Long gradeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int limit;

    @Column(nullable = false)
    private float pointPercent;
}
