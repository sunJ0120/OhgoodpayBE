package com.ohgoodteam.ohgoodpay.common.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Getter
@Entity
@Table(name = "point_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class PointHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer; 

    @Column(nullable = false)
    private int point; 

    @Column(name = "point_explain", nullable = false)
    private String pointExplain; 

    @Column(name = "date", nullable = false)
    private LocalDateTime date; 
}
