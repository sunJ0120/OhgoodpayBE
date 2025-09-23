package com.ohgoodteam.ohgoodpay.common.service;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PointHistoryEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.pay.repository.PointHistoryRepository;
import com.ohgoodteam.ohgoodpay.common.service.CheckInService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CustomerRepository customerRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    @Transactional
    public void saveCheckInPoint(int point, Long customerId) {
        
        // 오늘 이미 출석 체크 했는지 확인
        if (hasCheckedInToday(customerId)) {
            throw new IllegalStateException("오늘 이미 출석 체크 완료!");
        }

        // 포인트 적립
        customerRepository.plusCustomerPoint(point, customerId);

        // 고객 조회
        CustomerEntity customer = customerRepository.findByCustomerId(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));

        // 포인트 내역 저장
        PointHistoryEntity history = PointHistoryEntity.builder()
                .customer(customer)
                .point(point)
                .pointExplain("출석 체크")
                .date(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(history);
    }

    // 오늘 출석 체크 여부 확인
    @Override
    public boolean hasCheckedInToday(Long customerId) {
        return pointHistoryRepository.countByCustomerAndToday(customerId) > 0;
    }
}
