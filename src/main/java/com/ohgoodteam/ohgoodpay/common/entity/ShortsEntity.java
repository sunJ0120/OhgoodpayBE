package com.ohgoodteam.ohgoodpay.common.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "shorts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
@ToString
public class ShortsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shortsId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Column(nullable = false)
    private String shortsName;

    @Column(nullable = false)
    private String videoName;

    @Column(nullable = false)
    private String shortsExplain;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private long likeCount;

    @Column(nullable = false)
    private long commentCount;

    @Transient
    @Setter
    private String myReaction; // transient 필드 추가

}