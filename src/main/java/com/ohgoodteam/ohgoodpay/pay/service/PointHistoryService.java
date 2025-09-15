package com.ohgoodteam.ohgoodpay.pay.service;

import java.util.List;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PointHistoryEntity;
import com.ohgoodteam.ohgoodpay.pay.dto.PointHistoryDTO;

public interface PointHistoryService {

    // 포인트 내역 조회
    List<PointHistoryDTO> getPointHistory(Long customerId);

    // entity to dto
    default PointHistoryDTO entityToDto(PointHistoryEntity pointHistoryEntity) {
        return PointHistoryDTO.builder()
            .pointHistoryId(pointHistoryEntity.getPointHistoryId())
            .customerId(pointHistoryEntity.getCustomer().getCustomerId())
            .point(pointHistoryEntity.getPoint())
            .pointExplain(pointHistoryEntity.getPointExplain())
            .date(pointHistoryEntity.getDate())
            .build();
    }

    // dto to entity
    default PointHistoryEntity dtoToEntity(PointHistoryDTO pointHistoryDTO) {
        return PointHistoryEntity.builder()
            .pointHistoryId(pointHistoryDTO.getPointHistoryId())
            .customer(CustomerEntity.builder().customerId(pointHistoryDTO.getCustomerId()).build())
            .point(pointHistoryDTO.getPoint())
            .pointExplain(pointHistoryDTO.getPointExplain())
            .date(pointHistoryDTO.getDate())
            .build();
    }
}
