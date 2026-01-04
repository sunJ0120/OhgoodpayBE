package com.ohgoodteam.ohgoodpay.pay.service;

import com.ohgoodteam.ohgoodpay.common.entity.GradeEntity;
import com.ohgoodteam.ohgoodpay.pay.dto.GradeDTO;

public interface GradeService {
        // entity to dto
        default GradeDTO entityToDto(GradeEntity gradeEntity) {
            return GradeDTO.builder()
                .gradeName(gradeEntity.getGradeName())
                .limitPrice(gradeEntity.getLimitPrice())
                .pointPercent(gradeEntity.getPointPercent())
                .upgrade(gradeEntity.getUpgrade())
                .build();
        }
    
        //dto to entity
        default GradeEntity dtoToEntity(GradeDTO gradeDTO) {
            return GradeEntity.builder()
                .gradeName(gradeDTO.getGradeName())
                .limitPrice(gradeDTO.getLimitPrice())
                .pointPercent(gradeDTO.getPointPercent())
                .upgrade(gradeDTO.getUpgrade())
                .build();
        }
}
