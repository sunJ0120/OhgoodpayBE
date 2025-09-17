package com.ohgoodteam.ohgoodpay.common.service;

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

    @Override
    public boolean register(CustomerDTO customerDTO) {
        if (checkEmail(customerDTO.getEmailId())) {
            return false;
        }
        
        customerDTO.setGradeName("bronze");
        customerDTO.setBalance(100000);

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
