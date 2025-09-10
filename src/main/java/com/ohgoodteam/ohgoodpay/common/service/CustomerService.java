package com.ohgoodteam.ohgoodpay.common.service;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerDTO;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.GradeEntity;

public interface CustomerService {
    
    // entity to dto
    default CustomerDTO entityToDto(CustomerEntity customerEntity) {
        return CustomerDTO.builder()
            .customerId(customerEntity.getCustomerId())
            .name(customerEntity.getName())
            .emailId(customerEntity.getEmailId())
            .birth(customerEntity.getBirth())
            .account(customerEntity.getAccount())
            .accountName(customerEntity.getAccountName())
            .point(customerEntity.getPoint())
            .isBlocked(customerEntity.isBlocked())
            .profileImg(customerEntity.getProfileImg())
            .nickname(customerEntity.getNickname())
            .introduce(customerEntity.getIntroduce())
            .score(customerEntity.getScore())
            .hobby(customerEntity.getHobby())
            .blockedCnt(customerEntity.getBlockedCnt())
            .extensionCnt(customerEntity.getExtensionCnt())
            .joinDate(customerEntity.getJoinDate())
            .isExtension(customerEntity.isExtension())
            .isAuto(customerEntity.isAuto())
            .gradePoint(customerEntity.getGradePoint())
            .gradeName(customerEntity.getGrade().getGradeName())
            .balance(customerEntity.getBalance())
            .build();
    }

    // dto to entity
    default CustomerEntity dtoToEntity(CustomerDTO customerDTO) {
        return CustomerEntity.builder()
            .customerId(customerDTO.getCustomerId())
            .name(customerDTO.getName())
            .emailId(customerDTO.getEmailId())
            .birth(customerDTO.getBirth())
            .account(customerDTO.getAccount())
            .accountName(customerDTO.getAccountName())
            .point(customerDTO.getPoint())
            .isBlocked(customerDTO.isBlocked())
            .profileImg(customerDTO.getProfileImg())
            .nickname(customerDTO.getNickname())
            .introduce(customerDTO.getIntroduce())
            .score(customerDTO.getScore())
            .hobby(customerDTO.getHobby())
            .blockedCnt(customerDTO.getBlockedCnt())
            .extensionCnt(customerDTO.getExtensionCnt())
            .joinDate(customerDTO.getJoinDate())
            .isExtension(customerDTO.isExtension())
            .isAuto(customerDTO.isAuto())
            .gradePoint(customerDTO.getGradePoint())
            .grade(GradeEntity.builder().gradeName(customerDTO.getGradeName()).build())
            .balance(customerDTO.getBalance())
            .build();
    }
}
