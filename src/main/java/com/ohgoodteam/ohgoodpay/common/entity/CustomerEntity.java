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
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String emailId;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false)
    private String birth;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private String accountName;

    private int point;
    private int balance;

    private boolean isBlocked;

    private String profileImg;

    private String nickname;

    private String introduce;

    private int score;

    private String hobby;

    private boolean isExtension;

    private boolean isAuto;

    private int gradePoint;

    private int blockedCnt;

    private int extensionCnt;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grade_name")
    @ToString.Exclude
    private GradeEntity grade;

    /** 포인트 차감 */
    public void decreasePoint(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        if (this.point < amount) throw new IllegalArgumentException("포인트가 부족합니다.");
        this.point -= amount;
    }

    /** 포인트 적립 */
    public void addPoint(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        this.point += amount;
    }

    /** 잔액 차감 */
    public void decreaseBalance(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        if (this.balance < amount) throw new IllegalArgumentException("잔액이 부족합니다.");
        this.balance -= amount;
    }
  
    private int balance;

}
