package com.ohgoodteam.ohgoodpay.security.service;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerDTO;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.common.service.CustomerService;
import com.ohgoodteam.ohgoodpay.security.dto.ApiCustomerDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiCustomerDetailsService implements UserDetailsService{

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomerEntity customer = customerRepository.findByEmailId(username);
        if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (customer.isBlocked()) {
            throw new UsernameNotFoundException("User is blocked");
        }

        ApiCustomerDTO apiCustomerDTO = new ApiCustomerDTO(String.valueOf(customer.getCustomerId()), customer.getPwd(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        
        return apiCustomerDTO;
    }

}
