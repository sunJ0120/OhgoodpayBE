package com.ohgoodteam.ohgoodpay.pay.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ohgoodteam.ohgoodpay.common.entity.PointHistoryEntity;
import com.ohgoodteam.ohgoodpay.pay.dto.PointHistoryDTO;
import com.ohgoodteam.ohgoodpay.pay.repository.PointHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    @Override
    public List<PointHistoryDTO> getPointHistory(Long customerId) {
        List<PointHistoryEntity> pointHistoryEntities = pointHistoryRepository.findByCustomerCustomerIdOrderByDateDesc(customerId);
        return pointHistoryEntities.stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }

}
