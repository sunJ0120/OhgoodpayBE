package com.ohgoodteam.ohgoodpay.common.service;

import org.springframework.stereotype.Service;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerDTO;
import com.ohgoodteam.ohgoodpay.common.dto.MypageDTO;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.GradeEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.pay.dto.GradeDTO;
import com.ohgoodteam.ohgoodpay.pay.repository.GradeRepository;
import com.ohgoodteam.ohgoodpay.pay.service.GradeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final CustomerRepository customerRepository;
    private final GradeRepository gradeRepository;
    private final CustomerService customerService;
    private final GradeService gradeService;
    
    @Override
    public MypageDTO getMypageInfo(Long customerId) {
        CustomerEntity customer = customerRepository.findByCustomerId(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        CustomerDTO customerDTO = customerService.entityToDto(customer);

        GradeEntity grade = gradeRepository.findByGradeName(customerDTO.getGradeName());
        GradeDTO gradeDTO = gradeService.entityToDto(grade);

        MypageDTO mypageDTO = MypageDTO.builder()
            .customerId(customerDTO.getCustomerId())
            .name(customerDTO.getName())
            .emailId(customerDTO.getEmailId())
            .account(customerDTO.getAccount())
            .accountName(customerDTO.getAccountName())
            .point(customerDTO.getPoint())
            .gradePoint(customerDTO.getGradePoint())
            .gradeName(customerDTO.getGradeName())
            .limitPrice(gradeDTO.getLimitPrice())
            .pointPercent(gradeDTO.getPointPercent())
            .build();

        return mypageDTO;
    }
}
