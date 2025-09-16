package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentViewDTO;
import com.ohgoodteam.ohgoodpay.pay.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PayThisMonthService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private final PaymentRepository payRepo;
    private final CustomerRepository customerRepo;

    @Transactional(readOnly = true)
    public Map<String, Object> getThisMonth(Long customerId) {
        LocalDate today = LocalDate.now(KST);
        LocalDateTime from = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime toExclusive = today.plusDays(1).atStartOfDay();

        List<PaymentViewDTO> rows = payRepo.findRecentByCustomer(customerId, from, toExclusive);

        // record는 타입의 DTO는 필드명으로 접근해야 함
        long sumTotalPrice = rows.stream()
                .map(PaymentViewDTO::totalPrice)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();

        long sumPointThisMonth = rows.stream()
                .map(PaymentViewDTO::point)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
        int currentPointBalance = customerRepo.findById(customerId)
                .map(CustomerEntity::getPoint)
                .orElse(0);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("customerId", customerId);
        resp.put("month", today.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        resp.put("from", from);
        resp.put("to", toExclusive.minusNanos(1));
        resp.put("count", rows.size());
        resp.put("sumTotalPrice", sumTotalPrice);
        resp.put("items", rows);
        resp.put("sumPointThisMonth", sumPointThisMonth); // <- 이번 달 포인트 합계
        resp.put("currentPointBalance", currentPointBalance); // <- 현재 보유 포인트
        return resp;
    }
}
