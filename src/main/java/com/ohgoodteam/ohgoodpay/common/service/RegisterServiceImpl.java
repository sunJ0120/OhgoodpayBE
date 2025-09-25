package com.ohgoodteam.ohgoodpay.common.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerDTO;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean register(CustomerDTO customerDTO) {
        if (checkEmail(customerDTO.getEmailId())) {
            return false;
        }
    
        customerDTO.setPwd(passwordEncoder.encode(customerDTO.getPwd()));
        customerDTO.setJoinDate(LocalDateTime.now());
        customerDTO.setGradePoint(0);
        customerDTO.setBlockedCnt(0);
        customerDTO.setExtensionCnt(0);
        customerDTO.setScore(0);
        customerDTO.setHobby("");
        customerDTO.setIntroduce("");
        customerDTO.setNickname("");
        customerDTO.setProfileImg("");
        customerDTO.setPoint(0);
        customerDTO.setGradeName("bronze");
        customerDTO.setBalance(100000);
        customerDTO.setBlocked(false);
        customerDTO.setExtension(false);
        customerDTO.setAuto(false);

        CustomerEntity customer = customerRepository.save(customerService.dtoToEntity(customerDTO));

        if (customer != null) {
            return true;
        }   

        return false;
    }

    public boolean checkEmail(String email) {
        CustomerEntity customer = customerRepository.findByEmailId(email);
        if (customer != null) {
            return true;
        }
        return false;
    }
}
